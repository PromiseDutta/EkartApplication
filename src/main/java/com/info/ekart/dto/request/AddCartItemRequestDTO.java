package com.info.ekart.dto.request;

import jakarta.validation.constraints.*;

public class AddCartItemRequestDTO {

    @NotNull(message = "{product.absent}")
    private Integer productId;

    @NotNull(message = "{cartproduct.invalid.quantity}")
    @Positive(message = "{cartproduct.invalid.quantity}")
    private Integer quantity;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
