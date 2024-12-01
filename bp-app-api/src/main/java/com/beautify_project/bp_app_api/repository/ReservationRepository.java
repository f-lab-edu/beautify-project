package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

}
