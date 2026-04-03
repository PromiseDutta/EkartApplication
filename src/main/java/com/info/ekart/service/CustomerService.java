package com.info.ekart.service;

import com.info.ekart.dto.request.ChangePasswordRequestDTO;
import com.info.ekart.dto.request.LoginRequestDTO;
import com.info.ekart.dto.request.RegisterRequestDTO;
import com.info.ekart.dto.request.UpdateAddressRequestDTO;
import com.info.ekart.dto.response.CustomerResponseDTO;
import com.info.ekart.exception.EKartException;

public interface CustomerService {

    String login(LoginRequestDTO request) throws EKartException;

    String register(RegisterRequestDTO request) throws EKartException;

    void changePassword(ChangePasswordRequestDTO request) throws EKartException;

    void updateShippingAddress(UpdateAddressRequestDTO updateAddress) throws EKartException;

    void deleteShippingAddress(String customerEmailId) throws EKartException;

    CustomerResponseDTO getCustomerByEmailId(String emailId) throws EKartException;
}
