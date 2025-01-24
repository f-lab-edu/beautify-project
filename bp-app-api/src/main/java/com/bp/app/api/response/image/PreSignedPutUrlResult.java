package com.bp.app.api.response.image;

public record PreSignedPutUrlResult(String preSignedUrl, String fileId){

    @Override
    public String toString() {
        return "PreSignedPutUrl{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            ", fileId='" + fileId + '\'' +
            '}';
    }
}
