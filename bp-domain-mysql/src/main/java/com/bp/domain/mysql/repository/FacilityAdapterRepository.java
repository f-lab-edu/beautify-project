package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Facility;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FacilityAdapterRepository {

    private final FacilityRepository defaultRepository;

    public List<Facility> findByIdIn(final List<Long> facilityIdsToFind) {
        return defaultRepository.findByIdIn(facilityIdsToFind);
    }

    public Optional<Facility> findById(final Long facilityId) {
        return defaultRepository.findById(facilityId);
    }

    public List<Facility> saveAll(final List<Facility> facilitiesToSave) {
        return defaultRepository.saveAll(facilitiesToSave);
    }
}
