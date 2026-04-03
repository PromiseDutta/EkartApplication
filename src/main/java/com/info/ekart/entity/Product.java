package com.info.ekart.entity;

import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "EK_PRODUCT")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PRODUCT_ID")
	private Integer productId;

	@Column(name = "NAME", nullable = false, length = 500)
	private String name;

	@Column(name = "DESCRIPTION", nullable = false, length = 1000)
	private String description;

	@Column(name = "CATEGORY", nullable = false, length = 200)
	private String category;

	@Column(name = "BRAND", nullable = false, length = 250)
	private String brand;

	// BIGINT in DB → use Long
	@Column(name = "PRICE", nullable = false)
	private Long price;

	@Column(name = "QUANTITY", nullable = false)
	private Integer availableQuantity;

	/*
	 * One Product can be present in multiple CartProduct entries
	 * FK exists in EK_CART_PRODUCT table as PRODUCT_ID
	 */
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<CartProduct> cartProducts;

	/*
	 * One Product can be present in multiple OrderedProduct entries
	 * FK exists in EK_ORDERED_PRODUCT table as PRODUCT_ID
	 */
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<OrderedProduct> orderedProducts;

	// ================= Getters & Setters =================

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public List<CartProduct> getCartProducts() {
		return cartProducts;
	}

	public void setCartProducts(List<CartProduct> cartProducts) {
		this.cartProducts = cartProducts;
	}

	public List<OrderedProduct> getOrderedProducts() {
		return orderedProducts;
	}

	public void setOrderedProducts(List<OrderedProduct> orderedProducts) {
		this.orderedProducts = orderedProducts;
	}
}
