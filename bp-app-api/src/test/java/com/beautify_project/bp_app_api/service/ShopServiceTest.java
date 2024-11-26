package com.beautify_project.bp_app_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationResult;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.exception.FileStoreException;
import com.beautify_project.bp_app_api.fixtures.ShopTestFixture;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import jakarta.persistence.PersistenceException;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @InjectMocks
    private ShopService shopService;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private StorageService storageService;

    @BeforeAll
    static void setUp() throws Exception {
        ShopTestFixture.initMockedImageFiles();
    }

    @BeforeEach
    void deleteImageFiles() throws Exception {
        FileUtils.deleteDirectory(new File(ShopTestFixture.TEST_FILE_SYSTEM_DATA_PATH));
    }

    @Test
    @DisplayName("Shop 등록 처리에서 DB 처리 실패하면 모두 실패한다.")
    @Transactional
    void given_shopRegister_when_db_failed_then_get_ErrorResponseMessage() throws Exception {
        // given
        final ImageFiles mockedImages = getMockedImageFiles();
        final ShopRegistrationRequest mockedRequestDto = ShopTestFixture.createValidShopRegistrationRequest();
        when(shopRepository.save(any(Shop.class))).thenThrow(new PersistenceException());

        // when & then
        assertThatThrownBy(
            () -> shopService.registerShop(mockedImages, mockedRequestDto)).isInstanceOf(
            PersistenceException.class);

        assertThat(shopRepository.count()).isEqualTo(0);
        assertThat(getFileCountInTestFileSystemDataPath()).isEqualTo(0);
    }

    @Test
    @DisplayName("Shop 등록 처리에서 DB 처리 성공했으나 Storage 처리에 실패한 경우 모두 실패한다. (DB처리는 rollback 되어야 한다)")
    @Transactional
    void given_shopRegister_when_db_succeed_storage_failed_then_get_ErrorResponseMessage()
        throws Exception {
        // given
        final ImageFiles mockedImages = getMockedImageFiles();
        final ShopRegistrationRequest mockedRequestDto = ShopTestFixture.createValidShopRegistrationRequest();

        doThrow(FileStoreException.class).when(storageService)
            .storeImageFiles(any(ImageFiles.class), any(String.class));

        when(shopRepository.save(any(Shop.class))).thenReturn(Shop.from(mockedRequestDto));

        // when & then
        assertThatThrownBy(
            () -> shopService.registerShop(mockedImages, mockedRequestDto)).isInstanceOf(
            FileStoreException.class);
        assertThat(shopRepository.count()).isEqualTo(0);
        assertThat(getFileCountInTestFileSystemDataPath()).isEqualTo(0);
    }

    @Test
    @DisplayName("Shop 등록 처리에서 DB, Storage 처리 모두 성공한 경우 ShopRegistrationResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    @Transactional
    void given_shopRegister_when_succeed_then_get_ResponseMessage_wrapping_ShopRegistrationResult()
        throws Exception {
        // given
        final ImageFiles mockedImages = getMockedImageFiles();
        final ShopRegistrationRequest mockedRequestDto = ShopTestFixture.createValidShopRegistrationRequest();

        when(shopRepository.save(any(Shop.class))).thenReturn(Shop.from(mockedRequestDto));
        doNothing().when(storageService).storeImageFiles(any(ImageFiles.class), any(String.class));

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedImages, mockedRequestDto);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
    }

    private static ImageFiles getMockedImageFiles() {
        return new ImageFiles(
            ShopTestFixture.MOCKED_IMAGE_FILES.stream().map(file -> (MultipartFile) file).toList()
        );
    }

    private int getFileCountInTestFileSystemDataPath() {
        File directory = new File(ShopTestFixture.TEST_FILE_SYSTEM_DATA_PATH);
        if (!directory.exists()) {
            return 0;
        }
        return (int) Arrays.stream(Objects.requireNonNull(directory.listFiles())).count();
    }
}
