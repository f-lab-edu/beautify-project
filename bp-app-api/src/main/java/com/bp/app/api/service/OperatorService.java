package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.domain.mysql.entity.Operator;
import com.bp.domain.mysql.repository.OperatorAdaptorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperatorService {

    private final OperatorAdaptorRepository operatorAdaptorRepository;

    public Operator findOperatorById(final Long operatorId) {
        return operatorAdaptorRepository.findById(operatorId).orElseThrow(() -> new BpCustomException(
            ErrorCode.OP002));
    }

    public Operator findOperatorByEmail(final String operatorEmailToFind) {
        return operatorAdaptorRepository.findByEmail(operatorEmailToFind)
            .orElseThrow(() -> new BpCustomException(ErrorCode.OP002));
    }
}
