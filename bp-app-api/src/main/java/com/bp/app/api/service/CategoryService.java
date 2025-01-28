package com.bp.app.api.service;

import com.bp.domain.mysql.entity.Category;
import com.bp.domain.mysql.repository.CategoryAdapterRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryAdapterRepository categoryAdapterRepository;

    public List<Category> findCategoriesByIds(final List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        return categoryAdapterRepository.findByIdIn(categoryIds);
    }
}
