package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.entity.Category;
import com.beautify_project.bp_app_api.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findCategoriesByIds(final List<String> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        return categoryRepository.findByIdIn(categoryIds);
    }
}
