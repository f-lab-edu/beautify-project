package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
import com.beautify_project.bp_app_api.dto.shop.ShopFindListRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopFindResult;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationResult;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopService {

    public ResponseMessage registerShop(final ImageFiles images,
        final ShopRegistrationRequest shopRegistrationRequest) {

        return ResponseMessage.createResponseMessage(new ShopRegistrationResult("732e934"));
    }

    public ResponseMessage findShopList(final ShopFindListRequestParameters parameters) {

        return ResponseMessage.createResponseMessage(createFindShopListDummySuccessResponseBody());
    }

    private List<ShopFindResult> createFindShopListDummySuccessResponseBody() {
        final String[] shopIds = {"2360c169", "f4804d31"};

        ShopFindResult result1 = ShopFindResult.builder()
            .id(shopIds[0])
            .name("시술소1")
            .operations(Arrays.asList("두피문신", "눈썹문신", "입술문신"))
            .supportFacilities(Arrays.asList("주차가능", "와이파이", "샤워실"))
            .rate("4.5")
            .likes(132)
            .likePushed(false)
            .build();

        ShopFindResult result2 = ShopFindResult.builder()
            .id(shopIds[1])
            .name("시술소2")
            .operations(List.of("타투"))
            .supportFacilities(List.of("와이파이"))
            .rate("3.0")
            .likes(20)
            .likePushed(true)
            .build();

        return Arrays.asList(result1, result2);
    }
}
