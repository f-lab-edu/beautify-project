package com.beautify_project.bp_utils;

import java.util.UUID;

public class UUIDGenerator {

    public static String generateUUIDForEntity() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
