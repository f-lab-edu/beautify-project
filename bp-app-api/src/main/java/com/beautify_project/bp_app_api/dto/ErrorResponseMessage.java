package com.beautify_project.bp_app_api.dto;

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

    private ErrorResponseMessage(final HttpStatus httpStatus, final String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponseMessage createCustomErrorMessage(final ErrorCode errorCode, final String message) {
        return new ErrorResponseMessage(errorCode.getHttpStatus(), errorCode.getErrorCode(), message);
    }

    public static ErrorResponseMessage createCustomErrorMessageWithoutErrorCode(
        final HttpStatus httpStatus, final String message) {
        return new ErrorResponseMessage(httpStatus, message);
    }

    public static ErrorResponseMessage createErrorMessage(final ErrorCode errorCode) {
        return new ErrorResponseMessage(errorCode.getHttpStatus(), errorCode.errorCode,
            errorCode.errorMessage);
    }

    @Getter
    public enum ErrorCode {
        BR001(HttpStatus.BAD_REQUEST, "BR001", "요청 파라미터가 잘못되었습니다."),
        BR002(HttpStatus.BAD_REQUEST, "BR002", "본문 형식이 맞지 않습니다."),

        UA001(HttpStatus.UNAUTHORIZED, "UA001", "인증되지 않은 요청입니다."),
        UA002(HttpStatus.UNAUTHORIZED, "UA002", "접근 토큰이 존재하지 않습니다."),
        UA003(HttpStatus.UNAUTHORIZED, "UA003", "접근 토큰이 만료되었습니다. "),

        FB001(HttpStatus.FORBIDDEN, "FB001", "해당 API 사용 권한이 없습니다."),

        NF001(HttpStatus.NOT_FOUND, "NF001", "요청 URL이 잘못되었습니다."),
        NF002(HttpStatus.NOT_FOUND, "NF002", "등록되지 않은 리소스 입니다."),

        SH001(HttpStatus.NOT_FOUND, "SH001", "등록되지 않은 샵 입니다."),
        SH002(HttpStatus.INTERNAL_SERVER_ERROR, "SH002", "샵 등록에 실패했습니다."),

        EC001(HttpStatus.BAD_REQUEST, "EC001", "인증 메일 유효 시간이 지나지 않았습니다. 잠시후에 다시 시도해주세요."),
        EC002(HttpStatus.NOT_FOUND, "EC002", "인증 요청 이력이 없습니다."),
        EC003(HttpStatus.UNAUTHORIZED, "EC003", "인증 번호가 맞지 않습니다. 확인 후 다시 요청해주세요."),

        RE001(HttpStatus.NOT_FOUND, "RE001", "등록되지 않은 리뷰입니다."),

        OP001(HttpStatus.NOT_FOUND, "OP001", "등록되지 않은 시술입니다."),

        ME001(HttpStatus.NOT_FOUND, "ME001", "등록되지 않은 회원입니다."),
        ME002(HttpStatus.BAD_REQUEST, "SH003", "이미 등록된 회원입니다."),

        RS001(HttpStatus.NOT_FOUND, "RS001", "등록되지 않은 예약입니다."),

        FA001(HttpStatus.NOT_FOUND, "FA001", "등록되지 않은 편의시설입니다."),

        SF001(HttpStatus.NOT_FOUND, "SF001", "샵에 속한 편의시설이 존재하지 않습니다."),

        SO001(HttpStatus.NOT_FOUND, "SO001", "샵에 속한 시설이 존재하지 않습니다."),

        OC001(HttpStatus.NOT_FOUND, "OC001", "시술을 포함한 카테고리가 없습니다"),

        AL001(HttpStatus.BAD_REQUEST, "AL001", "이미 좋아요를 누른 샵 입니다."),
        AL002(HttpStatus.BAD_REQUEST, "AL002", "좋아요를 하지 않은 샵 입니다."),

        II001(HttpStatus.NOT_FOUND, "II001", "유효하지 않은 식별자 입니다."),
        II002(HttpStatus.BAD_REQUEST, "II002", "이미 처리된 식별자 입니다."),

        SI001(HttpStatus.UNAUTHORIZED, "SI001", "로그인에 실패하였습니다"),
        SI002(HttpStatus.UNAUTHORIZED, "SI002", "인증 토큰이 유효하지 않습니다"),


        IS001(HttpStatus.INTERNAL_SERVER_ERROR, "IS001", "시스템 에러가 발생하였습니다. 관리자에게 문의해주세요."),
        IS002(HttpStatus.INTERNAL_SERVER_ERROR, "IS002", "외부 시스템에서 에러가 발생하였습니다.")
        ;

        private final HttpStatus httpStatus;
        private final String errorCode;
        private final String errorMessage;

        ErrorCode(final HttpStatus httpStatus, final String errorCode, final String errorMessage) {
            this.httpStatus = httpStatus;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }
}
