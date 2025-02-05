package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Reservation;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationAdapterRepository {

    private final ReservationRepository defaultRepository;

    @ReadOnlyTransactional
    public Optional<Reservation> findById(final Long reservationIdToFind) {
        return defaultRepository.findById(reservationIdToFind);
    }

    public Reservation saveAndFlush(final Reservation reservationToSave) {
        return defaultRepository.saveAndFlush(reservationToSave);
    }

    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }

    public Reservation save(final Reservation reservation) {
        return defaultRepository.save(reservation);
    }
}
