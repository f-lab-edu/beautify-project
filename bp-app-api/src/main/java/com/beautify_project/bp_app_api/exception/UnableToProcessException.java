package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import lombok.Getter;

@Getter
public class UnableToProcessException extends RuntimeException {

  private final ErrorCode errorCode;

  public UnableToProcessException(final String message, final ErrorCode errorCode) {
      super(message);
      this.errorCode = errorCode;
  }

  public UnableToProcessException(final ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
