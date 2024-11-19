package com.beautify_project.bp_dto.shop;

import com.beautify_project.bp_dto.shop.enumeration.OrderType;
import com.beautify_project.bp_dto.shop.enumeration.ShopSearchType;

public record ShopFindListRequestParameters(ShopSearchType searchType, Integer page, Integer count,
                                            OrderType orderType) {

}
