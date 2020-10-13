package com.trafigura.transaction.enums;

/**
 * @author ：wpm
 */
public enum OperationTypeEnum {

    INSERT("Insert"),
    UPDATE("Update"),
    CANCEL("Cancel");

    private String value;

    OperationTypeEnum(String value) {
        this.value = value;
    }
}
