package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.enumeration.EntityType;
import com.beautify_project.bp_app_api.exception.InvalidIdException;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.OperationRepository;
import com.beautify_project.bp_app_api.utils.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;

    public List<Operation> findOperationsByIds(final List<String> operationIdsToFind) {
        Validator.throwIfNullOrEmpty(operationIdsToFind,
            new InvalidIdException(EntityType.OPERATION, "operationId", "null"));

        final List<Operation> foundOperations = operationRepository.findByIdIn(operationIdsToFind);
        validateFoundOperationsHaveOperationIdsToFind(operationIdsToFind, foundOperations);
        
        return foundOperations;
    }

    private void validateFoundOperationsHaveOperationIdsToFind(final List<String> operationIdsToFind,
        final List<Operation> foundOperations) {
        if (operationIdsToFind.size() == foundOperations.size()) {
            return;
        }

        final String notExistedId = extractNotExistedId(operationIdsToFind, foundOperations);
        throw new InvalidIdException(EntityType.OPERATION, "operationId", notExistedId);
    }

    private static String extractNotExistedId(final List<String> operationIdsToFind,
        final List<Operation> foundOperations) {
        final Set<String> foundOperationsIdSet = foundOperations.stream()
            .map(Operation::getId).collect(
                Collectors.toSet());

        return operationIdsToFind.stream()
            .filter(idToFind -> !foundOperationsIdSet.contains(idToFind))
            .findFirst().orElseGet(() -> "null");
    }

    public Operation findOperationById(final String operationId) {
        return operationRepository.findById(operationId).orElseThrow(() -> new NotFoundException(
            ErrorCode.OP001));
    }
}
