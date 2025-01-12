package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.response.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_mysql.entity.Operation;
import com.beautify_project.bp_mysql.repository.OperationRepository;
import com.beautify_project.bp_utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;


    public List<Operation> findOperationsByIds(final List<String> operationIdsToFind) {
        Validator.throwIfNullOrEmpty(operationIdsToFind, new BpCustomException(ErrorCode.BR001));
        return operationRepository.findByIdIn(operationIdsToFind);
    }

    public Operation findOperationById(final String operationId) {
        return operationRepository.findById(operationId).orElseThrow(() -> new BpCustomException(
            ErrorCode.OP001));
    }
}
