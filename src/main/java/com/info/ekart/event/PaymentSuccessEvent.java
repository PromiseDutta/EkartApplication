
package com.info.ekart.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentSuccessEvent {

	private Integer transactionId;
	private Long orderId;
	private String customerEmailId;
	private BigDecimal amount;
	private LocalDateTime timestamp;

	
	  // 🔥 REQUIRED for Kafka deserialization
    public PaymentSuccessEvent() {
    }

	public PaymentSuccessEvent(Integer transactionId, Long orderId, String customerEmailId, BigDecimal amount,
			LocalDateTime timestamp) {
		super();
		this.transactionId = transactionId;
		this.orderId = orderId;
		this.customerEmailId = customerEmailId;
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getCustomerEmailId() {
		return customerEmailId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setCustomerEmailId(String customerEmailId) {
		this.customerEmailId = customerEmailId;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
