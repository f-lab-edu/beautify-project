package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.OperationCategory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OperationCategoryAdapterRepository {

    private final OperationCategoryRepository defaultRepository;

    public List<OperationCategory> findByIdOperationIdIn(final List<Long> operationIds) {
        return defaultRepository.findByIdOperationIdIn(operationIds);
    }

    public List<OperationCategory> saveAll(final List<OperationCategory> operationCategoriesToSave) {
        return defaultRepository.saveAll(operationCategoriesToSave);
    }

    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }
}
