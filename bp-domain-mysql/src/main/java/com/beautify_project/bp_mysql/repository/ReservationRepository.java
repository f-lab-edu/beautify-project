package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

}
