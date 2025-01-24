package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.repository.OperationRepository;
import com.bp.utils.Validator;
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
