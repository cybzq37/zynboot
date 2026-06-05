#!/bin/bash
# ============================================================
# PostgreSQL 自动备份脚本
#
# 用法:
#   bash deploy/middleware/scripts/backup.sh              # 执行备份
#   bash deploy/middleware/scripts/backup.sh --restore FILE # 恢复
#
# 环境变量:
#   BACKUP_DIR    备份目录（默认 ./backups/postgres）
#   BACKUP_KEEP   保留天数（默认 7）
#   PG_CONTAINER  PostgreSQL 容器名（默认 postgres）
#   PG_USER       数据库用户（默认 postgres）
#   PG_DATABASE   数据库名（默认 zyn_base）
# ============================================================
set -euo pipefail

BACKUP_DIR="${BACKUP_DIR:-./backups/postgres}"
BACKUP_KEEP="${BACKUP_KEEP:-7}"
PG_CONTAINER="${PG_CONTAINER:-postgres}"
PG_USER="${PG_USER:-postgres}"
PG_DATABASE="${PG_DATABASE:-zyn_base}"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

timestamp() {
  date '+%Y%m%d_%H%M%S'
}

# ============================================================
# 备份
# ============================================================
do_backup() {
  mkdir -p "$BACKUP_DIR"

  local ts
  ts=$(timestamp)
  local file="${BACKUP_DIR}/${PG_DATABASE}_${ts}.sql.gz"

  echo -e "${YELLOW}[$(date '+%H:%M:%S')]${NC} 开始备份 ${PG_DATABASE} ..."

  if ! docker exec "$PG_CONTAINER" pg_isready -U "$PG_USER" > /dev/null 2>&1; then
    echo -e "${RED}错误:${NC} PostgreSQL 容器 ${PG_CONTAINER} 不可用"
    exit 1
  fi

  docker exec "$PG_CONTAINER" pg_dump -U "$PG_USER" -d "$PG_DATABASE" --no-owner --no-privileges \
    | gzip > "$file"

  local size
  size=$(du -h "$file" | cut -f1)
  echo -e "${GREEN}✓${NC} 备份完成: ${file} (${size})"

  # 清理过期备份
  local deleted=0
  while IFS= read -r old_file; do
    rm -f "$old_file"
    ((deleted++))
  done < <(find "$BACKUP_DIR" -name "${PG_DATABASE}_*.sql.gz" -mtime "+${BACKUP_KEEP}" -type f 2>/dev/null)

  if [ "$deleted" -gt 0 ]; then
    echo -e "${YELLOW}清理${NC} 已删除 ${deleted} 个过期备份（>${BACKUP_KEEP} 天）"
  fi
}

# ============================================================
# 恢复
# ============================================================
do_restore() {
  local file="$1"

  if [ ! -f "$file" ]; then
    echo -e "${RED}错误:${NC} 备份文件不存在: ${file}"
    exit 1
  fi

  echo -e "${YELLOW}警告:${NC} 即将恢复数据库 ${PG_DATABASE}，现有数据将被覆盖！"
  echo -e "文件: ${file}"
  read -rp "确认恢复? [y/N] " confirm
  if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
    echo "已取消"
    exit 0
  fi

  echo -e "${YELLOW}[$(date '+%H:%M:%S')]${NC} 开始恢复 ..."

  docker exec -i "$PG_CONTAINER" psql -U "$PG_USER" -d postgres -c "DROP DATABASE IF EXISTS ${PG_DATABASE};"
  docker exec -i "$PG_CONTAINER" psql -U "$PG_USER" -d postgres -c "CREATE DATABASE ${PG_DATABASE};"

  gunzip -c "$file" | docker exec -i "$PG_CONTAINER" psql -U "$PG_USER" -d "$PG_DATABASE" > /dev/null 2>&1

  echo -e "${GREEN}✓${NC} 恢复完成"
}

# ============================================================
# 主入口
# ============================================================
case "${1:-}" in
  --restore)
    if [ -z "${2:-}" ]; then
      echo "用法: $0 --restore <backup_file>"
      exit 1
    fi
    do_restore "$2"
    ;;
  *)
    do_backup
    ;;
esac
