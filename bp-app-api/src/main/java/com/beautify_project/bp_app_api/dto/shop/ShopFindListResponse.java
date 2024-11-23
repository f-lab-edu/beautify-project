package com.beautify_project.bp_app_api.dto.shop;

import java.util.List;
import lombok.Builder;

@Builder
public record ShopFindListResponse(
    String id,
    String name,
    List<String> operations,
    List<String> supportFacilities,
    String rate,
    Integer likes,
    Boolean likePushed,
    String thumbnail
) {

}
