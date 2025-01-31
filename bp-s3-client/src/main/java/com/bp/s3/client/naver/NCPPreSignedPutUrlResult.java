package com.bp.s3.client.naver;

public record NCPPreSignedPutUrlResult(String preSignedUrl, String fileId) {

    @Override
    public String toString() {
        return "PreSignedPutUrlResult{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            ", fileId='" + fileId + '\'' +
            '}';
    }
}
