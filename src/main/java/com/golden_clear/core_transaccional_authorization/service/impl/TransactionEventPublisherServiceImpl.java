package com.golden_clear.core_transaccional_authorization.service.impl;

import com.golden_clear.core_transaccional_authorization.dto.response.TransactionEvent;
import com.golden_clear.core_transaccional_authorization.service.TransactionEventPublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TransactionEventPublisherServiceImpl implements TransactionEventPublisherService {

    private static final String TOPIC = "transaction-events";
    private static final Logger log = LoggerFactory.getLogger(TransactionEventPublisherServiceImpl.class);

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionEventPublisherServiceImpl(KafkaTemplate<String, TransactionEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(TransactionEvent event) {

        CompletableFuture<SendResult<String, TransactionEvent>> future = kafkaTemplate.send(TOPIC,event.transactionId(),event);

        future.whenComplete((result, ex) -> {
           if(ex != null) {
               log.error("Fallo al publicar evento txn={}", event.transactionId(), ex);
           }else {
               log.info("Evento publicado txn={} partition={} offset={}",
                       event.transactionId(),
                       result.getRecordMetadata().partition(),
                       result.getRecordMetadata().offset());
           }
        });
    }
}
