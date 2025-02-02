package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
