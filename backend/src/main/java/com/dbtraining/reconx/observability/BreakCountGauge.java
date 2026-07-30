package com.dbtraining.reconx.observability;

import com.dbtraining.reconx.repository.ReconBreakRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BreakCountGauge {

    public BreakCountGauge(MeterRegistry registry, ReconBreakRepository breakRepo) {
        // Register the Gauge. It will poll breakRepo.countByStatus("OPEN") on every scrape.
        Gauge.builder("recon_break_count", breakRepo, repo -> repo.countByStatus("OPEN"))
             .description("Open recon breaks")
             .register(registry);
    }
}