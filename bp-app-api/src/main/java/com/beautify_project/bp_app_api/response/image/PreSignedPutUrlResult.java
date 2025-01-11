package com.beautify_project.bp_app_api.response.image;

public record PreSignedPutUrlResult(String preSignedUrl, String fileId){

    @Override
    public String toString() {
        return "PreSignedPutUrl{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            ", fileId='" + fileId + '\'' +
            '}';
    }
}
