package com.beautify_project.bp_s3_client.naver;

public record NCPPreSignedGetUrlResult(String preSignedUrl) {

    @Override
    public String toString() {
        return "PreSignedGetUrlResult{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            '}';
    }
}
