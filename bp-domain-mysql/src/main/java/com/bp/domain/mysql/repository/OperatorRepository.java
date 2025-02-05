package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Operator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatorRepository extends JpaRepository<Operator, Long> {

    Optional<Operator> findByEmail(final String operatorEmail);
}
