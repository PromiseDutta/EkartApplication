package com.info.ekart.service;

import java.util.List;

import com.info.ekart.dto.request.PlaceOrderRequestDTO;
import com.info.ekart.dto.response.OrderResponseDTO;
import com.info.ekart.exception.EKartException;

public interface CustomerOrderService {

    Long placeOrder(String customerEmailId, PlaceOrderRequestDTO request)
            throws EKartException;

    OrderResponseDTO getOrderDetails(Long orderId)
            throws EKartException;

    List<OrderResponseDTO> findOrdersByCustomerEmailId(String emailId)
            throws EKartException;
}
