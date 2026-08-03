package com.dbtraining.reconx.kafka;

import com.dbtraining.reconx.dto.TradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationConsumer.class);

    @KafkaListener(topics = "trade-events", groupId = "recon-service")
    public void onTradeEvent(TradeEvent event) {

        log.info("Recon-trigger received eventId={} ref={} type={}",
                event.eventId(),
                event.tradeRef(),
                event.eventType());

        switch (event.eventType()) {
            case TRADE_CREATED, TRADE_UPDATED ->
                    log.info("Scheduling reconciliation for {}", event.tradeRef());

            case TRADE_CANCELLED ->
                    log.info("Cancelling pending reconciliation for {}", event.tradeRef());
        }
    }
}
