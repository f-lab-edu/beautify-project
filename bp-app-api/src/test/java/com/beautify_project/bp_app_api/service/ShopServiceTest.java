package com.beautify_project.bp_app_api.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_app_api.entity.Category;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import jakarta.persistence.PersistenceException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @InjectMocks
    private ShopService shopService;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private FacilityService facilityService;

    @Mock
    private OperationService operationService;

    @Test
    @DisplayName("Shop 등록에 실패할 경우 DB에 INSERT 된 결과가 없다.")
    void given_shopRegister_when_db_failed_then_nothingInserted() {
        // given
        Category mockedCategory1 = Category.of("카테고리1", "카테고리1 설명", System.currentTimeMillis());
        Category mockedCategory2 = Category.of("카테고리2", "카테고리2 설명", System.currentTimeMillis());

        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.createOperation("시술1", "시술1 설명", System.currentTimeMillis(),
                List.of(mockedCategory1)),
            Operation.createOperation("시술2", "시술2 설명", System.currentTimeMillis(),
                Arrays.asList(mockedCategory1, mockedCategory2)));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.of("시설1", System.currentTimeMillis()),
            Facility.of("시설2", System.currentTimeMillis())
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

        final ShopRegistrationRequest mockedRequest = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperationIds,
            mockedFacilityIds,
            Arrays.asList("preSigned-url1", "preSigned-url2"),
            new BusinessTime(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                Arrays.asList("monday", "tuesday")),
            new Address(
                "111",
                "서울시",
                "마포구",
                "상암동",
                "481",
                "월드컵북로",
                "true",
                "131",
                "707",
                "오벨리스크",
                "134-070",
                "주상복합",
                "12345678",
                "34",
                "90"
            )
        );


        when(operationService.findOperationsByIds(any(List.class))).thenReturn(mockedOperationEntities);
        when(facilityService.findFacilitiesByIds(any(List.class))).thenReturn(mockedFacilityEntities);
        when(shopRepository.save(any(Shop.class))).thenThrow(new PersistenceException());

        // when & then
        assertThatThrownBy(() -> shopService.registerShop(mockedRequest)).isInstanceOf(
            PersistenceException.class);

    }

    @Test
    @DisplayName("Shop 등록에 실패할 경우 ErrorResponseMessage 를 리턴한다.")
    void given_shopRegister_when_db_failed_then_get_ErrorResponseMessage() {
        // given
//        final ShopRegistrationRequest mockedRequestDto = ShopTestFixture.createValidShopRegistrationRequest();
//        when(shopRepository.save(any(Shop.class))).thenThrow(new PersistenceException());
//
//        // when & then
//        assertThatThrownBy(
//            () -> shopService.registerShop(mockedRequestDto)).isInstanceOf(
//            PersistenceException.class);
//
//        assertThat(shopRepository.count()).isEqualTo(0);
//        assertThat(getFileCountInTestFileSystemDataPath()).isEqualTo(0);
    }

//    @Test
//    @DisplayName("Shop 등록 처리에서 DB, Storage 처리 모두 성공한 경우 ShopRegistrationResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
//    void given_shopRegister_when_succeed_then_get_ResponseMessage_wrapping_ShopRegistrationResult()
//        throws Exception {
//        // given
//        final ShopRegistrationRequest mockedRequestDto = ShopTestFixture.createValidShopRegistrationRequest();
//
//        when(shopRepository.save(any(Shop.class))).thenReturn(ShopTestFixture.MOCKED_VALID_SHOP_ENTITIES[0]);
//
//        // when
//        ResponseMessage responseMessage = shopService.registerShop(mockedRequestDto);
//
//        // then
//        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
//    }

//    @ParameterizedTest
//    @MethodSource("com.beautify_project.bp_app_api.fixtures.ShopTestFixture#validFindShopListParameterInServiceProvider")
//    @DisplayName("Shop 리스트 조회 성공한 경우 ShopFindResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
//    void given_shopFindList_when_succeed_then_get_ResponseMessage_wrapping_ShopFindResult(
//        final ShopListFindRequestParameters parameters) throws Exception {
//        // given
//        List<Shop> mockedShops = Arrays.asList(ShopTestFixture.MOCKED_VALID_SHOP_ENTITIES);
//        Page<Shop> mockedPage = new PageImpl<>(mockedShops);
//        when(shopRepository.findAll(any(Pageable.class))).thenReturn(mockedPage);
//
//        // when
//        ResponseMessage responseMessage = shopService.findShopList(parameters);
//        System.out.println(responseMessage.toString());
//
//        // then
//        assertThat(responseMessage.getReturnValue()).isInstanceOf(List.class);
//        List<ShopListFindResult> results = (List<ShopListFindResult>) responseMessage.getReturnValue();
//        System.out.println(results.get(0).toString());
//    }
}
