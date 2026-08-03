package com.dbtraining.reconx.kafka;

import static com.dbtraining.reconx.kafka.KafkaTopicsConfig.TRADE_EVENTS;

import com.dbtraining.reconx.dto.TradeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TradeEventProducer.class);

    private final KafkaTemplate<String, TradeEvent> template;

    public TradeEventProducer(KafkaTemplate<String, TradeEvent> template) {
        this.template = template;
    }

    public void publish(TradeEvent event) {
        template.send(TRADE_EVENTS, event.tradeRef(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error(
                                "Failed to publish TradeEvent eventId={} tradeRef={}",
                                event.eventId(),
                                event.tradeRef(),
                                ex
                        );
                    } else if (result != null) {
                        log.debug(
                                "Published TradeEvent tradeRef={} partition={} offset={}",
                                event.tradeRef(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}
