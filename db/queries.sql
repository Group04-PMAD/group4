-- ============================================================================
-- TICKET-ADV008 — REFRESH the daily-summary materialised view (concurrent so it can
--         run while the dashboard is reading it)
-- ============================================================================
REFRESH MATERIALIZED VIEW CONCURRENTLY mv_daily_recon_summary;

-- ============================================================================
-- TICKET-ADV010 — VWAP per instrument per day (Window Function)
-- ============================================================================

SELECT
    t.trade_ref,
    t.instrument_id,
    t.trade_date,
    t.quantity,
    t.price,
    (t.quantity * t.price) AS notional,

    SUM(t.price * t.quantity)
        OVER (PARTITION BY t.instrument_id, t.trade_date)
    /
    NULLIF(
        SUM(t.quantity)
            OVER (PARTITION BY t.instrument_id, t.trade_date),
        0
    ) AS vwap,

    ROW_NUMBER() OVER (
        PARTITION BY t.instrument_id, t.trade_date
        ORDER BY t.created_at
    ) AS row_num,

    SUM(t.quantity) OVER (
        PARTITION BY t.instrument_id, t.trade_date
        ORDER BY t.created_at
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS cumulative_quantity

FROM trades t
WHERE t.deleted_at IS NULL
ORDER BY t.trade_date DESC, t.instrument_id;