package com.beautify_project.bp_app_api.dto.shop;


import com.beautify_project.bp_app_api.enumeration.OrderType;
import com.beautify_project.bp_app_api.enumeration.ShopSearchType;
import jakarta.validation.constraints.Size;

public record ShopFindListRequestParameters(
    ShopSearchType searchType,
    @Size(min = 0)
    Integer page,
    @Size(max = 100)
    Integer count,
    OrderType orderType) {
}
