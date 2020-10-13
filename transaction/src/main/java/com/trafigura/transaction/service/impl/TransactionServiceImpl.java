package com.trafigura.transaction.service.impl;

import com.trafigura.transaction.dto.TransactionDTO;
import com.trafigura.transaction.entity.Position;
import com.trafigura.transaction.entity.Transaction;
import com.trafigura.transaction.enums.OperationTypeEnum;
import com.trafigura.transaction.enums.TradeTypeEnum;
import com.trafigura.transaction.exception.ApplicationException;
import com.trafigura.transaction.exception.ErrorCodeConstraints;
import com.trafigura.transaction.repository.PositionRepository;
import com.trafigura.transaction.repository.TransactionRepository;
import com.trafigura.transaction.service.TransactionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author ：wpm
 */
@Service
@Log4j2
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PositionRepository positionRepository;


    @Override
    @Transactional
    public void saveTransaction(TransactionDTO transactionDTO) {

        verifyTransaction(transactionDTO);

        Long tradeID = transactionDTO.getTradeId();
        OperationTypeEnum operationType = transactionDTO.getOperationType();
        TradeTypeEnum tradeType = transactionDTO.getTradeType();
        if (operationType.equals(OperationTypeEnum.INSERT)) {

            Optional<Transaction> optionalTransaction = transactionRepository.findFirstByTradeIdOrderByVersionDesc(tradeID);
            if (optionalTransaction.isPresent()) {
                throw new ApplicationException(ErrorCodeConstraints.TRAD_INSERT_DUPLICATE,
                        "Transaction insert duplicate on one trdeID");
            }

            Transaction transaction = new Transaction();
            transaction.setVersion(1L);
            BeanUtils.copyProperties(transactionDTO, transaction);
            transaction.setTradeId(tradeID);
            transactionRepository.save(transaction);

            //update position
            Position position = new Position(transactionDTO.getSecurityCode(), 0L);
            Optional<Position> optional = positionRepository.findById(transactionDTO.getSecurityCode());
            if (optional.isPresent()) {
                position = optional.get();
            }
            if (tradeType.equals(TradeTypeEnum.BUY)) {
                position.setPositionValue(position.getPositionValue() + transaction.getQuantity());
            } else {
                position.setPositionValue(position.getPositionValue() - transaction.getQuantity());
            }
            position.setSecurityCode(transaction.getSecurityCode());
            positionRepository.save(position);
        } else {
            Optional<Transaction> optionalTransactionData = transactionRepository
                    .findFirstByTradeIdOrderByVersionDesc(tradeID);
            if (!optionalTransactionData.isPresent()) {
                log.error("Transaction not exist with tradeId:", transactionDTO.getTradeId());
                throw new ApplicationException(ErrorCodeConstraints.TRANSACTION_NOT_EXIST,
                        "Transaction not exist with tradeId:" + transactionDTO.getTradeId());
            }
            //invalid data of cancel type
            if (optionalTransactionData.get().getOperationType().equals(OperationTypeEnum.CANCEL)) {
                log.error("Operation type error:", operationType);
                throw new ApplicationException(ErrorCodeConstraints.OPERATION_TYPE_ERROR,
                        "Error operation type:" + operationType);
            }

            Transaction lastTranscation = optionalTransactionData.get();
            Transaction newTranscation = new Transaction();
            newTranscation.setVersion(lastTranscation.getVersion() + 1);
            BeanUtils.copyProperties(transactionDTO, newTranscation);
            transactionRepository.save(newTranscation);

            List<String> securityCodes = new ArrayList<>();
            securityCodes.add(lastTranscation.getSecurityCode());
            securityCodes.add(newTranscation.getSecurityCode());
            List<Position> positionList = positionRepository.findAllById(securityCodes);
            Map<String, Position> positionMap = new HashMap<>();
            for (Position position : positionList) {
                positionMap.put(position.getSecurityCode(), position);
            }
            if (positionMap.get(transactionDTO.getSecurityCode()) == null) {
                positionMap.put(transactionDTO.getSecurityCode(),
                        new Position(transactionDTO.getSecurityCode(), 0L));
            }
            if (operationType.equals(OperationTypeEnum.CANCEL)) {
                transactionDTO.setQuantity(0L);
                updatePositionDataMap(transactionDTO, lastTranscation, positionMap);
            } else if (operationType.equals(OperationTypeEnum.UPDATE)) {
                updatePositionDataMap(transactionDTO, lastTranscation, positionMap);
            }
            positionRepository.saveAll(positionMap.values());
        }
    }

    private void verifyTransaction(TransactionDTO transactionDTO) {

        if (StringUtils.isEmpty(transactionDTO.getTradeId())) {
            throw new ApplicationException(ErrorCodeConstraints.TRAD_ID_CAN_NOT_NULL,
                    "TradeId can not null;");
        }

        if (StringUtils.isEmpty(transactionDTO.getSecurityCode())) {
            throw new ApplicationException(ErrorCodeConstraints.TRAD_SECURITY_CODE_CAN_NOT_NULL,
                    "SecurityCode can not null;");
        }

        if (StringUtils.isEmpty(transactionDTO.getQuantity())) {
            throw new ApplicationException(ErrorCodeConstraints.TRAD_QUANTITY_CAN_NOT_NULL,
                    "Quantity can not null;");
        }

        if (StringUtils.isEmpty(transactionDTO.getTradeType())) {
            throw new ApplicationException(ErrorCodeConstraints.TRAD_TYPE_CAN_NOT_NULL,
                    "TradeType can not null;");
        }

        if (StringUtils.isEmpty(transactionDTO.getOperationType())) {
            throw new ApplicationException(ErrorCodeConstraints.TRAD_OPERATION_TYPE_CAN_NOT_NULL,
                    "TradeType can not null;");
        }

    }

    @Override
    public List<Position> queryAllPosition() {

        return positionRepository.findAll();

    }

    @Override
    public List<Transaction> queryAllTransaction() {

        return transactionRepository.findAll();

    }

    @Override
    public Position queryPositionBySecurityCode(String securityCode) {

        Optional<Position> optionalPositionData = positionRepository.findById(securityCode);

        return optionalPositionData.orElse(null);
    }

    /**
     * Support UPDATE securityCode with one tradId (as a rule)
     * <p>
     * Firstly,get the lastest transaction and judge the tradeType;
     * Then 'remove' the lastest transaction quantity;
     * Finally deal with the positions with current request transaction quantity.
     *
     * @param newVersionData
     * @param lastTranscation
     * @param positionMap
     */
    private void updatePositionDataMap(TransactionDTO newVersionData, Transaction lastTranscation,
                                       Map<String, Position> positionMap) {

        if (lastTranscation.getTradeType().equals(TradeTypeEnum.BUY)) {
            positionMap.get(lastTranscation.getSecurityCode()).setPositionValue(
                    positionMap.get(lastTranscation.getSecurityCode()).getPositionValue() - lastTranscation.getQuantity());
        } else {
            positionMap.get(lastTranscation.getSecurityCode()).setPositionValue(
                    positionMap.get(lastTranscation.getSecurityCode()).getPositionValue() + lastTranscation.getQuantity());
        }

        if (newVersionData.getTradeType().equals(TradeTypeEnum.BUY)) {
            positionMap.get(newVersionData.getSecurityCode()).setPositionValue(
                    positionMap.get(lastTranscation.getSecurityCode()).getPositionValue() + newVersionData.getQuantity());
        } else {
            positionMap.get(newVersionData.getSecurityCode()).setPositionValue(
                    positionMap.get(lastTranscation.getSecurityCode()).getPositionValue() - newVersionData.getQuantity());
        }
    }
}
