package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Operation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OperationAdapterRepository {

    private final OperationRepository defaultRepository;

    @ReadOnlyTransactional
    public List<Operation> findByIdIn(final List<Long> operationIdsToFind) {
        return defaultRepository.findByIdIn(operationIdsToFind);
    }

    public Optional<Operation> findById(final Long operationIdToFind) {
        return defaultRepository.findById(operationIdToFind);
    }

    public List<Operation> saveAll(final List<Operation> operationsToSave) {
        return defaultRepository.saveAll(operationsToSave);
    }

    public Operation saveAndFlush(final Operation operationToSave) {
        return defaultRepository.saveAndFlush(operationToSave);
    }

    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }
}
