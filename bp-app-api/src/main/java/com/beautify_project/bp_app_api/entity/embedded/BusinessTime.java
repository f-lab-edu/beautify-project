package com.beautify_project.bp_app_api.entity.embedded;

import jakarta.persistence.Embeddable;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Embeddable
@Builder
public record BusinessTime(
    LocalTime openTime,
    LocalTime closeTime,
    LocalTime breakBeginTime,
    LocalTime breakEndTime,
    List<String> offDayOfWeek) {

    @Override
    public String toString() {
        return "BusinessTime{" +
            "openTime=" + openTime +
            ", closeTime=" + closeTime +
            ", breakBeginTime=" + breakBeginTime +
            ", breakEndTime=" + breakEndTime +
            ", offDayOfWeek=" + offDayOfWeek +
            '}';
    }
}
