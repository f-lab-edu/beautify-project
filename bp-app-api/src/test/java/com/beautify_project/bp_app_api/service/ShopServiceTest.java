package com.beautify_project.bp_app_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.dto.shop.ShopFindListRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindResult;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationResult;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.exception.FileStoreException;
import com.beautify_project.bp_app_api.fixtures.ShopTestFixture;
import com.beautify_project.bp_app_api.repository.CategoryRepository;
import com.beautify_project.bp_app_api.repository.FacilityRepository;
import com.beautify_project.bp_app_api.repository.OperationRepository;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import jakarta.persistence.PersistenceException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @InjectMocks
    private ShopService shopService;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private StorageService storageService;

    @BeforeAll
    static void setUp() throws Exception {
        ShopTestFixture.initMockedImageFiles();
        ShopTestFixture.initMockedFindListSuccessResponseMessage();
        ShopTestFixture.initMockedValidShopEntitiesIfNotInitialized();
    }

    @BeforeEach
    void deleteImageFiles() throws Exception {
        FileUtils.deleteDirectory(new File(ShopTestFixture.TEST_FILE_SYSTEM_DATA_PATH));
    }

    @Test
    @DisplayName("Shop 등록 처리에서 DB 처리 실패하면 모두 실패한다.")
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
    @DisplayName("Shop 등록 처리에서 DB 처리 성공했으나 Storage 처리에 실패한 경우 DB는 롤백하고 Storage 에는 저장되지 않아야 한다.")
    void given_shopRegister_when_db_succeed_storage_failed_then_get_ErrorResponseMessage()
        throws Exception {
        // given
        final ImageFiles mockedImages = getMockedImageFiles();
        final ShopRegistrationRequest mockedRequestDto = ShopTestFixture.createValidShopRegistrationRequest();

        doThrow(FileStoreException.class).when(storageService)
            .storeImageFiles(any(ImageFiles.class), any(String.class));

        when(shopRepository.save(any(Shop.class))).thenReturn(
            ShopTestFixture.MOCKED_VALID_SHOP_ENTITIES[0]);

        // when & then
        assertThatThrownBy(
            () -> shopService.registerShop(mockedImages, mockedRequestDto)).isInstanceOf(
            FileStoreException.class);
        assertThat(shopRepository.count()).isEqualTo(0);
        assertThat(getFileCountInTestFileSystemDataPath()).isEqualTo(0);
    }

    @Test
    @DisplayName("Shop 등록 처리에서 DB, Storage 처리 모두 성공한 경우 ShopRegistrationResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void given_shopRegister_when_succeed_then_get_ResponseMessage_wrapping_ShopRegistrationResult()
        throws Exception {
        // given
        final ImageFiles mockedImages = getMockedImageFiles();
        final ShopRegistrationRequest mockedRequestDto = ShopTestFixture.createValidShopRegistrationRequest();

        when(shopRepository.save(any(Shop.class))).thenReturn(ShopTestFixture.MOCKED_VALID_SHOP_ENTITIES[0]);
        doNothing().when(storageService).storeImageFiles(any(ImageFiles.class), any(String.class));

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedImages, mockedRequestDto);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
    }

    @ParameterizedTest
    @MethodSource("com.beautify_project.bp_app_api.fixtures.ShopTestFixture#validFindShopListParameterInServiceProvider")
    @DisplayName("Shop 리스트 조회 성공한 경우 ShopFindResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void given_shopFindList_when_succeed_then_get_ResponseMessage_wrapping_ShopFindResult(
        final ShopFindListRequestParameters parameters) throws Exception {
        // given
        List<Shop> mockedShops = Arrays.asList(ShopTestFixture.MOCKED_VALID_SHOP_ENTITIES);
        Page<Shop> mockedPage = new PageImpl<>(mockedShops);
        when(shopRepository.findAll(any(Pageable.class))).thenReturn(mockedPage);
        doReturn(ShopTestFixture.MOCKED_IMAGE_FILES.get(0).getBytes()).when(storageService).loadThumbnail(any(String.class));

        // when
        ResponseMessage responseMessage = shopService.findShopList(parameters);
        System.out.println(responseMessage.toString());

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(List.class);
        List<ShopListFindResult> results = (List<ShopListFindResult>) responseMessage.getReturnValue();
        System.out.println(results.get(0).toString());
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
