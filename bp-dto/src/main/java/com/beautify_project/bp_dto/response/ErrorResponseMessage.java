package com.beautify_project.bp_dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ErrorResponseMessage {

    @JsonIgnore
    private HttpStatus httpStatus;
    @JsonInclude(Include.NON_NULL)
    private String errorCode;
    @JsonInclude(Include.NON_NULL)
    private String errorMessage;

    private ErrorResponseMessage(final HttpStatus httpStatus, final String errorCode,
        final String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponseMessage createCustomErrorMessage(final ErrorCode errorCode, final String message) {
        return new ErrorResponseMessage(errorCode.getHttpStatus(), errorCode.getErrorCode(), message);
    }

    public static ErrorResponseMessage createErrorMessage(final ErrorCode errorCode) {
        return new ErrorResponseMessage(errorCode.getHttpStatus(), errorCode.getErrorCode(),
            errorCode.getErrorMessage());
    }
}
