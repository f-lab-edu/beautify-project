package com.beautify_project.bp_app_api.controller;

import com.beautify_project.bp_app_api.enumeration.ShopSearchType;
import com.beautify_project.bp_app_api.request.shop.ShopListFindRequestParameters;
import com.beautify_project.bp_app_api.request.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.response.ResponseMessage;
import com.beautify_project.bp_app_api.service.ShopService;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
import com.beautify_project.bp_common_kafka.producer.ShopLikeEventProducer;
import com.beautify_project.bp_mysql.enums.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final ShopLikeEventProducer eventProducer;

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
    @ResponseStatus(code = HttpStatus.OK)
    public void likeShop(@PathVariable(value = "id") @NotNull final Long shopId) {
        // FIXME: ShopLikeEvent 생성자 파라미터에 사용자 정보 token 에서 넣는 방식으로 수정 필요
        eventProducer.publishShopLikeEvent(shopId, "sssukho@gmail.com", LikeType.LIKE);
    }

    /**
     * Shop 좋아요 취소 이벤트 producer
     */
    @DeleteMapping("/v1/shops/likes/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void cancelLikeShop(@PathVariable(value = "id") @NotNull final Long shopId) {
        // FIXME: ShopLikeCancelEvent 생성자 파라미터에 사용자 정보 token 에서 넣는 방식으로 수정 필요
        eventProducer.publishShopLikeEvent(shopId, "sssukho@gmail.com", LikeType.LIKE_CANCEL);
    }
}
