package com.info.ekart.entity;

import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.info.ekart.dto.Role;

@Entity
@Table(name = "EK_CUSTOMER")
public class Customer {

	@Id
	@Column(name = "EMAIL_ID", length = 50)
	private String emailId;

	@Column(name = "NAME", nullable = false, length = 50)
	private String name;

	@Column(name = "PASSWORD", nullable = false, length = 70)
	private String password;

	@Column(name = "PHONE_NUMBER", nullable = false, length = 10, unique = true)
	private String phoneNumber;

	@Column(name = "ADDRESS", length = 500)
	private String address;
	
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;


	/*
	 * One Customer has one Cart
	 * FK exists in EK_CUSTOMER_CART as CUSTOMER_EMAIL_ID
	 */
	@OneToOne(mappedBy = "customer", fetch = FetchType.LAZY)
	@JsonIgnore
	private CustomerCart cart;

	/*
	 * One Customer can place many Orders
	 * FK exists in EK_ORDER as CUSTOMER_EMAIL_ID
	 */
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Order> orders;

	/*
	 * One Customer can have multiple Cards
	 * FK exists in EK_CARD as CUSTOMER_EMAIL_ID
	 */
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Card> cards;

	// ================= Getters & Setters =================

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getName() {
		return name;
	}

	public CustomerCart getCart() {
		return cart;
	}

	public void setCart(CustomerCart cart) {
		this.cart = cart;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	
}
