package com.info.ekart.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "EK_CUSTOMER_CART")
public class CustomerCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CART_ID")
	private Integer cartId;

	/*
	 * Many carts belong to one customer (logically 1:1 but safe to map as ManyToOne)
	 * FK column exists in this table: CUSTOMER_EMAIL_ID
	 */
	@OneToOne
	@JoinColumn(name = "CUSTOMER_EMAIL_ID")
	private Customer customer;

	/*
	 * One cart can contain multiple CartProduct entries
	 * FK exists in EK_CART_PRODUCT table as CART_ID
	 */
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<CartProduct> cartProducts = new HashSet<>();


	// ================= Getters & Setters =================

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Set<CartProduct> getCartProducts() {
		return cartProducts;
	}

	public void setCartProducts(Set<CartProduct> cartProducts) {
		this.cartProducts = cartProducts;
	}
}
