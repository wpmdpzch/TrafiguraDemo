package com.trafigura.transaction.exception;

public class ErrorCodeConstraints {

    public static final String OPERATION_TYPE_ERROR = "1000";
    public static final String TRANSACTION_NOT_EXIST = "1001";
    public static final String TRAD_INSERT_DUPLICATE = "1002";

    /**
     * Parameters verification error code
     */
    public static final String TRAD_ID_CAN_NOT_NULL = "1003";
    public static final String TRAD_SECURITY_CODE_CAN_NOT_NULL = "1004";
    public static final String TRAD_QUANTITY_CAN_NOT_NULL = "1005";
    public static final String TRAD_TYPE_CAN_NOT_NULL = "1006";
    public static final String TRAD_OPERATION_TYPE_CAN_NOT_NULL = "1007";

    private ErrorCodeConstraints() {

    }
}
