package com.info.ekart.service;

import java.util.List;

import com.info.ekart.dto.request.AddCartItemRequestDTO;
import com.info.ekart.dto.response.CartResponseDTO;
import com.info.ekart.exception.EKartException;

public interface CustomerCartService {

	Integer addProductToCart(String customerEmailId, AddCartItemRequestDTO request)
 throws EKartException;

    CartResponseDTO getProductsFromCart(String customerEmailId) throws EKartException;

    void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity)
            throws EKartException;

    void deleteProductFromCart(String customerEmailId, Integer productId) throws EKartException;

    void deleteAllProductsFromCart(String customerEmailId) throws EKartException;
}
