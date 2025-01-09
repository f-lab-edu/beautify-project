package com.beautify_project.bp_app_api.repository.image;

import com.beautify_project.bp_app_api.config.properties.NaverCloudPlatformObjectStorageConfig;
import com.beautify_project.bp_app_api.dto.image.PreSignedGetUrlResult;
import com.beautify_project.bp_app_api.dto.image.PreSignedPutUrlResult;
import com.beautify_project.bp_s3_client.naver.NCPConfig;
import com.beautify_project.bp_s3_client.naver.NCPObjectStorageClient;
import com.beautify_project.bp_s3_client.naver.NCPPreSignedPutUrlResult;
import java.util.ArrayList;
import java.util.List;

public class NaverCloudPlatformObjectStorageRepository implements ImageRepository {

    private final NCPObjectStorageClient ncpObjectStorageClient;

    public NaverCloudPlatformObjectStorageRepository(
        final NaverCloudPlatformObjectStorageConfig ncpConfigBean) {
        this.ncpObjectStorageClient = new NCPObjectStorageClient(
            new NCPConfig(ncpConfigBean.endpoint(), ncpConfigBean.regionName(),
                ncpConfigBean.bucketName(), ncpConfigBean.accessKey(),
                ncpConfigBean.secretKey()));
    }

    @Override
    public PreSignedPutUrlResult createPutUrlResult() {
        NCPPreSignedPutUrlResult result = ncpObjectStorageClient.createPreSignedPutUrl();
        return new PreSignedPutUrlResult(result.preSignedUrl(), result.fileId());
    }

    @Override
    public PreSignedGetUrlResult findImageLinkByFileId(final String fileId) {
        return new PreSignedGetUrlResult(
            ncpObjectStorageClient.createPreSignedGetUrl(fileId).preSignedUrl());
    }

    @Override
    public List<PreSignedGetUrlResult> findAllImageLinksByFileIds(final List<String> fileId) {
        List<PreSignedGetUrlResult> result = new ArrayList<>();
        ncpObjectStorageClient.createPreSignedGetUrls(fileId).forEach(ncpGetUrlResult -> {
            result.add(new PreSignedGetUrlResult(ncpGetUrlResult.preSignedUrl()));
        });
        return result;
    }
}
