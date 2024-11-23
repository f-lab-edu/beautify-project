package com.beautify_project.bp_dto.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
public class ResponseMessage {

    private Object returnValue;

    private ResponseMessage(final Object returnValue) {
        this.returnValue = returnValue;
    }

//    private ResponseMessage(final HttpStatus httpStatus, final Object returnValue) {
//        this.httpStatus = httpStatus;
//        this.returnValue = returnValue;
//    }

    public static ResponseMessage createResponseMessage(final Object responseBody) {
        return new ResponseMessage(responseBody);
    }

//    public static ResponseMessage createResponseMessage(final HttpStatus httpStatus,
//        final Object returnValue) {
//        return new ResponseMessage(httpStatus, returnValue);
//    }
}
