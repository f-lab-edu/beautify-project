package com.bp.app.api.controller;

import com.bp.app.api.request.reservation.ReservationRegistrationRequest;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 사용자의 예약 요청
     */
    @PostMapping("/v1/user/reservations")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage registerReservationAsPendingAndProduceEvent(
        @Valid @RequestBody final ReservationRegistrationRequest request, final Authentication authentication) {
        return reservationService.registerReservationAndProduceEvent(request, (String) authentication.getPrincipal());
    }

    /**
     * 샵 오너의 예약 컨펌
     */
    @PostMapping("/v1/owner/reservations")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage confirmReservation(@NotNull final Long reservationId) {
        return reservationService.confirm(reservationId);
    }

}
