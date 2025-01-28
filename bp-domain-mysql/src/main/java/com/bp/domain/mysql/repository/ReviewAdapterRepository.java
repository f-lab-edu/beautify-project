package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Review;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewAdapterRepository {

    private final ReviewRepository defaultRepository;

    @ReadOnlyTransactional
    public Optional<Review> findById(final Long reviewIdToFind) {
        return defaultRepository.findById(reviewIdToFind);
    }

    @ReadOnlyTransactional
    public List<Review> findAll(final String searchColumn, final Integer page,
        final Integer pageSize, final String orderType) {
        Pageable pageable = PageRequest.of(page, pageSize,
            Sort.by(Sort.Direction.fromString(orderType), searchColumn));
        return defaultRepository.findAll(pageable).getContent();
    }

    public void deleteById(final Long reviewIdToDelete) {
        defaultRepository.deleteById(reviewIdToDelete);
    }
}
