#!/bin/sh
# ----------------------------------------------------------
# 应用启动脚本（容器内执行）
#
# 环境变量:
#   SPRING_ACTIVE_PROFILES  - Spring Profile（默认 prod）
#   JVM_XMS          - 初始堆大小（默认 512m）
#   JVM_XMX          - 最大堆大小（默认 512m）
#   JVM_OPTS         - 额外 JVM 参数
#   SW_ENABLED       - 是否启用 SkyWalking（默认 true）
#   SW_SERVICE_NAME  - SkyWalking 服务名（默认读取 spring.application.name）
#   OAP_ADDRESS      - SkyWalking OAP 地址（默认 skywalking-oap:11800）
# ----------------------------------------------------------
set -e

SPRING_ACTIVE_PROFILES=${SPRING_ACTIVE_PROFILES:-prod}
JVM_XMS=${JVM_XMS:-512m}
JVM_XMX=${JVM_XMX:-512m}
SW_ENABLED=${SW_ENABLED:-true}
OAP_ADDRESS=${OAP_ADDRESS:-skywalking-oap:11800}

APP_JAR=/app/app.jar

echo "============================================"
echo "  Starting: $(basename $APP_JAR)"
echo "  Profile:  $SPRING_ACTIVE_PROFILES"
echo "  JVM:      -Xms$JVM_XMS -Xmx$JVM_XMX"
echo "  SkyWalking: $SW_ENABLED ($OAP_ADDRESS)"
echo "  Timezone: $(date +%Z)"
echo "============================================"

SW_AGENT_OPTS=""
if [ "$SW_ENABLED" = "true" ] && [ -f /app/skywalking-agent/agent/skywalking-agent.jar ]; then
  SW_AGENT_OPTS="\
    -javaagent:/app/skywalking-agent/agent/skywalking-agent.jar \
    -Dskywalking.agent.service_name=${SW_SERVICE_NAME:-unknown} \
    -Dskywalking.collector.backend_service=${OAP_ADDRESS}"
fi

exec java \
  -Xms$JVM_XMS \
  -Xmx$JVM_XMX \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Djava.security.egd=file:/dev/./urandom \
  -Dfile.encoding=UTF-8 \
  -Duser.timezone=Asia/Shanghai \
  $SW_AGENT_OPTS \
  $JVM_OPTS \
  -jar "$APP_JAR" \
  --spring.profiles.active=$SPRING_ACTIVE_PROFILES
