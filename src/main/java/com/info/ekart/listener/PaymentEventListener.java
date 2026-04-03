package com.info.ekart.listener;

import com.info.ekart.event.PaymentSuccessEvent;
import com.info.ekart.kafka.PaymentKafkaProducer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

/**
 * This class listens to Spring domain events related to payment success.
 *
 * IMPORTANT:
 * This is NOT a Kafka consumer.
 * This is a Spring internal event listener.
 *
 * Purpose:
 * 1. Wait until the database transaction is successfully committed.
 * 2. Only AFTER commit, send the event to Kafka.
 *
 * Why needed?
 * If we send Kafka message before DB commit and transaction rolls back,
 * we would send incorrect "payment success" message.
 *
 * This class ensures strong consistency between DB and Kafka.
 */
@Component
public class PaymentEventListener {

    /**
     * Kafka producer used to send event to Kafka topic.
     * (We will create this class in next step.)
     */
    private final PaymentKafkaProducer kafkaProducer;

    /**
     * Constructor injection of Kafka producer.
     * Spring automatically injects the bean.
     */
    public PaymentEventListener(PaymentKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * This method listens for PaymentSuccessEvent.
     *
     * VERY IMPORTANT:
     * phase = AFTER_COMMIT means:
     *
     * - If transaction commits successfully → this method runs.
     * - If transaction rolls back → this method does NOT run.
     *
     * This guarantees:
     * ✔ No Kafka event if DB fails
     * ✔ Data consistency maintained
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccess(PaymentSuccessEvent event) {

        // Forward the event to Kafka after successful DB commit
        kafkaProducer.sendPaymentSuccessEvent(event);
    }
}