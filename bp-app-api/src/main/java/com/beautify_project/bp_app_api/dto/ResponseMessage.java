package com.beautify_project.bp_app_api.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ResponseMessage {

    private Object returnValue;

    private ResponseMessage(final Object returnValue) {
        this.returnValue = returnValue;
    }

    public static ResponseMessage createResponseMessage(final Object responseBody) {
        return new ResponseMessage(responseBody);
    }
}
