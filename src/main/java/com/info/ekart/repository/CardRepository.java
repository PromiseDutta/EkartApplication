package com.info.ekart.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.info.ekart.dto.PaymentThrough;
import com.info.ekart.entity.Card;

public interface CardRepository extends JpaRepository<Card, Integer> {

	List<Card> findByCustomer_EmailId(String emailId);

	List<Card> findByCustomer_EmailIdAndCardType(String emailId, PaymentThrough cardType);

}
