package com.bp.app.api.request.reservation;

import com.bp.utils.FutureOrPresentLong;
import jakarta.validation.constraints.NotNull;

public record ReservationRegistrationRequest(
    @FutureOrPresentLong
    Long startDate,
    @FutureOrPresentLong
    Long endDate,
    @NotNull
    Long shopId,
    @NotNull
    Long operationId,
    @NotNull
    Long operatorId) { }
