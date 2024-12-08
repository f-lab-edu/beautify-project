package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.OperationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;

    public List<Operation> findOperationsByIds(final List<String> operationIds) {
        if (operationIds == null || operationIds.isEmpty()) {
            return new ArrayList<>();
        }
        return operationRepository.findByIdIn(operationIds);
    }

    public Operation findOperationById(final String operationId) {
        return operationRepository.findById(operationId).orElseThrow(() -> new NotFoundException(
            ErrorCode.OP001));
    }
}
