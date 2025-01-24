package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.OperationCategory;
import com.bp.domain.mysql.entity.OperationCategory.OperationCategoryId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationCategoryRepository extends JpaRepository<OperationCategory, OperationCategoryId> {

    List<OperationCategory> findByIdOperationIdIn(final List<String> operationIds);

}
