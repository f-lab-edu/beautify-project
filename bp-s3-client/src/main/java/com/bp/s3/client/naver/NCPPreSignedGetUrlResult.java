package com.bp.s3.client.naver;

public record NCPPreSignedGetUrlResult(String preSignedUrl) {

    @Override
    public String toString() {
        return "PreSignedGetUrlResult{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            '}';
    }
}
