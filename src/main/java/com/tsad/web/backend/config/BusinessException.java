package com.tsad.web.backend.config;

import com.tsad.web.backend.common.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends Exception {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public BusinessException(HttpStatus httpStatus,
                             ErrorCode errorCode) {
        super(errorCode.toString());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode.name();
    }
}