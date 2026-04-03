package com.info.ekart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.info.ekart.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    boolean existsByOrder_OrderId(Long orderId);
    
    Optional<Transaction>findTopByOrder_OrderIdOrderByTransactionDateDesc(Long orderId);
    /**
     * Fetches the most recent transaction for the given order.
     *
     * This method derives a query that:
     * - Filters transactions by the associated Order's orderId
     * - Sorts them by transactionDate in descending order
     * - Returns only the top (latest) record
     *
     * Used to determine the last payment attempt status
     * and prevent duplicate successful payments.
     *
     * @param orderId the id of the order
     * @return Optional containing the latest Transaction if present,
     *         otherwise empty
     */

}

