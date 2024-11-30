package com.beautify_project.bp_app_api.dto.shop;

import com.beautify_project.bp_app_api.entity.Shop;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.stream.Collectors;

public record ShopListFindResult(
    String id,
    String name,
    @JsonInclude(Include.NON_NULL)
    List<String> operations,
    @JsonInclude(Include.NON_NULL)
    List<String> facilities,
    String rate,
    Integer likes,
    Boolean likePushed,
    @JsonInclude(Include.NON_NULL)
    String introduction
) {

    public static ShopListFindResult from(final Shop shop) {
        return new ShopListFindResult(shop.getId(),
            shop.getName(),
            shop.getShopOperations().stream()
                .map(shopOperation -> shopOperation.getOperation().getName()).toList(),
            shop.getShopFacilities().stream()
                .map(shopFacility -> shopFacility.getFacility().getName()).toList(),
            shop.getRate(),
            shop.getLikes(),
            null, // TODO: 사용자 엔티티 구현 후 세팅 추가 필요
            shop.getIntroduction());
    }

    @Override
    public String toString() {
        return "ShopFindResult{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", operations=" + operations.stream().map(Object::toString).collect(Collectors.joining(", ")) +
            ", facilities=" + facilities.stream().map(Object::toString).collect(Collectors.joining(", ")) +
            ", rate='" + rate + '\'' +
            ", likes=" + likes +
            ", likePushed=" + likePushed +
            ", introduction='" + introduction + '\'' +
            '}';
    }
}
