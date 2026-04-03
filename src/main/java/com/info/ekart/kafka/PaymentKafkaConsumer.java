package com.info.ekart.kafka;

import com.info.ekart.event.PaymentSuccessEvent;
import com.info.ekart.notification.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer class.
 *
 * This class listens to the Kafka topic "payment-success-topic".
 * Whenever a payment success event is published,
 * this consumer receives the message asynchronously.
 *
 * It then triggers NotificationService to send confirmation.
 */
@Service
public class PaymentKafkaConsumer {

    /**
     * Service responsible for sending email notification.
     */
    private final NotificationService notificationService;

    /**
     * Constructor injection of NotificationService.
     */
    public PaymentKafkaConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * This method is automatically triggered when
     * a new message arrives in the Kafka topic.
     *
     * topics = "payment-success-topic"
     * groupId = "notification-group"
     *
     * Kafka ensures:
     * - Only one consumer in same group processes each message.
     * - Processing happens asynchronously (separate thread).
     */
    
    @KafkaListener(topics = "payment-success-topic", groupId = "notification-group")
    public void consume(PaymentSuccessEvent event) {

        // Debug log to confirm message received
        System.out.println("Received payment event from Kafka: " + event.getOrderId());

        // Delegate email sending to NotificationService
        notificationService.sendPaymentConfirmation(event);
    }
}
