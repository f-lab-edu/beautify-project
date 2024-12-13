package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.enumeration.EntityType;
import com.beautify_project.bp_app_api.exception.InvalidIdException;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.FacilityRepository;
import com.beautify_project.bp_app_api.utils.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> findFacilitiesByIds(final List<String> facilityIdsToFind) {
        Validator.throwIfNullOrEmpty(facilityIdsToFind,
            new InvalidIdException(EntityType.FACILITY, "facilityId", "null"));

        final List<Facility> foundFacilities = facilityRepository.findByIdIn(facilityIdsToFind);
        validateFoundFacilitiesHaveFacilityIdsToFind(facilityIdsToFind, foundFacilities);
        return foundFacilities;
    }

    private void validateFoundFacilitiesHaveFacilityIdsToFind(final List<String> facilityIdsToFind,
        final List<Facility> foundFacilities) {
        if (facilityIdsToFind.size() == foundFacilities.size()) {
            return;
        }

        final String notExistedId = extractNotExistedId(facilityIdsToFind, foundFacilities);
        throw new InvalidIdException(EntityType.FACILITY, "facilityId", notExistedId);
    }

    private static String extractNotExistedId(final List<String> facilityIdsToFind,
        final List<Facility> foundFacilities) {
        final Set<String> foundFacilitiesIdSet = foundFacilities.stream()
            .map(Facility::getId)
            .collect(Collectors.toSet());

        return facilityIdsToFind.stream()
            .filter(idToFind -> !foundFacilitiesIdSet.contains(idToFind))
            .findFirst().orElseGet(() -> "null");
    }

    public Facility findFacilityById(final String facilityId) {
        return facilityRepository.findById(facilityId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.FA001));
    }
}
