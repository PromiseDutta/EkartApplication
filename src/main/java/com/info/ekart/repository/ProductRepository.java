package com.info.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.info.ekart.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
