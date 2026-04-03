package com.info.ekart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.info.ekart.entity.CustomerCart;

public interface CartRepository extends JpaRepository<CustomerCart, Integer> {

    Optional<CustomerCart> findByCustomerEmailId(String emailId);

}
