package com.trafigura.transaction.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author ：wpm
 */
@Getter
@Setter
public class RestRequestError {
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String errorCode;
    private String message;
    private String debugMessage;

    private RestRequestError() {
        timestamp = LocalDateTime.now();
    }

    RestRequestError(HttpStatus status, String errorCode, String message, Throwable ex) {
        this();
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
}
