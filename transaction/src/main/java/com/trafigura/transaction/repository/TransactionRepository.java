package com.trafigura.transaction.repository;

import com.trafigura.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author ：wpm
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findFirstByTradeIdOrderByVersionDesc(Long tradeID);

    Optional<List<Transaction>> findTransactionsByTradeIdOrderByVersionDesc(Long tradeID);

}
