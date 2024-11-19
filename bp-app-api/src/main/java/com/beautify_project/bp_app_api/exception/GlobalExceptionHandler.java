package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_dto.common.response.ErrorCode;
import com.beautify_project.bp_dto.common.response.ErrorResponseMessage;
import com.beautify_project.bp_dto.exception.EnumMismatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MSG_FORMAT_PARAMETER_INVALID = "파라미터 '%s' 가 잘못되었습니다.";
    private static final String MSG_FORMAT_PARAMETER_TYPE_MISMATCH = "파라미터 '%s' 의 데이터 타입이 잘못되었습니다.";
    private static final String MSG_FORMAT_MISSING_PARAMETER = "본문 내 '%s' 은 필수값입니다.";

    /**
     * 주로 요청 온 데이터에 대해 validation(jakarta.validation) 을 실패하는 경우 발생
     *
     * @param exception MethodArgumentNotValidaException
     * @return ResponseEntity<ErrorResponseMessage>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponseMessage> handleMethodArgumentNotValidException (
        final MethodArgumentNotValidException exception) {

        log.error("", exception);

        ErrorResponseMessage errorResponseMessage = createErrorResponseMessage(
            MSG_FORMAT_PARAMETER_INVALID, enumerateFieldsWithComma(exception.getBindingResult()),
            ErrorCode.BR001);

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
     * @param exception MissingServletRequestPartException
     * @return ResponseEntity<ErrorResponseMessage>
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    private ResponseEntity<ErrorResponseMessage> handleMissingServletRequestPartException (
        final MissingServletRequestPartException exception) {
        log.error("", exception);

        ErrorResponseMessage errorResponseMessage = createErrorResponseMessage(
            MSG_FORMAT_MISSING_PARAMETER, exception.getRequestPartName(), ErrorCode.BR002);

        return new ResponseEntity<>(errorResponseMessage, errorResponseMessage.getHttpStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    private ResponseEntity<ErrorResponseMessage> handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException exception) {
        log.error("", exception);

        ErrorResponseMessage errorResponseMessage = createErrorResponseMessage(
            MSG_FORMAT_PARAMETER_INVALID, exception.getParameterName(), ErrorCode.BR001);

        return new ResponseEntity<>(errorResponseMessage, errorResponseMessage.getHttpStatus());
    }

    @ExceptionHandler(EnumMismatchException.class)
    private ResponseEntity<ErrorResponseMessage> handleQueryStringException(
        final EnumMismatchException exception) {
        log.error("", exception);

        ErrorResponseMessage errorResponseMessage = createErrorResponseMessage(
            MSG_FORMAT_PARAMETER_INVALID, exception.getValue(), ErrorCode.BR001);

        return new ResponseEntity<>(errorResponseMessage, errorResponseMessage.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponseMessage> handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException exception) {
        log.error("", exception);

        ErrorResponseMessage errorResponseMessage = createErrorResponseMessage(
            MSG_FORMAT_PARAMETER_TYPE_MISMATCH, exception.getPropertyName(), ErrorCode.BR001
        );

        return new ResponseEntity<>(errorResponseMessage, errorResponseMessage.getHttpStatus());
    }

    private static ErrorResponseMessage createErrorResponseMessage(
        final String messageFormat,
        final String replaceText,
        final ErrorCode errorCode) {

        return ErrorResponseMessage.createCustomErrorMessage(errorCode,
            String.format(messageFormat, replaceText));
    }
}
