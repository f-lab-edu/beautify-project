package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Category;
import java.sql.PreparedStatement;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryAdapterRepository {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss");

    private final CategoryRepository defaultRepository;
    private final JdbcTemplate jdbcTemplate;

    @ReadOnlyTransactional
    public List<Category> findByIdIn(final List<Long> ids) {
        return defaultRepository.findByIdIn(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bulkInsert(final List<Category> categoriesToInsert) {
        final String insertSql = "INSERT INTO category (category_name, category_description, created_date, last_modified_date) VALUES (?, ?, ?, ?)";

        int sizeToInsert = categoriesToInsert.size();
        jdbcTemplate.batchUpdate(insertSql, categoriesToInsert, sizeToInsert,
            (PreparedStatement ps, Category categoryEntity) -> {
                ps.setString(1, categoryEntity.getName());
                ps.setString(2, categoryEntity.getDescription());
                ps.setString(3, categoryEntity.getCreatedDate().format(DATE_TIME_FORMATTER));
                ps.setString(4, categoryEntity.getLastModifiedDate().format(DATE_TIME_FORMATTER));
            }
        );
    }

    @Transactional
    public List<Category> saveAll(final List<Category> categoriesToSave) {
        return defaultRepository.saveAll(categoriesToSave);
    }

    @Transactional
    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }
}
