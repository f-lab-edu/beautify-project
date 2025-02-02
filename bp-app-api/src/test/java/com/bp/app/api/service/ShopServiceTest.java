package com.bp.app.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bp.app.api.provider.image.ImageProvider;
import com.bp.app.api.request.shop.ShopRegistrationRequest;
import com.bp.app.api.request.shop.ShopRegistrationRequest.Address;
import com.bp.app.api.request.shop.ShopRegistrationRequest.BusinessTime;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.shop.ShopRegistrationResult;
import com.bp.app.api.service.ShopCategoryService;
import com.bp.app.api.service.ShopFacilityService;
import com.bp.app.api.service.ShopOperationService;
import com.bp.app.api.service.ShopService;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
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
    private ShopAdapterRepository shopAdapterRepository;

    @Mock
    private ShopOperationService shopOperationService;

    @Mock
    private ShopFacilityService shopFacilityService;

    @Mock
    private ShopCategoryService shopCategoryService;

    @Test
    @DisplayName("Shop 등록 요청시 모든 필드값이 있는 경우 등록에 성공 후 ResponseMessage 를 리턴한다.")
    void given_shopRegisterWithAllFields_when_succeed_then_returnResponseMessageWrappingShopId() {
        // given
        final List<Long> mockedOperationIds = Arrays.asList(1L, 2L);
        final List<Long> mockedFacilityIds = Arrays.asList(1L, 2L);

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

        final Shop mockedRegisteredShop = ShopService.createShopEntityFromShopRegistrationRequest(mockedRequest);
        when(shopAdapterRepository.save(any(Shop.class))).thenReturn(mockedRegisteredShop);
        when(shopOperationService.registerShopOperations(any(), any())).thenReturn(null);
        when(shopCategoryService.registerShopCategories(any(), any())).thenReturn(null);
        when(shopFacilityService.registerShopFacilities(any(), any())).thenReturn(null);

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedRequest);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
        verify(shopAdapterRepository, times(1)).save(any(Shop.class));
        verify(shopOperationService, times(1)).registerShopOperations(any(), anyList());
        verify(shopCategoryService, times(1)).registerShopCategories(any(), anyList());
        verify(shopFacilityService, times(1)).registerShopFacilities(any(), anyList());
    }

    @Test
    @DisplayName("Shop 등록 요청 데이터에 시술 아이디가 없는 경우에도 등록에 성공 후 ResponseMessage 를 리턴한다.")
    void given_shopRegisterWithoutOperation_when_succeed_then_returnResponseMessageWrappingShopId() {
        // given
        final List<Long> mockedFacilityIds = Arrays.asList(1L, 2L);

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

        final Shop mockedRegisteredShop = ShopService.createShopEntityFromShopRegistrationRequest(mockedRequest);

        when(shopAdapterRepository.save(any(Shop.class))).thenReturn(mockedRegisteredShop);
        when(shopFacilityService.registerShopFacilities(any(), anyList())).thenReturn(null);

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedRequest);

        // when & then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
        verify(shopAdapterRepository, times(1)).save(any(Shop.class));
        verify(shopFacilityService, times(1)).registerShopFacilities(any(), anyList());
    }

    @Test
    @DisplayName("Shop 등록 요청 데이터에 지원 시설 아이디가 없어도 등록에 성공 후 ResponseMessage 를 리턴한다.")
    void given_shopRegisterWithoutFacility_when_succeed_then_returnResponseMessageWrappingShopId() {
        // given
        final List<Long> mockedOperationIds = Arrays.asList(1L, 2L);

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

        when(shopAdapterRepository.save(any(Shop.class))).thenReturn(mockedRegisteredShop);
        when(shopOperationService.registerShopOperations(any(), anyList())).thenReturn(null);
        when(shopCategoryService.registerShopCategories(any(), anyList())).thenReturn(null);

        // when
        ResponseMessage responseMessage = shopService.registerShop(mockedRequest);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ShopRegistrationResult.class);
        verify(shopAdapterRepository, times(1)).save(any(Shop.class));
        verify(shopOperationService, times(1)).registerShopOperations(any(), anyList());
        verify(shopCategoryService, times(1)).registerShopCategories(any(), anyList());
    }
}
