package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import lombok.Getter;

@Getter
public class ConfigurationException extends RuntimeException {

    private ErrorCode errorCode;

    public ConfigurationException(final ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public ConfigurationException(final Exception exception, final ErrorCode errorCode) {
        super(exception);
        this.errorCode = errorCode;
    }

    public ConfigurationException(final String message, final ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
