package com.bp.app.api.response;

import java.util.ArrayList;
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

    public static ResponseMessage createResponseMessage(final Object responseBody) {
        return new ResponseMessage(responseBody);
    }

    public static ResponseMessage createEmptyListResponseMessage() {
        return new ResponseMessage(new ArrayList<>());
    }
}
