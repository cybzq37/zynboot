#!/bin/bash
# ============================================================
# 全栈集成测试：中间件 + 微服务 + 链路追踪
#
# 用法:
#   bash tests/test_all.sh              # 全部测试
#   bash tests/test_all.sh --mw         # 仅中间件
#   bash tests/test_all.sh --svc        # 仅微服务
#   bash tests/test_all.sh --trace      # 仅链路追踪
# ============================================================
set -e
unset http_proxy https_proxy HTTP_PROXY HTTPS_PROXY

PASS=0
FAIL=0
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

TEST_MW=true
TEST_SVC=true
TEST_TRACE=true

for arg in "$@"; do
  case "$arg" in
    --mw)    TEST_SVC=false; TEST_TRACE=false ;;
    --svc)   TEST_MW=false;  TEST_TRACE=false ;;
    --trace) TEST_MW=false;  TEST_SVC=false  ;;
  esac
done

pass() { ((PASS++)); echo -e "  ${GREEN}✓${NC} $1"; }
fail() { ((FAIL++)); echo -e "  ${RED}✗${NC} $1"; }
info() { echo -e "\n${YELLOW}[$1]${NC}"; }

# ============================================================
# 中间件测试
# ============================================================
if [ "$TEST_MW" = true ]; then

info "PostgreSQL"
result=$(docker exec postgres psql -U postgres -d zyn_base -t -c "SELECT 1;" 2>/dev/null | tr -d ' ')
[ "$result" = "1" ] && pass "连接查询: SELECT 1" || fail "连接查询失败"

info "Redis"
docker exec redis redis-cli -a 'Zyn@secure#99' SET zyn:test "hello" > /dev/null 2>&1
result=$(docker exec redis redis-cli -a 'Zyn@secure#99' GET zyn:test 2>/dev/null)
[ "$result" = "hello" ] && pass "读写: SET/GET" || fail "读写失败"
docker exec redis redis-cli -a 'Zyn@secure#99' DEL zyn:test > /dev/null 2>&1

info "Nacos"
# Nacos 3.x 健康检查端口 12800 (OAP 共用) 或检查容器状态
nacos_status=$(docker inspect --format='{{.State.Health.Status}}' nacos 2>/dev/null || echo "unknown")
[ "$nacos_status" = "healthy" ] && pass "Nacos 容器健康: $nacos_status" || fail "Nacos 容器状态: $nacos_status"

info "Kafka"
echo "zyn-test" | docker exec -i kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9092 --topic zyn-test > /dev/null 2>&1 && \
  pass "生产消息" || fail "生产消息失败"

sleep 1
result=$(docker exec kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic zyn-test --from-beginning --timeout-ms 5000 2>/dev/null)
echo "$result" | grep -q "zyn-test" && pass "消费消息" || fail "消费消息失败"
docker exec kafka /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 --delete --topic zyn-test > /dev/null 2>&1

