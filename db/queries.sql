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

-- ============================================================================
-- TICKET-ADV011 — Recursive CTE: Trade Lifecycle Rollup
-- ============================================================================

WITH RECURSIVE trade_lifecycle AS (

    -- Base case: Execution
    SELECT
        t.id AS trade_id,
        t.trade_ref,
        1 AS stage,
        'EXECUTION' AS stage_name,
        t.created_at AS event_at,
        'EXECUTED' AS event_status
    FROM trades t
    WHERE t.deleted_at IS NULL

    UNION ALL

    -- Recursive step
    SELECT
        tl.trade_id,
        tl.trade_ref,
        tl.stage + 1,

        CASE tl.stage
            WHEN 1 THEN 'CONFIRMATION'
            WHEN 2 THEN 'SETTLEMENT'
            WHEN 3 THEN 'RECON_BREAK'
            WHEN 4 THEN 'RESOLUTION'
        END AS stage_name,

        tl.event_at,

        CASE tl.stage
            WHEN 1 THEN 'CONFIRMED'
            WHEN 2 THEN 'SETTLED'
            WHEN 3 THEN 'OPEN_BREAK'
            WHEN 4 THEN 'RESOLVED'
        END AS event_status

    FROM trade_lifecycle tl
    WHERE tl.stage < 5
)

SELECT
    trade_id,
    trade_ref,
    stage,
    stage_name,
    event_at,
    event_status
FROM trade_lifecycle
ORDER BY trade_id, stage;