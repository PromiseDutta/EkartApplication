package com.info.ekart.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.info.ekart.dto.request.AddCardRequestDTO;
import com.info.ekart.dto.response.CardResponseDTO;
import com.info.ekart.exception.EKartException;

public interface PaymentService {

	// Add new card
	Integer addCustomerCard(String customerEmailId, AddCardRequestDTO request)
			throws EKartException, NoSuchAlgorithmException;

	// Get cards of specific type (DEBIT_CARD / CREDIT_CARD)
	List<CardResponseDTO> getCustomerCardOfCardType(String customerEmailId, String cardType) throws EKartException;

	// Make payment for order
	String makePayment(String customerEmailId, Long orderId, Integer cardId, String cvv)
			throws EKartException, NoSuchAlgorithmException;

}
