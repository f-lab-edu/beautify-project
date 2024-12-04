package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.dto.common.ErrorCode;
import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {

    private ErrorCode errorCode;

    public StorageException(final ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public StorageException(final Exception exception, final ErrorCode errorCode) {
        super(exception);
        this.errorCode = errorCode;
    }
}
