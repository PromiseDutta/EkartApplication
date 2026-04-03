package com.info.ekart.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "EK_CART_PRODUCT")
public class CartProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CART_PRODUCT_ID")
	private Integer cartProductId;

	/*
	 * Many CartProduct entries belong to one Cart
	 * FK column: CART_ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CART_ID", nullable = false)
	private CustomerCart cart;

	/*
	 * Many CartProduct entries refer to one Product
	 * FK column: PRODUCT_ID
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	private Product product;

	@Column(name = "QUANTITY")
	private Integer quantity;

	// ================= Getters & Setters =================

	public Integer getCartProductId() {
		return cartProductId;
	}

	public void setCartProductId(Integer cartProductId) {
		this.cartProductId = cartProductId;
	}

	public CustomerCart getCart() {
		return cart;
	}

	public void setCart(CustomerCart cart) {
		this.cart = cart;
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

	// ================= Equals & HashCode =================

	@Override
	public int hashCode() {
		return product != null ? product.getProductId().hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CartProduct)) return false;
		CartProduct other = (CartProduct) obj;
		if (this.product == null || other.product == null) return false;
		return this.product.getProductId()
				.equals(other.product.getProductId());
	}
}
