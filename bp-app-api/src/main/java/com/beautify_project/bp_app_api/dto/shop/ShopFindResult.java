package com.beautify_project.bp_app_api.dto.shop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;

@Builder
public record ShopFindResult(
    String id,
    String name,
    @JsonInclude(Include.NON_NULL)
    List<String> operations,
    @JsonInclude(Include.NON_NULL)
    List<String> supportFacilities,
    String rate,
    Integer likes,
    Boolean likePushed,
    @JsonInclude(Include.NON_NULL)
    String content,
    @JsonInclude(Include.NON_NULL)
    String thumbnail) {

}
