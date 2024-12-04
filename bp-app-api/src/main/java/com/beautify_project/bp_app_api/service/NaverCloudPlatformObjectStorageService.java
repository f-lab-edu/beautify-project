package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorCode;
import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.exception.StorageException;
import com.beautify_project.bp_s3_client.naver.NCPObjectStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverCloudPlatformObjectStorageService {

    private final NCPObjectStorageClient ncpObjectStorageClient;

    public ResponseMessage getPreSignedPutUrl() {
        try {
            return ResponseMessage.createResponseMessage(
                ncpObjectStorageClient.createPreSignedPutUrl());
        } catch (Exception e) {
            throw new StorageException(e, ErrorCode.SH002);
        }
    }

    public ResponseMessage getPreSignedGetUrl(final String fileId) {
        try {
            return ResponseMessage.createResponseMessage(
                ncpObjectStorageClient.createPreSignedGetUrl(
                    fileId));
        } catch (Exception e) {
            throw new StorageException(e, ErrorCode.SH002);
        }
    }

}
