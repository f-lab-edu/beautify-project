package com.beautify_project.bp_app_api.dto.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    BR001(HttpStatus.BAD_REQUEST, "BR001", "요청 파라미터가 잘못되었습니다."),
    BR002(HttpStatus.BAD_REQUEST, "BR002", "본문 형식이 맞지 않습니다."),

    UA001(HttpStatus.UNAUTHORIZED, "UA001", "접근 토큰이 존재하지 않습니다."),
    UA002(HttpStatus.UNAUTHORIZED, "UA002", "접근 토큰이 만료되었습니다. "),

    FB001(HttpStatus.FORBIDDEN, "FB001", "해당 API 사용 권한이 없습니다."),

    NF001(HttpStatus.NOT_FOUND, "NF001", "요청 URL이 잘못되었습니다."),
    NF002(HttpStatus.NOT_FOUND, "NF002", "등록되지 않은 리소스 입니다."),

    SH001(HttpStatus.NOT_FOUND, "SH001", "등록되지 않은 미용 시술소입니다."),

    RV001(HttpStatus.NOT_FOUND, "RV001", "등록되지 않은 리뷰입니다."),

    IS001(HttpStatus.INTERNAL_SERVER_ERROR, "IS001", "시스템 에러가 발생하였습니다. 관리자에게 문읜해주세요.")
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
