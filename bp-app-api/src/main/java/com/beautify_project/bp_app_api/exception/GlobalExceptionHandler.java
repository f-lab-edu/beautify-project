package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage;
import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MSG_FORMAT_PARAMETER_INVALID = "파라미터 '%s' 가 잘못되었습니다.";
    private static final String MSG_FORMAT_PARAMETER_TYPE_MISMATCH = "파라미터 '%s' 의 데이터 타입이 잘못되었습니다.";
    private static final String MSG_FORMAT_MISSING_PARAMETER = "본문 내 '%s' 은 필수값입니다.";
    private static final String MSG_FORMAT_PARAMETER_OUT_OF_RANGE = "파라미터 '%s' 의 값이 범위에 벗어났습니다.";

    /**
     * 주로 요청 온 데이터에 대해 validation(jakarta.validation) 을 실패하는 경우 발생
     *
     * @param exception MethodArgumentNotValidaException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponseMessage> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception) {
        log.error("", exception);

        final String customMessage = String.format(MSG_FORMAT_PARAMETER_INVALID,
            enumerateFieldsWithComma(exception.getBindingResult()));

        return createResponseWithCustomMessage(ErrorCode.BR001, customMessage);
    }

    private String enumerateFieldsWithComma(final BindingResult bindingResult) {
        StringBuilder fields = new StringBuilder();
        bindingResult.getFieldErrors()
            .forEach(fieldError -> fields.append(fieldError.getField()).append(","));
        fields.deleteCharAt(fields.length() - 1);
        return fields.toString();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<ErrorResponseMessage> handleHttpMessageNotReadableException(
        final HttpMessageNotReadableException exception) {
        log.error("", exception);
        return createResponse(ErrorCode.BR002);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    private ResponseEntity<ErrorResponseMessage> handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException exception) {
        log.error("", exception);

        final String customMessage = String.format(MSG_FORMAT_PARAMETER_INVALID,
            exception.getParameterName());

        return createResponseWithCustomMessage(ErrorCode.BR001, customMessage);
    }

    @ExceptionHandler(EnumMismatchException.class)
    private ResponseEntity<ErrorResponseMessage> handleQueryStringException(final EnumMismatchException exception) {
        log.error("", exception);

        final String customMessage = String.format(MSG_FORMAT_PARAMETER_INVALID,
            exception.getValue());

        return createResponseWithCustomMessage(ErrorCode.BR001, customMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponseMessage> handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException exception) {
        log.error("", exception);

        final String customMessage = String.format(MSG_FORMAT_PARAMETER_TYPE_MISMATCH,
            exception.getPropertyName());

        return createResponseWithCustomMessage(ErrorCode.BR001, customMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    private ResponseEntity<ErrorResponseMessage> handleNoResourceFoundException(
        final NoResourceFoundException exception) {
        log.error("", exception);
        return createResponse(ErrorCode.NF001);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    private ResponseEntity<ErrorResponseMessage> handleHandlerMethodValidationException(
        final HandlerMethodValidationException exception) {
        log.error("", exception);
        return createResponse(ErrorCode.BR001);
    }

    @ExceptionHandler(ParameterOutOfRangeException.class)
    private ResponseEntity<ErrorResponseMessage> handleParameterOutOfRangeException(
        final ParameterOutOfRangeException exception) {
        log.error("", exception);

        final String customMessage = String.format(MSG_FORMAT_PARAMETER_OUT_OF_RANGE,
            exception.getParameterName());
        return createResponseWithCustomMessage(ErrorCode.BR001, customMessage);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    private ResponseEntity<ErrorResponseMessage> handleEmptyResultDataAccessException(
        final EmptyResultDataAccessException exception) {
        log.error("", exception);
        return createResponse(ErrorCode.NF002);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<ErrorResponseMessage> handleNotFoundException(final NotFoundException exception) {
        log.error("", exception);
        return createResponse(exception.getErrorCode());
    }

    @ExceptionHandler(StorageException.class)
    private ResponseEntity<ErrorResponseMessage> handleStorageException(final StorageException exception) {
        log.error("", exception);
        return createResponse(exception.getErrorCode());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    private ResponseEntity<ErrorResponseMessage> handleMissingPathVariableException(
        final MissingPathVariableException exception
    ) {
        log.error("", exception);
        return createResponse(ErrorCode.BR001);
    }

    @ExceptionHandler(ConfigurationException.class)
    private ResponseEntity<ErrorResponseMessage> handleConfigurationException(
        final ConfigurationException exception) {
        log.error("", exception);
        return createResponse(exception.getErrorCode());
    }

    private static ResponseEntity<ErrorResponseMessage> createResponse(final ErrorCode errorCode) {
        ErrorResponseMessage errorResponseMessage = ErrorResponseMessage.createErrorMessage(
            errorCode);
        return new ResponseEntity<>(errorResponseMessage, errorResponseMessage.getHttpStatus());
    }

    private static ResponseEntity<ErrorResponseMessage> createResponseWithCustomMessage(
        final ErrorCode errorCode, final String customMessage) {

        ErrorResponseMessage customErrorResponseMessage = ErrorResponseMessage.createCustomErrorMessage(
            errorCode, customMessage);

        return new ResponseEntity<>(customErrorResponseMessage,
            customErrorResponseMessage.getHttpStatus());
    }
}
