package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findByIdIn(final List<String> ids);

}
