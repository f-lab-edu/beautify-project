package com.bp.app.api.request.shop;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;


public record ShopRegistrationRequest(
    @NotNull @Size(max = 128) String name,
    @NotNull @Size(max = 13) String contact,
    String url,
    @Size(max = 2048) String introduction,
    List<Long> operationIds,
    List<Long> facilityIds,
    List<String> imageFileIds,
    BusinessTime businessTime,
    Address address) {


    public record BusinessTime (
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime breakBeginTime,
        LocalTime breakEndTime,
        List<String> offDayOfWeek){
    }

    public record Address (
        String dongCode,
        String siDoName,
        String siGoonGooName,
        String eubMyunDongName,
        String roadNameCode,
        String roadName,
        String underGround,
        String roadMainNum,
        String roadSubNum,
        String siGoonGooBuildingName,
        String zipCode,
        String apartComplex,
        String eubMyunDongSerialNumber,
        String latitude,
        String longitude) { }
}
