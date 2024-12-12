package com.beautify_project.bp_s3_client.naver;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

public class NCPObjectStorageClient {

    private static final String MESSAGE_FAILED_TO_CREATE_PRESIGNED_PUR_URL = "NCP preSignedPutUrl 생성에 실패하였습니다.";
    private static final String MESSAGE_FAILED_TO_CREATE_PRESIGNED_GET_URL = "NCP preSignedGetUrl 생성에 실패하였습니다.";
    private final NCPConfig ncpConfig;
    private final StaticCredentialsProvider credentialsProvider;

    public NCPObjectStorageClient(final NCPConfig ncpConfig) {
        this.ncpConfig = ncpConfig;
        this.credentialsProvider = initializeCredentialsProvider(ncpConfig);
    }

    private StaticCredentialsProvider initializeCredentialsProvider(
        final NCPConfig ncpConfig) {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(ncpConfig.accessKey(),
            ncpConfig.secretKey()));
    }

    public NCPPreSignedPutUrlResult createPreSignedPutUrl() {
        final String fileId = UUID.randomUUID().toString();

        try (S3Presigner s3Presigner = createS3Presigner()) {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(ncpConfig.bucketName())
                .key(fileId)
                .build();

            PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                preSignRequest);

            return new NCPPreSignedPutUrlResult(presignedPutObjectRequest.url().toExternalForm(),
                fileId);
        } catch (Exception e) {
            throw new S3ClientException(MESSAGE_FAILED_TO_CREATE_PRESIGNED_PUR_URL, e);
        }
    }

    public NCPPreSignedGetUrlResult createPreSignedGetUrl(final String fileName) {
        return getNcpPreSignedGetUrlResult(fileName);
    }

    public List<NCPPreSignedGetUrlResult> createPreSignedGetUrls(final List<String> fileNames) {
        List<NCPPreSignedGetUrlResult> urls = new ArrayList<>();
        fileNames.forEach(fileName -> {
            urls.add(getNcpPreSignedGetUrlResult(fileName));
        });

        return urls;
    }

    private NCPPreSignedGetUrlResult getNcpPreSignedGetUrlResult(final String fileName) {
        try (S3Presigner s3Presigner = createS3Presigner()) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(ncpConfig.bucketName())
                .key(fileName)
                .build();

            GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(objectRequest)
                .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(
                preSignRequest);

            return new NCPPreSignedGetUrlResult(presignedGetObjectRequest.url().toExternalForm());
        } catch (Exception e) {
            throw new S3ClientException(MESSAGE_FAILED_TO_CREATE_PRESIGNED_GET_URL, e);
        }
    }

    private S3Presigner createS3Presigner() {
        return S3Presigner.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create(ncpConfig.endpoint()))
            .region(Region.of(ncpConfig.regionName()))
            .build();
    }
}
