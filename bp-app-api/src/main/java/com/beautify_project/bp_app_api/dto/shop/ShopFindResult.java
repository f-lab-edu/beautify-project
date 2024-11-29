package com.beautify_project.bp_app_api.dto.shop;

import com.beautify_project.bp_app_api.entity.Shop;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;

@Builder
public record ShopFindResult(
    String id,
    String name,
    @JsonInclude(Include.NON_NULL)
    List<String> operations,
    @JsonInclude(Include.NON_NULL)
    List<String> supportFacilities,
    String rate,
    Integer likes,
    Boolean likePushed,
    String url,
    @JsonInclude(Include.NON_NULL)
    String introduction,
    @JsonInclude(Include.NON_NULL)
    String thumbnail) {



    public static ShopFindResult createShopFindResult(final Shop shop, final String thumbnail) {
        // TODO: likePushed 는 사용자 정보까지 같이 포함해서 조회필요
        return ShopFindResult.builder()
            .id(shop.getId())
            .name(shop.getName())
            .operations(shop.getShopOperations().stream()
                .map(shopOperation -> shopOperation.getOperation().getName()).toList())
            .supportFacilities(shop.getShopFacilities().stream()
                .map(shopFacility -> shopFacility.getFacility().getName()).toList())
            .rate(shop.getRate())
            .likes(shop.getLikes())
            .introduction(shop.getIntroduction())
            .url(shop.getUrl())
            .thumbnail(thumbnail)
            .build();
    }
}
