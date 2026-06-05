-- ============================================================
-- Seata AT 模式 undo_log 表
-- 每个使用分布式事务的数据库都需要此表
-- ============================================================
CREATE TABLE IF NOT EXISTS undo_log (
    id            BIGSERIAL    NOT NULL,
    branch_id     BIGINT       NOT NULL,
    xid           VARCHAR(128) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info  BYTEA        NOT NULL,
    log_status    INT          NOT NULL,
    log_created   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    log_modified  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ext           VARCHAR(128),
    PRIMARY KEY (id),
    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)
);

COMMENT ON TABLE undo_log IS 'Seata AT mode undo log for distributed transaction rollback';
