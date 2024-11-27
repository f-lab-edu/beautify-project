package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.Operation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation, String> {

    List<Operation> findByIdIn(final List<String> operationIds);

}
