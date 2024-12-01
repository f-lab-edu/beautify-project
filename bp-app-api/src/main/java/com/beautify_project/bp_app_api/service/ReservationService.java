package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorCode;
import com.beautify_project.bp_app_api.entity.Reservation;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.ReservationRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation findReservationById(final @NotNull String reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new NotFoundException(
                ErrorCode.RS001));
    }
}
