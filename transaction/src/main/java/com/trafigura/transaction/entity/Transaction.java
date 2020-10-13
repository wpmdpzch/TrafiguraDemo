package com.trafigura.transaction.entity;

import com.trafigura.transaction.enums.OperationTypeEnum;
import com.trafigura.transaction.enums.TradeTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author ：wpm
 */
@Entity
@Getter
@Setter
@Table(name = "transaction")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_transaction_id")
    @SequenceGenerator(name = "seq_transaction_id", sequenceName = "seq_transaction_id", allocationSize = 1)
    private Long transactionId;

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "security_code", columnDefinition = "varchar(6) not null")
    private String securityCode;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "operation_type", columnDefinition = "varchar(12) not null")
    @Enumerated(EnumType.STRING)
    private OperationTypeEnum operationType;

    @Column(name = "trade_type", columnDefinition = "varchar(6) not null")
    @Enumerated(EnumType.STRING)
    private TradeTypeEnum tradeType;

}
