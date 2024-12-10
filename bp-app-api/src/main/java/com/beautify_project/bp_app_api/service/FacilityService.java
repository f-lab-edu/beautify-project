package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.FacilityRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> findFacilitiesByIds(final List<String> facilityIds) {
        if (facilityIds == null || facilityIds.isEmpty()) {
            throw new NotFoundException(ErrorCode.FA001);
        }
        return facilityRepository.findByIdIn(facilityIds);
    }

    public Facility findFacilityById(final String facilityId) {
        return facilityRepository.findById(facilityId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.FA001));
    }
}
