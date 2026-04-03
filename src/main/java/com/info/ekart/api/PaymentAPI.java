package com.info.ekart.api;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.info.ekart.dto.request.AddCardRequestDTO;
import com.info.ekart.dto.request.MakePaymentRequestDTO;
import com.info.ekart.dto.response.CardResponseDTO;
import com.info.ekart.exception.EKartException;
import com.info.ekart.service.PaymentService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/payment-api")
@Validated
public class PaymentAPI {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private Environment environment;

    Log logger = LogFactory.getLog(PaymentAPI.class);

    // ================= ADD NEW CARD =================

    @PostMapping("/customer/{customerEmailId:.+}/cards")
    public ResponseEntity<String> addNewCard(
           
               @Email( message = "{invalid.email.format}")
            
            @PathVariable String customerEmailId,

            @Valid @RequestBody AddCardRequestDTO request)
            throws EKartException, NoSuchAlgorithmException {

        logger.info("Adding new card for: " + customerEmailId);

        Integer cardId =
                paymentService.addCustomerCard(customerEmailId, request);

        String message =
                environment.getProperty("PaymentAPI.NEW_CARD_ADDED_SUCCESS")
                + cardId;

        return new ResponseEntity<>(message.trim(), HttpStatus.OK);
    }

    // ================= GET CARDS BY TYPE =================

    @GetMapping("/customer/{customerEmailId:.+}/card-type/{cardType}")
    public ResponseEntity<List<CardResponseDTO>> getCardsOfCustomer(
            @PathVariable String customerEmailId,
            @PathVariable String cardType)
            throws EKartException {

        logger.info("Fetching cards of type " + cardType + " for " + customerEmailId);

        List<CardResponseDTO> cards =
                paymentService.getCustomerCardOfCardType(customerEmailId, cardType);

        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    // ================= MAKE PAYMENT =================

    @PostMapping("/customer/{customerEmailId:.+}/order/{orderId}")
    public ResponseEntity<String> payForOrder(
            @Email(message = "{invalid.email.format}" )
            @PathVariable String customerEmailId,

            @PathVariable Long orderId,

            @Valid @RequestBody MakePaymentRequestDTO request)
            throws EKartException, NoSuchAlgorithmException {

        logger.info("Processing payment for order " + orderId);

        String response =paymentService.makePayment(
                        customerEmailId,
                        orderId,
                        request.getCardId(),
                        request.getCvv()
                );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
