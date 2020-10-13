package com.trafigura.transaction.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author ：wpm
 */
@Entity
@Getter
@Setter
@Table(name = "position")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Position {

    @Id
    @Column(name = "security_code", columnDefinition = "varchar(6) not null")
    private String securityCode;

    @Column(name = "position_value", nullable = false)
    private Long positionValue;
}
