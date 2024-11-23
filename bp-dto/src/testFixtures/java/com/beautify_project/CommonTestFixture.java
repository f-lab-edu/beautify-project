package com.beautify_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CommonTestFixture {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(
        new JavaTimeModule());

}
