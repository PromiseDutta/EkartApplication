package com.info.ekart.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {

    private Integer cartId;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public List<CartItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponseDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
