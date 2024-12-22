package com.beautify_project.bp_app_api.exception;

import com.beautify_project.bp_app_api.enumeration.EntityType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class InvalidIdException extends RuntimeException {

    private static final String FORMAT_ERROR_MESSAGE = "'%s' 엔티티의 식별자 '%s' 에 해당하는 값 '%s' 이 존재하지 않습니다.";
    private static final String FORMAT_ERROR_MESSAGE_VALUE_NOT_EXISTS = "'%s' 엔티티의 식별자 '%s' 에 해당하는 값이 존재하지 않습니다.";

    private final EntityType entityType;
    private final String id;
    private final String value;
    private final String errorMessage;

    public InvalidIdException(final EntityType entityType, final String id, final String value) {
        super(String.format(FORMAT_ERROR_MESSAGE, entityType, id, value));
        this.entityType = entityType;
        this.id = id;
        this.value = value;
        errorMessage = createErrorMessage(entityType, id, value);
    }

    private static String createErrorMessage(final EntityType entityType, final String id,
        final String value) {
        if (StringUtils.isEmpty(value)) {
            return String.format(FORMAT_ERROR_MESSAGE_VALUE_NOT_EXISTS, entityType, id);
        }
        return String.format(FORMAT_ERROR_MESSAGE, entityType, id, value);
    }
}
