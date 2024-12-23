package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {

    private ErrorCode errorCode;

    public InvalidRequestException(final ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

}
