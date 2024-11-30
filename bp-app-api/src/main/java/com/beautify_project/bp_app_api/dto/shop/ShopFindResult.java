package com.beautify_project.bp_app_api.dto.shop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalTime;
import java.util.List;

public record ShopFindResult(
    String id,
    String name,
    String contact,
    String url,
    String rate,
    Integer likes,
    Boolean likePushed,
    @JsonInclude(Include.NON_NULL)
    String introduction,
    @JsonInclude(Include.NON_NULL)
    List<String> operations,
    @JsonInclude(Include.NON_NULL)
    List<String> facilities,
    @JsonInclude(Include.NON_NULL)
    List<String> imageUrls
    ) {

    public record BusinessTime (
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime breakBeginTime,
        LocalTime breakEndTime,
        List<String> offDayOfWeek
    ) {}

    public record Address (
        String siDoName,
        String siGoonGooName,
        String eubMyunDongName,
        String roadName,
        String roadMainNum,
        String siGoonGooBuildingName,
        String zipCode,
        String latitude,
        String longitude
    ) {}
}
