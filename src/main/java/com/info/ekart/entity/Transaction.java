package com.info.ekart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.info.ekart.dto.TransactionStatus;

@Entity
@Table(name = "EK_TRANSACTION")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "TRANSACTION_ID")
	private Integer transactionId;

	/*
	 * One Transaction belongs to one Order
	 * FK column: ORDER_ID
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ID", nullable = false)
	private Order order;

	/*
	 * Many transactions can use same Card
	 * FK column: CARD_ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CARD_ID", nullable = false)
	private Card card;

	@Column(name = "TOTAL_PRICE", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalPrice;

	@Column(name = "TRANSACTION_DATE", nullable = false)
	private LocalDateTime transactionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "TRANSACTION_STATUS", length = 50,columnDefinition = "varchar(30)")
	private TransactionStatus transactionStatus;

	// ================= Getters & Setters =================

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	
}