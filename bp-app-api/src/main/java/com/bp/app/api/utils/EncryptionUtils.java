package com.bp.app.api.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncryptionUtils {

    private static final PasswordEncoder BCRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static String encodeBCrypt(final String plain) {
        return BCRYPT_PASSWORD_ENCODER.encode(plain);
    }

    public static boolean matchesBCrypt(final String inputPlain, final String encodedPassword) {
        return BCRYPT_PASSWORD_ENCODER.matches(inputPlain, encodedPassword);
    }

}
