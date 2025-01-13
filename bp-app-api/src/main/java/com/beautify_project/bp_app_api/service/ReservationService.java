package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.dto.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_mysql.entity.Reservation;
import com.beautify_project.bp_mysql.repository.ReservationRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation findReservationById(final @NotNull String reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.RS001));
    }
}
