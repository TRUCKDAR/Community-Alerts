-- ═══════════════════════════════════════════════════════════════════
-- V2 — Ingesta idempotente de alertas registradas sin conexión por la PWA
-- ═══════════════════════════════════════════════════════════════════

-- (driver_id, client_event_id) hace que reenviar un lote no duplique alertas ni confirmaciones.
CREATE TABLE IF NOT EXISTS ingested_events (
    driver_id       VARCHAR(64)    NOT NULL,
    client_event_id UUID           NOT NULL,
    kind            VARCHAR(40)    NOT NULL,
    seq             BIGINT,
    occurred_at     TIMESTAMPTZ(6) NOT NULL,
    received_at     TIMESTAMPTZ(6) NOT NULL DEFAULT now(),
    status          VARCHAR(10)    NOT NULL,
    reason          VARCHAR(500),
    PRIMARY KEY (driver_id, client_event_id),
    CONSTRAINT ingested_events_status_check CHECK (status IN ('APPLIED', 'REJECTED'))
);
