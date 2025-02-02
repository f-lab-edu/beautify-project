package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.repository.ReservationAdapterRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationAdapterRepository reservationAdapterRepository;

    public Reservation findReservationById(final @NotNull Long reservationId) {
        return reservationAdapterRepository.findById(reservationId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.RS001));
    }
}
