package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.response.ErrorResponseMessage.ErrorCode;
import lombok.Getter;

@Getter
public class BpCustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public BpCustomException(final ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public BpCustomException(final String message, final ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
