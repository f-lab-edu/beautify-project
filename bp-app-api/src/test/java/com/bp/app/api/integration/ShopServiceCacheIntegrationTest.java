package com.bp.app.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bp.app.api.config.IOBoundAsyncThreadPoolConfiguration;
import com.bp.app.api.enumeration.ShopSearchType;
import com.bp.app.api.provider.image.ImageProvider;
import com.bp.app.api.request.shop.ShopListFindRequestParameters;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.image.PreSignedGetUrlResult;
import com.bp.app.api.response.shop.ShopListFindResult;
import com.bp.app.api.service.FacilityService;
import com.bp.app.api.service.OperationService;
import com.bp.app.api.service.ShopCategoryService;
import com.bp.app.api.service.ShopFacilityService;
import com.bp.app.api.service.ShopLikeService;
import com.bp.app.api.service.ShopOperationService;
import com.bp.app.api.service.ShopService;
import com.bp.app.api.testcontainers.TestContainerFactory;
import com.bp.cache.config.RedisAutoConfiguration;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.ShopFacility;
import com.bp.domain.mysql.entity.ShopOperation;
import com.bp.domain.mysql.entity.embedded.Address;
import com.bp.domain.mysql.entity.embedded.BusinessTime;
import com.bp.domain.mysql.enums.OrderType;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@Testcontainers
@Import({RedisAutoConfiguration.class, ShopService.class})
@MockBeans({
    @MockBean(OperationService.class),
    @MockBean(FacilityService.class),
    @MockBean(ShopCategoryService.class),
    @MockBean(IOBoundAsyncThreadPoolConfiguration.class),
    @MockBean(ShopLikeService.class)
})
public class ShopServiceCacheIntegrationTest {

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = TestContainerFactory.createRedisContainer();

    @Autowired
    ShopService shopService;

    @MockBean
    ShopAdapterRepository mockedShopAdapterRepository;

    @MockBean
    ShopOperationService mockedShopOperationService;

    @MockBean
    ShopFacilityService mockedShopFacilityService;

    @MockBean
    ImageProvider mockedImageProvider;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        TestContainerFactory.overrideCacheProps(registry, REDIS_CONTAINER);
    }

    @Test
    @DisplayName("location 기준 샵 목록 조회시 캐시에서 데이터를 가져온다.")
    void getShopListFromCacheIfCalledByLocation() {
        // given
        Shop mockedShop = Shop.newShop("미용시술소1", "010-1234-5678", "www.naver.com", "소개글",
            Arrays.asList("presigned-url1", "presigned-url2"),
            Address.of("dongCode", "sidoname", "sigoongooname",
                "eubmyundongname", "roadnamecode", "roadName",
                "underGround", "roadMainNum", "roadSubNum",
                "sigoongoobuildingname", "zipcode", "appartComplex",
                "eubmyundongserialnumber", "latitude", "longitude"),
            BusinessTime.of(LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(),
                List.of("monday")));

        List<Shop> mockedCachedShopList = Arrays.asList(mockedShop);
        when(mockedShopAdapterRepository.findAll(any(), any(), any(), any())).thenReturn(mockedCachedShopList);
        when(mockedShopOperationService.findShopOperationsByShopIds(any())).thenReturn(
            Arrays.asList(ShopOperation.newShopOperation(1L, 1L)));
        when(mockedShopFacilityService.findShopFacilitiesByShopIds(any())).thenReturn(
            Arrays.asList(ShopFacility.newShopFacility(1L, 1L)));
        when(mockedImageProvider.providePreSignedGetUrlByFileId(any())).thenReturn(
            new PreSignedGetUrlResult("thumbnail link"));

        ShopListFindRequestParameters mockedRequestParams = new ShopListFindRequestParameters(
            ShopSearchType.LOCATION, 0, 10, OrderType.DESC);

        // cache miss => 캐싱 처리
        shopService.findShopList(mockedRequestParams);

        // when
        // cache hit
        ResponseMessage response = shopService.findShopList(mockedRequestParams);
        ShopListFindResult result = ((List<ShopListFindResult>) response.getReturnValue()).get(0);

        // then
        verify(mockedShopAdapterRepository, times(1)).findAll(any(), any(), any(), any());
        assertEquals(mockedShop.getName(), result.name());
        assertEquals(mockedShop.getLikes(), result.likes());
        assertEquals(mockedShop.getRate(), result.rate());
    }

    @Test
    @DisplayName("location 기준이 아닌 샵 목록 조회시 DB 에서 데이터를 가져온다.")
    void getShopListFromDBIfCalledByNotLocation() {
        // given
        Shop mockedShop = Shop.newShop("미용시술소1", "010-1234-5678", "www.naver.com", "소개글",
            Arrays.asList("presigned-url1", "presigned-url2"),
            Address.of("dongCode", "sidoname", "sigoongooname",
                "eubmyundongname", "roadnamecode", "roadName",
                "underGround", "roadMainNum", "roadSubNum",
                "sigoongoobuildingname", "zipcode", "appartComplex",
                "eubmyundongserialnumber", "latitude", "longitude"),
            BusinessTime.of(LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(),
                List.of("monday")));

        List<Shop> mockedCachedShopList = Arrays.asList(mockedShop);
        when(mockedShopAdapterRepository.findAll(any(), any(), any(), any())).thenReturn(
            mockedCachedShopList);
        when(mockedShopOperationService.findShopOperationsByShopIds(any())).thenReturn(
            Arrays.asList(ShopOperation.newShopOperation(1L, 1L)));
        when(mockedShopFacilityService.findShopFacilitiesByShopIds(any())).thenReturn(
            Arrays.asList(ShopFacility.newShopFacility(1L, 1L)));
        when(mockedImageProvider.providePreSignedGetUrlByFileId(any())).thenReturn(
            new PreSignedGetUrlResult("thumbnail link"));

        ShopListFindRequestParameters mockedRequestParams = new ShopListFindRequestParameters(
            ShopSearchType.SHOP_NAME, 0, 10, OrderType.DESC);

        // cache miss => 캐싱 처리
        shopService.findShopList(mockedRequestParams);

        // when
        // cache hit
        ResponseMessage response = shopService.findShopList(mockedRequestParams);
        ShopListFindResult result = ((List<ShopListFindResult>) response.getReturnValue()).get(0);

        // then
        verify(mockedShopAdapterRepository, times(2)).findAll(any(), any(), any(), any());
        assertEquals(mockedShop.getName(),result.name());
        assertEquals(mockedShop.getLikes(), result.likes());
        assertEquals(mockedShop.getRate(), result.rate());
    }
}
