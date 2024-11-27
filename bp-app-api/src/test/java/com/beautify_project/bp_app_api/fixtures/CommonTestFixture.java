package com.beautify_project.bp_app_api.fixtures;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CommonTestFixture {

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static boolean isInitialized(Object[] array) {
        return array != null && array.length > 0;
    }


}
