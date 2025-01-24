package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Facility;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {

    List<Facility> findByIdIn(final List<String> ids);

}
