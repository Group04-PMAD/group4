package com.dbtraining.reconx.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TradeMetrics {

    private final Counter tradeCreated;
    private final DistributionSummary tradeValue; // TICKET-ADV086

    public TradeMetrics(MeterRegistry registry) {
        // From TICKET-ADV083
        this.tradeCreated = Counter.builder("trade_created_total")
                .description("Total trades created")
                .register(registry);

        // From TICKET-ADV086: Record trade sizes
        this.tradeValue = DistributionSummary.builder("trade_value_total")
                .description("Distribution of trade notional values")
                .baseUnit("USD") // Setting the base unit makes it explicit in Prometheus
                .publishPercentileHistogram() // Required for Grafana heatmap/percentiles
                .register(registry);
    }

    public void incrementTradeCreated() { 
        tradeCreated.increment(); 
    }

    // Add this method to record the value of the trade
    public void recordTradeValue(double value) { 
        tradeValue.record(value); 
    }
}