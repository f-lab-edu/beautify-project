package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Facility;
import com.bp.domain.mysql.repository.FacilityRepository;
import com.bp.utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> findFacilitiesByIds(final List<Long> facilityIdsToFind) {
        Validator.throwIfNullOrEmpty(facilityIdsToFind, new BpCustomException(ErrorCode.BR001));
        return facilityRepository.findByIdIn(facilityIdsToFind);
    }

    public Facility findFacilityById(final Long facilityId) {
        return facilityRepository.findById(facilityId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.FA001));
    }
}
