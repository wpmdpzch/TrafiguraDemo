package com.trafigura.transaction.controller;

import com.trafigura.transaction.dto.TransactionDTO;
import com.trafigura.transaction.entity.Position;
import com.trafigura.transaction.entity.Transaction;
import com.trafigura.transaction.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author ：wpm
 */
@RestController
@Api(tags = {"Transaction Controller"})
@RequestMapping(value = "/api/v1")
@Log4j2
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping(value = "/transaction/save")
    @ApiOperation("save transaction data")
    public void saveTransactionByDto(@Valid @ApiParam(required = true) @RequestBody TransactionDTO transactionDTO) throws Exception {

        log.info("Start method saveTransactionByDto");
        transactionService.saveTransaction(transactionDTO);
    }

    @GetMapping(value = "/transaction/getAll")
    @ApiOperation("query all transactions")
    public ResponseEntity<List<Transaction>> getTranactionList() throws Exception {

        log.info("Start method getTranactionList");
        List<Transaction> transactionList = transactionService.queryAllTransaction();
        return ResponseEntity.ok(transactionList);
    }

    @GetMapping(value = "/position/getAll")
    @ApiOperation("query all positions")
    public ResponseEntity<List<Position>> getPositionList() throws Exception {

        log.info("Start method getPositionList");
        List<Position> positionList = transactionService.queryAllPosition();
        return ResponseEntity.ok(positionList);
    }

    @GetMapping(value = "/position/getByCode")
    @ApiOperation("query one position by securityCode")
    public ResponseEntity<Position> getPositionBySecurityCode(@Valid @Size(min = 3, max = 3)
                                                              @ApiParam(required = true) @RequestParam String securityCode) throws Exception {
        log.info("Start method getPositionBySecurityCode");
        Position position = transactionService.queryPositionBySecurityCode(securityCode);
        return ResponseEntity.ok(position);
    }
}
