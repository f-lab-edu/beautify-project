package com.beautify_project.bp_app_api.request.shop;


import com.beautify_project.bp_app_api.enumeration.ShopSearchType;
import com.beautify_project.bp_mysql.enums.OrderType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ShopListFindRequestParameters(
    ShopSearchType searchType,
    @Min(0)
    Integer page,
    @Min(0)
    @Max(100)
    Integer count,
    OrderType orderType) {
}
