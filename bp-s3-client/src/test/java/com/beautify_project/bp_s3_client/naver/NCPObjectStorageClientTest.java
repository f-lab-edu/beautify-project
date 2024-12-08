package com.beautify_project.bp_s3_client.naver;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
        NCPPreSignedPutUrlResult ncpPreSignedPutUrlResult = REAL_NCP_OBJECT_STORAGE_CLIENT.createPreSignedPutUrl();
        assertThat(ncpPreSignedPutUrlResult.preSignedUrl()).isNotBlank();
        assertThat(ncpPreSignedPutUrlResult.fileId()).isNotBlank();
    }

    @Test
    @DisplayName("preSignedGetUrl 생성 테스트")
    void testPreSignedGetUrl() {
        NCPPreSignedGetUrlResult ncpPreSignedGetUrlResult = REAL_NCP_OBJECT_STORAGE_CLIENT.createPreSignedGetUrl(
            UUID.randomUUID().toString());
        assertThat(ncpPreSignedGetUrlResult.preSignedUrl()).isNotBlank();
    }

    @Test
    @DisplayName("preSignedGetUrl 리스트 생성 테스트")
    void testPreSignedGetUrls() {
        final List<String> fakeFileIds = Arrays.asList(UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString());

        List<NCPPreSignedGetUrlResult> ncpPreSignedGetUrlResultList = REAL_NCP_OBJECT_STORAGE_CLIENT.createPreSignedGetUrls(
            fakeFileIds);

        ncpPreSignedGetUrlResultList.forEach(value -> {
            assertThat(value.preSignedUrl()).isNotBlank();
        });
    }
}
