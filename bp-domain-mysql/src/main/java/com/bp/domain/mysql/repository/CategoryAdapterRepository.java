package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Category;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryAdapterRepository {

    private final CategoryRepository defaultRepository;

    @ReadOnlyTransactional
    public List<Category> findByIdIn(final List<Long> ids) {
        return defaultRepository.findByIdIn(ids);
    }
}
