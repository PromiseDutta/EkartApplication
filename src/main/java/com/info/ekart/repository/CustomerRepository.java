package com.info.ekart.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.info.ekart.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByPhoneNumber(String phoneNumber);

}
