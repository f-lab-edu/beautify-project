package com.beautify_project.bp_app_api.dto.image;

public record PreSignedGetUrlResult (String preSignedUrl){

    @Override
    public String toString() {
        return "PreSignedGetUrlResult{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            '}';
    }
}