info "Elasticsearch"
code=$(curl -sf -o /dev/null -w '%{http_code}' -u elastic:Zyn@secure#99 http://localhost:9200/_cluster/health 2>/dev/null || echo '000')
[ "$code" = "200" ] && pass "集群健康: $code" || fail "集群健康检查失败: $code"

info "SkyWalking OAP"
code=$(curl -sf -o /dev/null -w '%{http_code}' http://localhost:12800/healthcheck 2>/dev/null || echo '000')
[ "$code" = "200" ] && pass "OAP 健康: $code" || fail "OAP 健康检查失败: $code"

info "SkyWalking UI"
code=$(curl -sf -o /dev/null -w '%{http_code}' http://localhost:8088/ 2>/dev/null || echo '000')
[ "$code" = "200" ] && pass "UI 访问: $code" || fail "UI 访问失败: $code"

fi

# ============================================================
# 微服务测试
# ============================================================
if [ "$TEST_SVC" = true ]; then

info "System 服务 - 登录"
login_resp=$(curl -sf -X POST http://localhost:28081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"root","password":"Zyn@secure#99"}' 2>/dev/null)
token=$(echo "$login_resp" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)
[ -n "$token" ] && pass "登录成功，获取 token" || fail "登录失败"

info "System 服务 - 用户列表"
code=$(curl -sf -o /dev/null -w '%{http_code}' \
  "http://localhost:28081/api/v1/user?pageNum=1&pageSize=5" \
  -H "Authorization: $token" 2>/dev/null || echo '000')
[ "$code" = "200" ] && pass "用户列表: $code" || fail "用户列表失败: $code"

info "Gateway 路由 - 登录"
gw_login=$(curl -sf -X POST http://localhost:28000/sys/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"root","password":"Zyn@secure#99"}' 2>/dev/null)
gw_token=$(echo "$gw_login" | grep -o '"token":"[^"]*"' | head -1 | cut -d'"' -f4)
[ -n "$gw_token" ] && pass "Gateway 登录成功" || fail "Gateway 登录失败"

info "Gateway 路由 - 用户列表"
code=$(curl -sf -o /dev/null -w '%{http_code}' \
  "http://localhost:28000/sys/api/v1/user?pageNum=1&pageSize=5" \
  -H "Authorization: $gw_token" 2>/dev/null || echo '000')
[ "$code" = "200" ] && pass "Gateway 用户列表: $code" || fail "Gateway 用户列表失败: $code"

info "Gateway 路由 - Demo 服务"
code=$(curl -sf -o /dev/null -w '%{http_code}' \
  http://localhost:28000/demo/api/v1/demo/health 2>/dev/null || echo '000')
[ "$code" = "200" ] && pass "Gateway Demo 路由: $code" || fail "Gateway Demo 路由失败: $code"

info "Nacos 服务注册"
echo -e "  ${YELLOW}⚠${NC} Nacos 3.x API 不兼容，暂未集成服务注册，跳过"

fi

# ============================================================
# 链路追踪测试
# ============================================================
if [ "$TEST_TRACE" = true ]; then

info "SkyWalking - 服务列表"
# 触发一次请求产生 trace
curl -sf -X POST http://localhost:28000/sys/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"root","password":"Zyn@secure#99"}' > /dev/null 2>&1
sleep 5

# 查询 OAP GraphQL 获取服务列表
oap_resp=$(curl -sf -m 5 -X POST http://localhost:12800/graphql \
  -H 'Content-Type: application/json' \
  -d '{"query":"{getAllServices(duration:{start:\"2025-01-01 0000\",end:\"2026-12-31 0000\",step:MINUTE}){key: id label: name}}"}' 2>/dev/null)

if echo "$oap_resp" | grep -q "zyn-gateway"; then
  pass "zyn-gateway trace 已上报"
else
  fail "zyn-gateway trace 未找到"
fi
if echo "$oap_resp" | grep -q "zyn-sys"; then
  pass "zyn-sys trace 已上报"
else
  fail "zyn-sys trace 未找到"
fi
if echo "$oap_resp" | grep -q "zyn-demo"; then
  pass "zyn-demo trace 已上报"
else
  fail "zyn-demo trace 未找到"
fi

info "SkyWalking - 链路查询"
trace_resp=$(curl -sf -m 5 -X POST http://localhost:12800/graphql \
  -H 'Content-Type: application/json' \
  -d '{"query":"{version}"}' 2>/dev/null)

if echo "$trace_resp" | grep -q "version"; then
  pass "OAP GraphQL 可用"
else
  echo -e "  ${YELLOW}⚠${NC} OAP GraphQL 不可用"
fi

fi

# ============================================================
# 汇总
# ============================================================
echo ""
echo "=============================="
echo -e "  ${GREEN}通过: ${PASS}${NC}  ${RED}失败: ${FAIL}${NC}"
echo "=============================="

[ $FAIL -eq 0 ] && echo -e "  ${GREEN}全部通过${NC}" || echo -e "  ${RED}存在失败项${NC}"
exit $FAIL
