package com.beautify_project.bp_app_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beautify_project.bp_app_api.enumeration.ShopSearchType;
import com.beautify_project.bp_app_api.provider.image.ImageProvider;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_app_api.dto.ResponseMessage;
import com.beautify_project.bp_app_api.dto.image.PreSignedGetUrlResult;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationResult;
import com.beautify_project.bp_mysql.entity.Facility;
import com.beautify_project.bp_mysql.entity.Operation;
import com.beautify_project.bp_mysql.entity.Shop;
import com.beautify_project.bp_mysql.enums.OrderType;
import com.beautify_project.bp_mysql.repository.ShopRepository;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @InjectMocks
    private ShopService shopService;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ShopOperationService shopOperationService;

    @Mock
    private ShopFacilityService shopFacilityService;

    @Mock
    private ShopCategoryService shopCategoryService;

    @Mock
    private ImageProvider imageProvider;

    @Test
    @DisplayName("Shop 등록 요청시 모든 필드값이 있는 경우 등록에 성공 후 ResponseMessage 를 리턴한다.")
    void given_shopRegisterWithAllFields_when_succeed_then_returnResponseMessageWrappingShopId() {
        // given
        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
            Operation.of("점 제거 시술", "점 제거 시술 설명"));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.withName("와이파이"),
            Facility.withName("샤워실")
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

        Shop mockedRegisteredShop = ShopService.createShopEntityFromShopRegistrationRequest(mockedRequest);
        when(shopRepository.save(any(Shop.class))).thenReturn(mockedRegisteredShop);
        when(shopOperationService.registerShopOperations(any(String.class), anyList())).thenReturn(
            null);
        when(shopCategoryService.registerShopCategories(any(String.class), anyList())).thenReturn(
            null);
        when(shopFacilityService.registerShopFacilities(any(String.class), anyList())).thenReturn(
            null);

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedRequest);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
        verify(shopRepository, times(1)).save(any(Shop.class));
        verify(shopOperationService, times(1)).registerShopOperations(any(String.class), anyList());
        verify(shopCategoryService, times(1)).registerShopCategories(any(String.class), anyList());
        verify(shopFacilityService, times(1)).registerShopFacilities(any(String.class), anyList());
    }

    @Test
    @DisplayName("Shop 등록 요청 데이터에 시술 아이디가 없는 경우에도 등록에 성공 후 ResponseMessage 를 리턴한다.")
    void given_shopRegisterWithoutOperation_when_succeed_then_returnResponseMessageWrappingShopId() {
        // given
        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.withName("시설1"),
            Facility.withName("시설2")
        );

        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

        final ShopRegistrationRequest mockedRequest = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            null,
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

        Shop mockedRegisteredShop = ShopService.createShopEntityFromShopRegistrationRequest(mockedRequest);

        when(shopRepository.save(any(Shop.class))).thenReturn(mockedRegisteredShop);
        when(shopFacilityService.registerShopFacilities(any(String.class), anyList())).thenReturn(
            null);

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedRequest);

        // when & then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
        verify(shopRepository, times(1)).save(any(Shop.class));
        verify(shopFacilityService, times(1)).registerShopFacilities(any(String.class), anyList());
    }

    @Test
    @DisplayName("Shop 등록 요청 데이터에 지원 시설 아이디가 없어도 등록에 성공 후 ResponseMessage 를 리턴한다.")
    void given_shopRegisterWithoutFacility_when_succeed_then_returnResponseMessageWrappingShopId() {
        // given
        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
            Operation.of("점 제거 시술", "점 제거 시술 설명"));

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());

        final ShopRegistrationRequest mockedRequest = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperationIds,
            null,
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

        Shop mockedRegisteredShop = ShopService.createShopEntityFromShopRegistrationRequest(mockedRequest);

        when(shopRepository.save(any(Shop.class))).thenReturn(mockedRegisteredShop);
        when(shopOperationService.registerShopOperations(any(String.class), anyList())).thenReturn(
            null);
        when(shopCategoryService.registerShopCategories(any(String.class), anyList())).thenReturn(
            null);

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedRequest);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
        verify(shopRepository, times(1)).save(any(Shop.class));
        verify(shopOperationService, times(1)).registerShopOperations(any(String.class), anyList());
        verify(shopCategoryService, times(1)).registerShopCategories(any(String.class), anyList());
    }

    @ParameterizedTest
    @MethodSource("validFindShopListParameterInServiceProvider")
    @DisplayName("Shop 리스트 조회 성공시 ShopFindResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void given_shopListFind_when_succeed_then_returnResponseMessageWrappingShopFindResult(final
    ShopListFindRequestParameters parameters) {
        // given
        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
            Operation.of("점 제거 시술", "점 제거 시술 설명"));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.withName("와이파이"),
            Facility.withName("샤워실")
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

        Shop mockedRegisteredShop = ShopService.createShopEntityFromShopRegistrationRequest(mockedRequest);

        List<Shop> mockedShopList = List.of(mockedRegisteredShop);
        Page<Shop> mockedPage = new PageImpl<>(mockedShopList);
        when(shopRepository.findAll(any(Pageable.class))).thenReturn(mockedPage);
        when(imageProvider.providePreSignedGetUrlByFileId(any(String.class))).thenReturn(
            new PreSignedGetUrlResult("presigned-get-url"));

        // when
        ResponseMessage responseMessage = shopService.findShopList(parameters);

        // then
        verify(shopRepository, times(1)).findAll(any(Pageable.class));
        assertThat(responseMessage.getReturnValue()).isInstanceOf(List.class);
    }

    private static Stream<Arguments> validFindShopListParameterInServiceProvider() {

        return Stream.of(
            Arguments.arguments(
                new ShopListFindRequestParameters(ShopSearchType.SHOP_NAME, 0, 5, OrderType.ASC),
                new ShopListFindRequestParameters(ShopSearchType.LIKE, 0, 10, OrderType.ASC),
                new ShopListFindRequestParameters(ShopSearchType.RATE, 0, 15, OrderType.DESC),
                new ShopListFindRequestParameters(ShopSearchType.LOCATION, 0, 100, OrderType.DESC)
            )
        );
    }
}
