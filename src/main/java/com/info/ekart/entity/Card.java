package com.info.ekart.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.info.ekart.dto.PaymentThrough;

@Entity
@Table(name = "EK_CARD")
public class Card {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CARD_ID")
	private Integer cardId;

	/*
	 * CARD_TYPE has CHECK constraint
	 * ('DEBIT_CARD', 'CREDIT_CARD')
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "CARD_TYPE", nullable = false,columnDefinition = "varchar(20)", length = 11)
	private PaymentThrough cardType;

	@Column(name = "CARD_NUMBER", nullable = false, length = 16)
	private String cardNumber;

	@Column(name = "CVV", nullable = false, length = 70)
	private String cvv;

	@Column(name = "EXPIRY_DATE", nullable = false)
	private LocalDateTime expiryDate;

	@Column(name = "NAME_ON_CARD", nullable = false, length = 50)
	private String nameOnCard;

	/*
	 * Many cards belong to one customer
	 * FK column: CUSTOMER_EMAIL_ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_EMAIL_ID")
	private Customer customer;

	/*
	 * One card can have many transactions
	 * FK exists in EK_TRANSACTION table
	 */
	@OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
	@JsonIgnore//Prevent Infinite Recursion  You have a bi-directional relationship:Card → has transactions  Transaction → has card
	private List<Transaction> transactions;

	// ================= Getters & Setters =================

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public PaymentThrough getCardType() {
		return cardType;
	}

	public void setCardType(PaymentThrough cardType) {
		this.cardType = cardType;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getNameOnCard() {
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
}
