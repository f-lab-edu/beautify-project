package com.beautify_project.bp_app_api.dto.shop;

import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
import java.util.List;

public record ShopDetailFindResult(
    String id,
    String name,
    String contact,
    String url,
    String introduction,
    String likePushed,
    List<String> operations,
    BusinessTime businessTime,
    Address address,
    List<String> categories,
    List<String> supportFacilities,
    String rate,
    Integer likes,
    List<String> images
) {

}
