package com.bp.s3.client.naver;

public class S3ClientException extends RuntimeException {

    public S3ClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
