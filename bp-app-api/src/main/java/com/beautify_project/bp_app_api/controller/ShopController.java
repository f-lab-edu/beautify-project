package com.beautify_project.bp_app_api.controller;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.event.ShopLikeCancelEvent;
import com.beautify_project.bp_app_api.dto.event.ShopLikeEvent;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.enumeration.OrderType;
import com.beautify_project.bp_app_api.enumeration.ShopSearchType;
import com.beautify_project.bp_app_api.service.ShopService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    /**
     * Shop 등록
     */
    @PostMapping("/v1/shops")
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
    public DeferredResult<?> likeShop(@PathVariable(value = "id") @NotBlank final String shopId) {
        final DeferredResult<Object> deferredResult = new DeferredResult<>();
        shopService.produceShopLikeEvent(deferredResult, shopId, "sssukho@gmail.com");
        return deferredResult;
    }

    /**
     * Shop 좋아요 이벤트 처리 (consumer 는 bp-kafka-consumer 에서 처리)
     */
    @PostMapping("/v1/shops/likes")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void batchShopLikes(@Valid @RequestBody final List<ShopLikeEvent> shopLikeEvents) {
        shopService.batchShopLikes(shopLikeEvents);
    }

    /**
     * Shop 좋아요 취소 이벤트 producer
     */
    @DeleteMapping("/v1/shops/likes/{id}")
    public DeferredResult<?> cancelLikeShop(@PathVariable(value = "id") @NotBlank final String shopId) {
        final DeferredResult<Object> deferredResult = new DeferredResult<>();
        shopService.produceShopLikeCancelEvent(deferredResult, shopId, "sssukho@gmail.com");
        return deferredResult;
    }

    /**
     * Shop 좋아요 취소 이벤트 처리 (consumer 는 bp-kafka-consumer 에서 처리)
     */
    @DeleteMapping("/v1/shops/likes")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void batchShopLikeCancel(@Valid @RequestBody final List<ShopLikeCancelEvent> shopLikeCancelEvents) {
        shopService.batchShopLikesCancel(shopLikeCancelEvents);
    }
}
