package com.info.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.info.ekart.entity.CartProduct;

public interface CartProductRepository extends JpaRepository<CartProduct, Integer> {

}
