package com.bp.app.api.provider.image;

import com.bp.app.api.config.properties.NaverCloudPlatformObjectStorageConfigProperties;
import com.bp.app.api.response.image.PreSignedGetUrlResult;
import com.bp.app.api.response.image.PreSignedPutUrlResult;
import com.bp.s3.client.naver.NCPConfig;
import com.bp.s3.client.naver.NCPObjectStorageClient;
import com.bp.s3.client.naver.NCPPreSignedGetUrlResult;
import com.bp.s3.client.naver.NCPPreSignedPutUrlResult;
import java.util.List;

public class NCPImageProvider implements ImageProvider {

    private final NCPObjectStorageClient ncpObjectStorageClient;

    public NCPImageProvider(final NaverCloudPlatformObjectStorageConfigProperties ncpConfigProperties) {
        this.ncpObjectStorageClient = new NCPObjectStorageClient(
            new NCPConfig(ncpConfigProperties.endpoint(), ncpConfigProperties.regionName(),
                ncpConfigProperties.bucketName(), ncpConfigProperties.accessKey(),
                ncpConfigProperties.secretKey())
        );
    }

    @Override
    public PreSignedPutUrlResult providePreSignedPutUrl() {
        final NCPPreSignedPutUrlResult result = ncpObjectStorageClient.createPreSignedPutUrl();
        return new PreSignedPutUrlResult(result.preSignedUrl(), result.fileId());
    }

    @Override
    public PreSignedGetUrlResult providePreSignedGetUrlByFileId(final String fileId) {
        final NCPPreSignedGetUrlResult result = ncpObjectStorageClient.createPreSignedGetUrl(
            fileId);
        return new PreSignedGetUrlResult(result.preSignedUrl());
    }

    @Override
    public List<PreSignedGetUrlResult> provideAllPreSignedGetUrlsByFileId(final List<String> fileIds) {
        final List<NCPPreSignedGetUrlResult> resultsFromNCP = ncpObjectStorageClient.createPreSignedGetUrls(
            fileIds);

        return resultsFromNCP.stream().map(ncpGetUrlResult -> {
            return new PreSignedGetUrlResult(ncpGetUrlResult.preSignedUrl());
        }).toList();
    }
}
