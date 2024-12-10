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
    Long likes,
    Boolean likePushed,
    String thumbnailLink
) {

    public static ShopListFindResult of(final Shop shop, final String thumbnailLink) {
        return new ShopListFindResult(shop.getId(), shop.getName(),
            shop.getShopOperations().stream()
                .map(shopOperation -> shopOperation.getOperation().getName()).toList(),
            shop.getShopFacilities().stream()
                .map(shopFacility -> shopFacility.getFacility().getName()).toList(), shop.getRate(),
            shop.getLikes(), null, thumbnailLink); // TODO: 사용자 엔티티 구현 후 likePushed 추가 세팅 필요
    public static ShopListFindResult createShopListFindResult(final Shop shop,
        final List<String> operationNames, final List<String> facilityNames) {
        return new ShopListFindResult(shop.getId(), shop.getName(), operationNames, facilityNames,
            shop.getRate(), shop.getLikes(), null); // TODO: 사용자 엔티티 구현 후 세팅 추가 필요
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
            ", likePushed=" + likePushed + '\'' +
            '}';
    }
}
