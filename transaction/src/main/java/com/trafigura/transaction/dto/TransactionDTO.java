package com.trafigura.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.trafigura.transaction.enums.OperationTypeEnum;
import com.trafigura.transaction.enums.TradeTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author ：wpm
 */
@Getter
@Setter
@ApiModel("Transaction saving model")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDTO {

    @ApiModelProperty("tradeID")
    private Long tradeId;

    @ApiModelProperty("securityCode")
    @NotBlank
    @Size(min = 3, max = 3)
    private String securityCode;

    @ApiModelProperty("quantity")
    @NotNull
    @Min(1)
    @Max(9999999)
    private long quantity;

    @ApiModelProperty("Operation Type ")
    @NotNull
    private OperationTypeEnum operationType;

    @ApiModelProperty("Trade Type")
    @NotNull
    private TradeTypeEnum tradeType;
}
