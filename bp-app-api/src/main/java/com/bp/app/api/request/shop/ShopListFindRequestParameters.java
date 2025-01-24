package com.bp.app.api.request.shop;


import com.bp.app.api.enumeration.ShopSearchType;
import com.bp.domain.mysql.enums.OrderType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ShopListFindRequestParameters(
    ShopSearchType searchType,
    @Min(0)
    Integer page,
    @Min(0)
    @Max(100)
    Integer count,
    OrderType orderType) {
}
