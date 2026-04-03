package com.info.ekart.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.info.ekart.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer_EmailId(String customerEmailId);

}
