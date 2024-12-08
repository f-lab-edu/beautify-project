package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public NotFoundException(final ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
