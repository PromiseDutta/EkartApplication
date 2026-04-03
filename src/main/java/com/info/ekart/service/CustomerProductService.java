package com.info.ekart.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.info.ekart.dto.request.ProductCreateRequestDTO;
import com.info.ekart.dto.response.ProductResponseDTO;
import com.info.ekart.entity.Product;
import com.info.ekart.exception.EKartException;

import jakarta.validation.Valid;

public interface CustomerProductService {

    List<ProductResponseDTO> getAllProducts() throws EKartException;

    ProductResponseDTO getProductById(Integer productId) throws EKartException;

    void reduceAvailableQuantity(Integer productId, Integer quantity) throws EKartException;
    
    ProductResponseDTO addProduct( ProductCreateRequestDTO request) throws EKartException;
    
    void deleteProduct(Integer productId)  throws EKartException;
}
