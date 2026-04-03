package com.info.ekart.notification;

import com.info.ekart.event.PaymentSuccessEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * NotificationService is responsible for sending
 * real email notifications to customers.
 *
 * It is triggered by Kafka Consumer after
 * payment success event is received.
 *
 * This class communicates with external SMTP server (e.g., Gmail).
 */
@Service
public class NotificationService {

    /**
     * JavaMailSender is provided by Spring Boot.
     * It handles connection to SMTP server and sending emails.
     */
    private final JavaMailSender mailSender;

    /**
     * Constructor injection of JavaMailSender.
     * Spring automatically configures this based on
     * spring.mail.* properties.
     */
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends payment confirmation email to customer.
     *
     * This method:
     * 1. Creates a simple text email
     * 2. Sets recipient, subject, and body
     * 3. Sends email via SMTP server
     *
     * @param event PaymentSuccessEvent containing order & transaction details
     */
    public void sendPaymentConfirmation(PaymentSuccessEvent event) {

        // Create a simple email message
        SimpleMailMessage message = new SimpleMailMessage();

        // Set recipient email (customer email from event)
        message.setTo(event.getCustomerEmailId());

        // Set email subject
        message.setSubject("Payment Confirmation - Ekart");

        // Set email body (plain text format)
        message.setText(
                "Hello,\n\n" +
                "Your payment was successful.\n\n" +
                "Order ID: " + event.getOrderId() + "\n" +
                "Transaction ID: " + event.getTransactionId() + "\n" +
                "Amount Paid: Rs. " + event.getAmount() + "\n\n" +
                "Thank you for shopping with Ekart!"
        );

        // Send email using SMTP server
        mailSender.send(message);
    }
}