#!/bin/bash
# ============================================================
# 验证 PostgreSQL 镜像中的扩展
# 用法: bash verify-postgres.sh
# ============================================================

set -euo pipefail
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

pass() { echo -e "${GREEN}✓${NC} $1"; }
fail() { echo -e "${RED}✗${NC} $1"; }

echo "=== 验证 PostgreSQL 镜像扩展 ==="

# 构建镜像
echo "构建镜像..."
cd "$(dirname "$0")"
docker compose build postgres --quiet

# 启动临时容器
docker compose up -d postgres
sleep 5

# 等待 PostgreSQL 就绪
for i in $(seq 1 12); do
    if docker exec postgres pg_isready -U postgres > /dev/null 2>&1; then
        break
    fi
    sleep 2
done

echo ""
echo "--- 已安装的扩展 ---"
docker exec postgres psql -U postgres -d zyn_base -c "SELECT name, default_version, installed_version FROM pg_available_extensions WHERE name IN ('postgis', 'pg_search', 'vector', 'timescaledb', 'pg_cron', 'pgRouting') ORDER BY name;" 2>/dev/null || echo "(查询失败)"

echo ""
echo "--- 启用扩展测试 ---"

# PostGIS
if docker exec postgres psql -U postgres -d zyn_base -c "CREATE EXTENSION IF NOT EXISTS postgis; SELECT PostGIS_Version();" > /dev/null 2>&1; then
    pass "PostGIS"
else
    fail "PostGIS"
fi

# pg_search (BM25)
if docker exec postgres psql -U postgres -d zyn_base -c "CREATE EXTENSION IF NOT EXISTS pg_search;" > /dev/null 2>&1; then
    pass "pg_search (BM25)"
else
    fail "pg_search (BM25)"
fi

# pgvector
if docker exec postgres psql -U postgres -d zyn_base -c "CREATE EXTENSION IF NOT EXISTS vector; SELECT vector_dims('[1,2,3]'::vector);" > /dev/null 2>&1; then
    pass "pgvector"
else
    fail "pgvector"
fi

# TimescaleDB
if docker exec postgres psql -U postgres -d zyn_base -c "CREATE EXTENSION IF NOT EXISTS timescaledb; SELECT default_version FROM pg_available_extensions WHERE name='timescaledb';" > /dev/null 2>&1; then
    pass "TimescaleDB"
else
    fail "TimescaleDB"
fi

# pgRouting
if docker exec postgres psql -U postgres -d zyn_base -c "CREATE EXTENSION IF NOT EXISTS pgrouting; SELECT pgr_version();" > /dev/null 2>&1; then
    pass "pgRouting"
else
    fail "pgRouting"
fi

# pg_cron
if docker exec postgres psql -U postgres -d zyn_base -c "CREATE EXTENSION IF NOT EXISTS pg_cron;" > /dev/null 2>&1; then
    pass "pg_cron"
else
    fail "pg_cron"
fi

echo ""
echo "=== 验证完成 ==="

# 清理
docker compose down postgres
