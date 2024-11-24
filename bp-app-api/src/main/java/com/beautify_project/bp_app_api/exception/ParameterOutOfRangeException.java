package com.beautify_project.bp_app_api.exception;

import lombok.Getter;

@Getter
public class ParameterOutOfRangeException extends RuntimeException {

    private String parameterName;
    private String parameterValue;

    public ParameterOutOfRangeException(String message) {
        super(message);
    }

    public ParameterOutOfRangeException(final String parameterName, final String parameterValue) {
        super(parameterName + "에 해당하는 값 " + "'" + parameterValue + "' 는 올바르지 않습니다.");
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }
}
