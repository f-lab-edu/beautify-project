package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.dto.common.ErrorCode;
import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponseMessage handleMethodArgumentNotValidException (
        final MethodArgumentNotValidException exception) {

        log.error("", exception);

        return createErrorResponseMessage(MSG_FORMAT_PARAMETER_INVALID,
            enumerateFieldsWithComma(exception.getBindingResult()), ErrorCode.BR001);
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
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponseMessage handleMissingServletRequestPartException(
        final MissingServletRequestPartException exception) {

        log.error("", exception);

        return createErrorResponseMessage(MSG_FORMAT_MISSING_PARAMETER,
            exception.getRequestPartName(), ErrorCode.BR002);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponseMessage handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException exception) {

        log.error("", exception);

        return createErrorResponseMessage(MSG_FORMAT_PARAMETER_INVALID,
            exception.getParameterName(), ErrorCode.BR001);
    }

    @ExceptionHandler(EnumMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponseMessage handleQueryStringException(final EnumMismatchException exception) {
        log.error("", exception);

        return createErrorResponseMessage(MSG_FORMAT_PARAMETER_INVALID, exception.getValue(),
            ErrorCode.BR001);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ErrorResponseMessage handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException exception) {
        log.error("", exception);

        return createErrorResponseMessage(MSG_FORMAT_PARAMETER_TYPE_MISMATCH,
            exception.getPropertyName(), ErrorCode.BR001);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ErrorResponseMessage handleNoResourceFoundException(final NoResourceFoundException exception) {
        log.error("", exception);
        return ErrorResponseMessage.createErrorMessage(ErrorCode.NF001);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorResponseMessage handleHandlerMethodValidationException(
        final HandlerMethodValidationException exception) {
        log.error("", exception);
        return ErrorResponseMessage.createErrorMessage(ErrorCode.BR001);
    }

    @ExceptionHandler(ParameterOutOfRangeException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ErrorResponseMessage handleParameterOutOfRangeException(
        final ParameterOutOfRangeException exception) {
        log.error("", exception);
        return createErrorResponseMessage(MSG_FORMAT_PARAMETER_OUT_OF_RANGE,
            exception.getParameterName(), ErrorCode.BR001);
    }

    @ExceptionHandler(FileStoreException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    private ErrorResponseMessage handleFileStoreException(final FileStoreException exception) {
        log.error("", exception);
        return ErrorResponseMessage.createErrorMessage(ErrorCode.IS001);
    }

    private static ErrorResponseMessage createErrorResponseMessage(
        final String messageFormat,
        final String replaceText,
        final ErrorCode errorCode) {

        return ErrorResponseMessage.createCustomErrorMessage(errorCode,
            String.format(messageFormat, replaceText));
    }
}
