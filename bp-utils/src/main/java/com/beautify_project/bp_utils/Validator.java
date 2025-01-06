package com.beautify_project.bp_utils;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Validator {

    public static boolean isNullOrEmpty(final List<?> input) {
        return input == null || input.isEmpty();
    }

    public static boolean isNullOrEmpty(final Map<?, ?> input) {
        return input == null || input.isEmpty();
    }

    public static void throwIfNullOrEmpty(final List<?> input, final RuntimeException exception) {
        if (isNullOrEmpty(input)) {
            throw exception;
        }
    }

    public static void throwIfEmptyOrBlank(final String input, final RuntimeException exception) {
        if (isEmptyOrBlank(input)) {
            throw exception;
        }
    }

    public static boolean isEmptyOrBlank(final String input) {
        return StringUtils.isEmpty(input) || StringUtils.isBlank(input);
    }
}
