package com.info.ekart.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductCreateRequestDTO {

    @Pattern(regexp = "([A-Za-z0-9-.])+(\\s[A-Za-z0-9-.]+)*",
            message = "{product.invalid.name}")
    private String name;

    @Size(min = 10, message = "{product.invalid.description}")
    private String description;

    @NotNull(message = "{product.category.absent}")
    private String category;

    @Size(min = 3, message = "{product.invalid.brand}")
    private String brand;

    @NotNull(message = "{product.invalid.price}")
    @Positive(message = "{product.invalid.price}")
    private Long price;

    @NotNull(message = "{product.invalid.quantity}")
    @PositiveOrZero(message = "{product.invalid.quantity}")
    private Integer availableQuantity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
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

	

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

    // getters & setters
    
}
