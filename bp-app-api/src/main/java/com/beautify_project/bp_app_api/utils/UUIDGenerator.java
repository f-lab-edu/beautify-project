package com.beautify_project.bp_app_api.utils;

import java.util.UUID;

public class UUIDGenerator {

    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
