package com.info.ekart.kafka;

import com.info.ekart.event.PaymentSuccessEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka Producer class responsible for sending payment success events
 * to Kafka topic.
 *
 * This class acts as a bridge between our application and Kafka broker.
 *
 * Flow:
 * PaymentService → Spring Event → AFTER_COMMIT Listener →
 * → This Producer → Kafka Topic
 */
@Service
public class PaymentKafkaProducer {

    /**
     * Kafka topic name where payment success events will be published.
     * This topic must exist in Kafka broker.
     */
    private static final String TOPIC = "payment-success-topic";

    /**
     * KafkaTemplate is provided by Spring Kafka.
     * It is used to send messages to Kafka broker.
     *
     * <Key Type, Value Type>
     * Here:
     * Key = String
     * Value = PaymentSuccessEvent
     */
    private final KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate;

    /**
     * Constructor injection of KafkaTemplate.
     * Spring automatically provides this bean.
     */
    public PaymentKafkaProducer(KafkaTemplate<String, PaymentSuccessEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends PaymentSuccessEvent to Kafka topic.
     *
     * Internally:
     * 1. Event is serialized to JSON.
     * 2. JSON converted to byte[].
     * 3. Message sent to Kafka broker.
     * 4. Kafka stores it in topic partition.
     */
    public void sendPaymentSuccessEvent(PaymentSuccessEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}