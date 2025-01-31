package com.bp.domain.mysql.exception;

import lombok.Getter;

@Getter
public class EnumMismatchException extends RuntimeException {

    private String enumName;
    private String value;

    public EnumMismatchException(String message) {
        super(message);
    }

    public EnumMismatchException(final String enumName, final String value) {
        super(enumName +  "에 해당하는 값 " + "'" + value + "' 은 올바르지 않습니다.");
        this.enumName = enumName;
        this.value = value;
    }

}
