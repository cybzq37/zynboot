# ============================================================
# ParadeDB PG16 + TimescaleDB + pgRouting + PostGIS + BM25 + pgvector
# ============================================================
FROM paradedb/paradedb:pg16

USER root

# 安装 TimescaleDB + pgRouting
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        postgresql-16-timescaledb \
        postgresql-16-pgrouting && \
    rm -rf /var/lib/apt/lists/*

USER postgres
