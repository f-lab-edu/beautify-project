package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findByIdIn(final List<Long> ids);

}
