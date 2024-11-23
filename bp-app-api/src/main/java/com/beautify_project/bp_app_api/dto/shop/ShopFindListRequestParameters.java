package com.beautify_project.bp_app_api.dto.shop;


import com.beautify_project.bp_app_api.enumeration.OrderType;
import com.beautify_project.bp_app_api.enumeration.ShopSearchType;

public record ShopFindListRequestParameters(ShopSearchType searchType, Integer page, Integer count,
                                            OrderType orderType) {

}
