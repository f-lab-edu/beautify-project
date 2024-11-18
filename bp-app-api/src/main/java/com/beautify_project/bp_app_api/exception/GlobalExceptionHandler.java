package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_dto.response.ErrorCode;
import com.beautify_project.bp_dto.response.ErrorResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String METHOD_ARGUMENT_NOT_VALID_EXCEPTION_FORMAT = "파라미터 '%s' 가 잘못되었습니다.";
    private static final String MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION_FORMAT = "본문 내 '%s' 은 필수값입니다.";

    /**
     * 주로 요청 온 데이터에 대해 validation(jakarta.validation) 을 실패하는 경우 발생
     *
     * @param exception MethodArgumentNotValidaException
     * @return ResponseEntity<ErrorResponseMessage>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponseMessage> handleMethodArgumentNotValidException (
        final MethodArgumentNotValidException exception) {

        ErrorResponseMessage errorResponseMessage = ErrorResponseMessage.createCustomErrorMessage(
            ErrorCode.BR001,
            String.format(METHOD_ARGUMENT_NOT_VALID_EXCEPTION_FORMAT,
                enumerateFieldsWithComma(exception.getBindingResult()))
        );

        return new ResponseEntity<>(errorResponseMessage, errorResponseMessage.getHttpStatus());
    }

    private String enumerateFieldsWithComma(final BindingResult bindingResult) {
        StringBuilder fields = new StringBuilder();
        bindingResult.getFieldErrors()
            .forEach(fieldError -> fields.append(fieldError.getField()).append(","));
        fields.deleteCharAt(fields.length() - 1);
        return fields.toString();
    }

    /**
     * '@RequestPart required = true' 인 파라미터에 아무런 값이 들어오지 않을 때
     *
     * @param exception MissingServletRequestPartException.class
     * @return ResponseEntity<ErrorResponseMessage>
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    private ResponseEntity<ErrorResponseMessage> handleMissingServletRequestPartException (
        final MissingServletRequestPartException exception) {

        ErrorResponseMessage errorResponseMessage = ErrorResponseMessage.createCustomErrorMessage(
            ErrorCode.BR002,
            String.format(MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION_FORMAT,
                exception.getRequestPartName()));

        return new ResponseEntity<>(errorResponseMessage, errorResponseMessage.getHttpStatus());
    }
}
