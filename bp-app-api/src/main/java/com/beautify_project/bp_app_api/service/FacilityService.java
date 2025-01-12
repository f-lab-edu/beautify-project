package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.response.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_mysql.entity.Facility;
import com.beautify_project.bp_mysql.repository.FacilityRepository;
import com.beautify_project.bp_utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> findFacilitiesByIds(final List<String> facilityIdsToFind) {
        Validator.throwIfNullOrEmpty(facilityIdsToFind, new BpCustomException(ErrorCode.BR001));
        return facilityRepository.findByIdIn(facilityIdsToFind);
    }

    public Facility findFacilityById(final String facilityId) {
        return facilityRepository.findById(facilityId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.FA001));
    }
}
