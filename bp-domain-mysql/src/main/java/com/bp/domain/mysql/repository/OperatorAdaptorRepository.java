package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Operator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OperatorAdaptorRepository {

    private final OperatorRepository defaultRepository;

    public Optional<Operator> findById(final Long operatorIdToFind) {
        return defaultRepository.findById(operatorIdToFind);
    }

    public Optional<Operator> findByEmail(final String operatorEmailToFind) {
        return defaultRepository.findByEmail(operatorEmailToFind);
    }

}
