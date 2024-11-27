package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.Facility;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {

    List<Facility> findByIdIn(final List<String> ids);

}
