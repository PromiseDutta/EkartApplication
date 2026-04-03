package com.info.ekart.dto.response;

public class OrderedItemResponseDTO {

    private Integer orderedProductId;
    private ProductResponseDTO product;
    private Integer quantity;
	public Integer getOrderedProductId() {
		return orderedProductId;
	}
	public void setOrderedProductId(Integer orderedProductId) {
		this.orderedProductId = orderedProductId;
	}
	public ProductResponseDTO getProduct() {
		return product;
	}
	public void setProduct(ProductResponseDTO product) {
		this.product = product;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

    // getters & setters
    
    
}
