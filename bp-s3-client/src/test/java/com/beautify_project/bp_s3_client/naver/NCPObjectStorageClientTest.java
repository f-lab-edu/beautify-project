package com.beautify_project.bp_s3_client.naver;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NCPObjectStorageClientTest {

    private static NCPConfig NCP_CONFIG;
    private static NCPObjectStorageClient REAL_NCP_OBJECT_STORAGE_CLIENT;

    @BeforeAll
    static void setUp() {
        initNcpConfig();
        initNcpObjectStorageClient();
    }

    private static void initNcpConfig() {
        String filePath = "src/main/resources/naver-cloud-platform-auth.yml";
        Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            properties.load(fileInputStream);
            NCP_CONFIG = new NCPConfig(properties.getProperty("end-point"),
                properties.getProperty("region-name"), properties.getProperty("bucket-name"),
                properties.getProperty("access-key"), properties.getProperty("secret-key"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void initNcpObjectStorageClient() {
        REAL_NCP_OBJECT_STORAGE_CLIENT = new NCPObjectStorageClient(NCP_CONFIG);
    }

    @Test
    @DisplayName("preSignedPutUrl 생성 테스트")
    void testCreatePreSignedPutUrl() {
        PreSignedPutUrlResult preSignedPutUrlResult = REAL_NCP_OBJECT_STORAGE_CLIENT.createPreSignedPutUrl();
        assertThat(preSignedPutUrlResult.preSignedUrl()).isNotBlank();
        assertThat(preSignedPutUrlResult.fileId()).isNotBlank();
    }

    @Test
    @DisplayName("preSignedGetUrl 생성 테스트")
    void testPreSignedGetUrl() {
        PreSignedGetUrlResult preSignedGetUrlResult = REAL_NCP_OBJECT_STORAGE_CLIENT.createPreSignedGetUrl(
            UUID.randomUUID().toString());
        assertThat(preSignedGetUrlResult.preSignedUrl()).isNotBlank();
    }
}
