package com.bp.app.api.controller;

import com.bp.app.api.enumeration.ShopSearchType;
import com.bp.app.api.producer.ShopLikeEventProducer;
import com.bp.app.api.request.shop.ShopListFindRequestParameters;
import com.bp.app.api.request.shop.ShopRegistrationRequest;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.service.ShopService;
import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
import com.bp.domain.mysql.enums.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ShopLikeEventProducer eventProducer;

    /**
     * Shop 등록
     */
    @PostMapping("/v1/owner/shops")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage registerShop(
        @Valid @RequestBody final ShopRegistrationRequest shopRegistrationRequest) {

        return shopService.registerShop(shopRegistrationRequest);
    }

    /**
     * Shop 리스트 조회
     */
    @GetMapping("/v1/shops")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage findShopList(@RequestParam(name = "type") final String searchType,
        @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
        @RequestParam(name = "count", required = false, defaultValue = "10") final Integer count,
        @RequestParam(name = "order", required = false, defaultValue = "asc") final String order) {

        return shopService.findShopList(
            new ShopListFindRequestParameters(ShopSearchType.from(searchType), page, count,
                OrderType.from(order)));
    }

    // TODO: 샵 상세 조회 구현
    // TODO: 샵 수정 구현
    // TODO: 샵 삭제 구현

    /**
     * Shop 좋아요 이벤트 producer
     */
    @PostMapping("/v1/shops/likes/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void likeShop(@PathVariable(value = "id") @NotNull final Long shopId,
        final Authentication authentication) {
        eventProducer.publishShopLikeEvent(shopId, (String) authentication.getPrincipal(),
            LikeType.LIKE);
    }

    /**
     * Shop 좋아요 취소 이벤트 producer
     */
    @DeleteMapping("/v1/shops/likes/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void cancelLikeShop(@PathVariable(value = "id") @NotNull final Long shopId,
        final Authentication authentication) {
        eventProducer.publishShopLikeEvent(shopId, (String) authentication.getPrincipal(),
            LikeType.LIKE_CANCEL);
    }
}
