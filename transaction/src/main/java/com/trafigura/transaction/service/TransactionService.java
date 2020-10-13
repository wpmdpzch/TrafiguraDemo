package com.trafigura.transaction.service;

import com.trafigura.transaction.dto.TransactionDTO;
import com.trafigura.transaction.entity.Position;
import com.trafigura.transaction.entity.Transaction;

import java.util.List;

/**
 * @author ：wpm
 */
public interface TransactionService {

    void saveTransaction(TransactionDTO transactionDTO) throws Exception;

    List<Position> queryAllPosition() throws Exception;

    List<Transaction> queryAllTransaction() throws Exception;

    Position queryPositionBySecurityCode(String securityCode) throws Exception;
}
