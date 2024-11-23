package com.beautify_project.bp_app_api.controller;

import com.beautify_project.bp_app_api.service.ShopService;
import com.beautify_project.bp_dto.common.response.ResponseMessage;
import com.beautify_project.bp_dto.shop.ImageFiles;
import com.beautify_project.bp_dto.shop.ShopFindListRequestParameters;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_dto.shop.enumeration.OrderType;
import com.beautify_project.bp_dto.shop.enumeration.ShopSearchType;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/v1/shops")
    @ResponseStatus(code = HttpStatus.OK)
    ResponseMessage registerShop(
        @RequestPart(value = "images", required = false) final List<MultipartFile> imageFiles,
        @Valid @RequestPart(value = "shopRegistrationInfo") final ShopRegistrationRequest shopRegistrationRequest) {

        return shopService.registerShop(new ImageFiles(imageFiles), shopRegistrationRequest);
    }

    @GetMapping("/v1/shops")
    @ResponseStatus(code = HttpStatus.OK)
    ResponseMessage findShopList(@RequestParam(name = "type") final String searchType,
        @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
        @RequestParam(name = "count", required = false, defaultValue = "10") final Integer count,
        @RequestParam(name = "order", required = false, defaultValue = "asc") final String order)
        throws RuntimeException {

        return shopService.findShopList(
            new ShopFindListRequestParameters(ShopSearchType.from(searchType), page, count,
                OrderType.from(order)));
    }

    // TODO: 샵 상세 조회 구현
    // TODO: 샵 수정 구현
    // TODO: 샵 삭제 구현
    // TODO: 샵 좋아요 구현
    // TODO: 샵 좋아요 취소 구현
}
