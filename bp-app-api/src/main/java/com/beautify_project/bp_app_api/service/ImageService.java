package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_s3_client.naver.NCPObjectStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final NCPObjectStorageClient ncpObjectStorageClient;

    public ResponseMessage issuePreSignedPutUrl() {

        return ResponseMessage.createResponseMessage(
            ncpObjectStorageClient.createPreSignedPutUrl());
    }

    public ResponseMessage issuePreSignedGetUrl(final String fileId) {
        return ResponseMessage.createResponseMessage(
            ncpObjectStorageClient.createPreSignedGetUrl(
                fileId));
    }

}
