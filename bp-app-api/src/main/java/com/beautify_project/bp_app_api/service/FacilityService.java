package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.repository.FacilityRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> findFacilitiesByIds(final List<String> facilityIds) {
        if (facilityIds == null || facilityIds.isEmpty()) {
            return new ArrayList<>();
        }
        return facilityRepository.findByIdIn(facilityIds);
    }
}
