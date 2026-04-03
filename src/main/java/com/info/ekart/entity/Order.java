package com.info.ekart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.info.ekart.dto.OrderStatus;
import com.info.ekart.dto.PaymentThrough;

@Entity
@Table(name = "EK_ORDER")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ORDER_ID")
	private Long orderId;

	/*
	 * Many orders belong to one customer
	 * FK column: CUSTOMER_EMAIL_ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_EMAIL_ID")
	private Customer customer;

	@Column(name = "DATE_OF_ORDER", nullable = false)
	private LocalDateTime dateOfOrder;

	@Column(name = "DISCOUNT", precision = 5, scale = 2)
	private BigDecimal discount;

	@Column(name = "TOTAL_PRICE", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "ORDER_STATUS", nullable = false,columnDefinition = "varchar(20)")
	private OrderStatus orderStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYMENT_THROUGH", nullable = false,columnDefinition = "varchar(20)")
	private PaymentThrough paymentThrough;

	@Column(name = "DATE_OF_DELIVERY")
	private LocalDateTime dateOfDelivery;

	@Column(name = "DELIVERY_ADDRESS", length = 500)
	private String deliveryAddress;

	/*
	 * One order has multiple ordered products
	 * FK exists in EK_ORDERED_PRODUCT table as ORDER_ID
	 */
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<OrderedProduct> orderedProducts= new ArrayList<>();

	// ================= Getters & Setters =================

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LocalDateTime getDateOfOrder() {
		return dateOfOrder;
	}

	public void setDateOfOrder(LocalDateTime dateOfOrder) {
		this.dateOfOrder = dateOfOrder;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public PaymentThrough getPaymentThrough() {
		return paymentThrough;
	}

	public void setPaymentThrough(PaymentThrough paymentThrough) {
		this.paymentThrough = paymentThrough;
	}

	public LocalDateTime getDateOfDelivery() {
		return dateOfDelivery;
	}

	public void setDateOfDelivery(LocalDateTime dateOfDelivery) {
		this.dateOfDelivery = dateOfDelivery;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public List<OrderedProduct> getOrderedProducts() {
		return orderedProducts;
	}

	public void setOrderedProducts(List<OrderedProduct> orderedProducts) {
		this.orderedProducts = orderedProducts;
	}
}
