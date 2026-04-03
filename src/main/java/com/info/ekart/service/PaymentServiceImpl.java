package com.info.ekart.service;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.info.ekart.dto.OrderStatus;
import com.info.ekart.dto.PaymentThrough;
import com.info.ekart.dto.TransactionStatus;
import com.info.ekart.dto.request.AddCardRequestDTO;
import com.info.ekart.dto.request.PlaceOrderRequestDTO;
import com.info.ekart.dto.response.CardResponseDTO;
import com.info.ekart.dto.response.OrderResponseDTO;
import com.info.ekart.dto.response.OrderedItemResponseDTO;
import com.info.ekart.dto.response.ProductResponseDTO;
import com.info.ekart.entity.Card;
import com.info.ekart.entity.CartProduct;
import com.info.ekart.entity.Customer;
import com.info.ekart.entity.CustomerCart;
import com.info.ekart.entity.Order;
import com.info.ekart.entity.OrderedProduct;
import com.info.ekart.entity.Product;
import com.info.ekart.entity.Transaction;
import com.info.ekart.event.PaymentSuccessEvent;
import com.info.ekart.exception.EKartException;
import com.info.ekart.repository.CardRepository;
import com.info.ekart.repository.CartRepository;
import com.info.ekart.repository.CustomerRepository;
import com.info.ekart.repository.OrderRepository;
import com.info.ekart.repository.ProductRepository;
import com.info.ekart.repository.TransactionRepository;
import com.info.ekart.utility.HashingUtility;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

	
	private final CardRepository cardRepository;

	
	private final TransactionRepository transactionRepository;

	
	private final  CustomerRepository customerRepository;

	
	private final  OrderRepository orderRepository;
	
	
	private  final PasswordEncoder passwordEncoder;

	
	private final ApplicationEventPublisher eventPublisher;
	
	
	public PaymentServiceImpl(CardRepository cardRepository, TransactionRepository transactionRepository,
			CustomerRepository customerRepository, OrderRepository orderRepository, PasswordEncoder passwordEncoder,ApplicationEventPublisher eventPublisher ) {
		super();
		this.cardRepository = cardRepository;
		this.transactionRepository = transactionRepository;
		this.customerRepository = customerRepository;
		this.orderRepository = orderRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventPublisher = eventPublisher;
	}

	// ==========================
	// ADD CARD
	// ==========================

	@Override
	public Integer addCustomerCard(String customerEmailId, AddCardRequestDTO request)
			throws EKartException, NoSuchAlgorithmException {

		Customer customer = customerRepository.findById(customerEmailId)
				.orElseThrow(() -> new EKartException("PaymentService.CUSTOMER_NOT_FOUND"));

		Card card = new Card();
		card.setCustomer(customer);
		card.setCardNumber(request.getCardNumber());
		card.setCardType(PaymentThrough.valueOf(request.getCardType()));
		card.setNameOnCard(request.getNameOnCard());
		card.setExpiryDate(request.getExpiryDate());

		// Hash CVV
		//card.setCvv(HashingUtility.getHashValue(request.getCvv().toString()));
		card.setCvv(passwordEncoder.encode(request.getCvv()));

		cardRepository.save(card);

		return card.getCardId();
	}

	// ==========================
	// GET CARDS
	// ==========================

	@Override
	public List<CardResponseDTO> getCustomerCardOfCardType(String emailId, String cardType) throws EKartException {

		List<Card> cards = cardRepository.findByCustomer_EmailIdAndCardType(emailId, PaymentThrough.valueOf(cardType));

		if (cards.isEmpty())
			throw new EKartException("PaymentService.CARD_NOT_FOUND");

		List<CardResponseDTO> responses = new ArrayList<>();

		for (Card card : cards) {

			CardResponseDTO dto = new CardResponseDTO();
			dto.setCardId(card.getCardId());
			dto.setCardType(card.getCardType().name());
			dto.setNameOnCard(card.getNameOnCard());
			dto.setExpiryDate(card.getExpiryDate());

			// Mask card number (show only last 4 digits)
			String number = card.getCardNumber();
			String masked = "XXXX-XXXX-XXXX-" + number.substring(number.length() - 4);
			dto.setMaskedCardNumber(masked);

			responses.add(dto);
		}

		return responses;
	}

	// ==========================
	// MAKE PAYMENT
	// ==========================

	@Override
	public String makePayment(String customerEmailId,
	                          Long orderId,
	                          Integer cardId,
	                          String cvv)
	        throws EKartException, NoSuchAlgorithmException {

	    // ================= FETCH ORDER =================
	    // Retrieve the order using orderId.
	    // If the order does not exist, throw exception.
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new EKartException("PaymentService.ORDER_NOT_FOUND"));

	    // ================= VERIFY ORDER OWNERSHIP =================
	    // Ensure that the order belongs to the customer making the payment.
	    // Prevents unauthorized payment attempts on someone else's order.
	    if (!order.getCustomer().getEmailId().equals(customerEmailId))
	        throw new EKartException("PaymentService.ORDER_DOES_NOT_BELONGS");


	    // ================= CHECK PREVIOUS TRANSACTION STATUS =================
	    // Retrieve the most recent transaction attempt for this order.
	    // Business Rule:
	    // - If previous transaction was SUCCESS → block duplicate payment.
	    // - If previous transaction FAILED or does not exist → allow retry.
	    Transaction lastTransaction =
	            transactionRepository
	                    .findTopByOrder_OrderIdOrderByTransactionDateDesc(orderId)
	                    .orElse(null);

	    if (lastTransaction != null &&
	            lastTransaction.getTransactionStatus() == TransactionStatus.TRANSACTION_SUCCESS) {

	        throw new EKartException("PaymentService.TRANSACTION_ALREADY_DONE");
	    }


	    // ================= FETCH CARD DETAILS =================
	    // Retrieve the card using cardId.
	    // If card does not exist, throw exception.
	    Card card = cardRepository.findById(cardId)
	            .orElseThrow(() -> new EKartException("PaymentService.CARD_NOT_FOUND"));

	    // ================= VERIFY CARD OWNERSHIP =================
	    // Ensure the card belongs to the same customer making the payment.
	    if (!card.getCustomer().getEmailId().equals(customerEmailId))
	        throw new EKartException("PaymentService.CARD_DOES_NOT_BELONGS");

	    // ================= VALIDATE PAYMENT METHOD =================
	    // Ensure selected payment option in order matches card type.
	    // For example, DEBIT_CARD order cannot be paid using CREDIT_CARD.
	    if (!card.getCardType().equals(order.getPaymentThrough()))
	        throw new EKartException("PaymentService.PAYMENT_OPTION_SELECTED_NOT_MATCHING_CARD_TYPE");


	    // ================= HASH INPUT CVV =================
	    // Hash the CVV provided by the user to compare securely
	    // with the hashed CVV stored in database.
	  //  String hashedInputCvv = HashingUtility.getHashValue(cvv.toString());
	   // String hashedInputCvv =passwordEncoder.encode(cvv); 
	   // no need of hashing again as encoder do matches(rawInput, storedHash) not this -> matches(storedHash, newlyEncodedHash)
	    /// so in the time of match directly pass the raw password and the hased password



	    // ================= CREATE NEW TRANSACTION RECORD =================
	    // Create a new transaction attempt for this payment.
	    // Each attempt (success or failure) must be recorded.
	    Transaction transaction = new Transaction();
	    transaction.setOrder(order);
	    transaction.setCard(card);
	    transaction.setTransactionDate(LocalDateTime.now());
	    transaction.setTotalPrice(order.getTotalPrice());


	    // ================= VERIFY CVV =================
	    // If hashed CVV does not match stored CVV:
	    // - Mark transaction as FAILED
	    // - Save failure record
	    // - Throw exception
	    
	   // if (!card.getCvv().equals(hashedInputCvv))
	    
	    if (!passwordEncoder.matches(cvv,card.getCvv() )) {
	        transaction.setTransactionStatus(TransactionStatus.TRANSACTION_FAILED);
	        transactionRepository.save(transaction);
	        throw new EKartException("PaymentService.TRANSACTION_FAILED_CVV_NOT_MATCHING");
	    }


	    // ================= MARK TRANSACTION SUCCESS =================
	    // If CVV matches:
	    // - Mark transaction as SUCCESS
	    // - Persist transaction
	    transaction.setTransactionStatus(TransactionStatus.TRANSACTION_SUCCESS);
	    transactionRepository.save(transaction);


	    // ================= UPDATE ORDER STATUS =================
	    // Once payment succeeds, update order status to CONFIRMED.
	    order.setOrderStatus(OrderStatus.CONFIRMED);
	    // ⚠ Ideally we should save order explicitly:
	    // orderRepository.save(order);

	    
	 // Create event
	  PaymentSuccessEvent event=new PaymentSuccessEvent(transaction.getTransactionId(), order.getOrderId(), order.getCustomer().getEmailId(), order.getTotalPrice(), LocalDateTime.now());
	// Publish Spring event
	  eventPublisher.publishEvent(event);
	  
	  
	    // ================= RETURN SUCCESS MESSAGE =================
	    return "We have received the payment of Rs. "
	            + order.getTotalPrice()
	            + " for Order id "
	            + order.getOrderId();
	}

	
}
