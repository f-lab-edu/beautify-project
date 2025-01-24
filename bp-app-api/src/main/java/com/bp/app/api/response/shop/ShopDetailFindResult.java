package com.bp.app.api.response.shop;

import com.bp.domain.mysql.entity.embedded.Address;
import com.bp.domain.mysql.entity.embedded.BusinessTime;
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
