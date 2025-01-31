package com.bp.app.api.response.image;

public record PreSignedGetUrlResult (String preSignedUrl){

    @Override
    public String toString() {
        return "PreSignedGetUrlResult{" +
            "preSignedUrl='" + preSignedUrl + '\'' +
            '}';
    }
}
