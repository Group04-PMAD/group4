package com.dbtraining.reconx.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class ReconMetrics {

    private final Timer reconciliationTimer;

    public ReconMetrics(MeterRegistry registry) {
        this.reconciliationTimer = Timer.builder("reconciliation_duration_seconds")
                .description("Wall time of reconciliation engine runs")
                .publishPercentileHistogram() // Required to generate _bucket series in Prometheus
                .publishPercentiles(0.5, 0.95, 0.99) // Pre-computes these percentiles client-side
                .register(registry);
    }

    public Timer reconciliationTimer() {
        return this.reconciliationTimer;
    }
}