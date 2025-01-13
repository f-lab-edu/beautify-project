package com.beautify_project.bp_app_api.dto.shop;

import com.beautify_project.bp_mysql.entity.embedded.Address;
import com.beautify_project.bp_mysql.entity.embedded.BusinessTime;
import java.util.List;

public record ShopDetailFindResult(
    String id,
    String name,
    String contact,
    String url,
    String introduction,
    String likePushed,
    String rate,
    Integer likes,
    List<String> operations,
    List<String> categories,
    List<String> supportFacilities,
    BusinessTime businessTime,
    Address address,
    List<String> imageUrls
) {

}
