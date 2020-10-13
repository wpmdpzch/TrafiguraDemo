package com.trafigura.transaction.enums;

/**
 * @author ：wpm
 */
public enum TradeTypeEnum {

    BUY("Buy"),
    SELL("Sell");

    private String value;

    TradeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
