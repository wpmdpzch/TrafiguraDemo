package com.trafigura.transaction.repository;

import com.trafigura.transaction.entity.TradeSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author ：wpm
 */
public interface TradeSequenceRepository extends JpaRepository<TradeSequence, Long> {
    @Query(value = "select nextval('seq_transaction_id')", nativeQuery = true)
    Long getTransactionId();
}
