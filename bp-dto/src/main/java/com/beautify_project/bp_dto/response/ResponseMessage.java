package com.beautify_project.bp_dto.response;

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
public class ResponseMessage {

    @JsonIgnore
    private HttpStatus httpStatus;
    @JsonInclude(Include.NON_NULL)
    private Object returnValue;

    private ResponseMessage(final HttpStatus httpStatus, final Object returnValue) {
        this.httpStatus = httpStatus;
        this.returnValue = returnValue;
    }

    public static ResponseMessage createResponseMessage(final HttpStatus httpStatus,
        final Object returnValue) {
        return new ResponseMessage(httpStatus, returnValue);
    }
}
