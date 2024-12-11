package com.beautify_project.bp_s3_client.naver;

public record NCPPreSignedPutUrlResult(String preSignedUrl, String fileId) {

    @Override
    public String toString() {
        return "PreSignedPutUrlResult{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            ", fileId='" + fileId + '\'' +
            '}';
    }
}
