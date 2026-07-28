package com.dbtraining.reconx.model;

import java.math.BigDecimal;

/**
 * ============================================================================
 * ReconciliationRule enum with configurable thresholds
 * ============================================================================
 */
public enum ReconciliationRule {

    EXACT(BigDecimal.ZERO, BigDecimal.ZERO),
    PRICE_TOLERANCE_1PCT(new BigDecimal("0.01"), BigDecimal.ZERO),
    PRICE_TOLERANCE_50BPS(new BigDecimal("0.005"), BigDecimal.ZERO),
    QTY_TOLERANCE_5UNITS(BigDecimal.ZERO, new BigDecimal("5")),
    LOOSE(new BigDecimal("0.05"), new BigDecimal("10"));

    private final BigDecimal priceTolerancePct;
    private final BigDecimal qtyToleranceAbs;

    ReconciliationRule(BigDecimal priceTolerancePct, BigDecimal qtyToleranceAbs) {
        this.priceTolerancePct = priceTolerancePct;
        this.qtyToleranceAbs   = qtyToleranceAbs;
    }

    public BigDecimal priceTolerancePct() { return priceTolerancePct; }
    public BigDecimal qtyToleranceAbs()   { return qtyToleranceAbs; }

    /**
     * Decide whether two prices/quantities are within this rule's tolerance.
     * @return true if BOTH the price diff (as %) AND the qty diff (as abs)
     *         are within tolerance.
     */
    public boolean matches(BigDecimal internalPrice, BigDecimal internalQty,
                           BigDecimal externalPrice, BigDecimal externalQty) {
        
        BigDecimal priceDiff = internalPrice.subtract(externalPrice).abs();
        
        // Prevent divide-by-zero if internal price is exactly 0
        BigDecimal priceDiffPct = internalPrice.signum() == 0
                ? BigDecimal.ZERO
                : priceDiff.divide(internalPrice, 6, java.math.RoundingMode.HALF_UP);
                
        BigDecimal qtyDiff = internalQty.subtract(externalQty).abs();

        // Compare calculated drifts against the allowed tolerances
        boolean priceOk = priceDiffPct.compareTo(priceTolerancePct) <= 0;
        boolean qtyOk   = qtyDiff.compareTo(qtyToleranceAbs) <= 0;
        
        return priceOk && qtyOk;
    }
}