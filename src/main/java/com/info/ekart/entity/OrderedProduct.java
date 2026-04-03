package com.info.ekart.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "EK_ORDERED_PRODUCT")
public class OrderedProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ORDERED_PRODUCT_ID")
	private Integer orderedProductId;

	/*
	 * Many ordered products belong to one Order
	 * FK column: ORDER_ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_ID", nullable = false)
	private Order order;

	/*
	 * Many ordered products refer to one Product
	 * FK column: PRODUCT_ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;

	@Column(name = "QUANTITY")
	private Integer quantity;

	// ================= Getters & Setters =================

	public Integer getOrderedProductId() {
		return orderedProductId;
	}

	public void setOrderedProductId(Integer orderedProductId) {
		this.orderedProductId = orderedProductId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
