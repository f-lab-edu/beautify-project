package com.bp.domain.mysql.entity.embedded;

import jakarta.persistence.Embeddable;
import java.time.LocalTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessTime {

    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalTime breakBeginTime;
    private LocalTime breakEndTime;
    private List<String> offDayOfWeek;

    private BusinessTime(final LocalTime openTime, final LocalTime closeTime,
        final LocalTime breakBeginTime, final LocalTime breakEndTime,
        final List<String> offDayOfWeek) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakBeginTime = breakBeginTime;
        this.breakEndTime = breakEndTime;
        this.offDayOfWeek = offDayOfWeek;
    }

    public static BusinessTime of(final LocalTime openTime, final LocalTime closeTime,
        final LocalTime breakBeginTime, final LocalTime breakEndTime,
        final List<String> offDayOfWeek) {
        return new BusinessTime(openTime, closeTime, breakBeginTime, breakEndTime, offDayOfWeek);
    }

    @Override
    public String toString() {
        return "BusinessTime{" + "openTime=" + openTime + ", closeTime=" + closeTime
            + ", breakBeginTime=" + breakBeginTime + ", breakEndTime=" + breakEndTime
            + ", offDayOfWeek=" + offDayOfWeek + '}';
    }
}
