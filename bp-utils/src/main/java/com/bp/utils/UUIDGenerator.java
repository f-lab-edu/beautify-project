package com.bp.utils;

import java.util.UUID;

public class UUIDGenerator {

    public static String generateUUIDForEntity() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateEmailCertificationNumber() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
