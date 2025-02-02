package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Shop;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ShopAdapterRepository {

    private final ShopRepository shopRepository;

    @ReadOnlyTransactional
    public List<Shop> findByIdIn(final Set<Long> shopIdsToFind) {
        return shopRepository.findByIdIn(shopIdsToFind);
    }

    @ReadOnlyTransactional
    public Optional<Shop> findById(final Long shopIdToFind) {
        return shopRepository.findById(shopIdToFind);
    }

    @ReadOnlyTransactional
    public Long count() {
        return shopRepository.count();
    }

    @Transactional
    public void saveAll(final Collection<Shop> shopsToSave) {
        shopRepository.saveAll(shopsToSave);
    }

    public Shop saveAndFlush(final Shop shopToSave) {
        return shopRepository.saveAndFlush(shopToSave);
    }

    @Transactional
    public void deleteAllInBatch() {
        shopRepository.deleteAllInBatch();
    }

    public Shop save(final Shop shopToSave) {
        return shopRepository.save(shopToSave);
    }

    @ReadOnlyTransactional
    public List<Shop> findAll(final String searchColumn, final Integer page, final Integer pageSize,
        final String orderType) {
        final Pageable pageable = PageRequest.of(page, pageSize,
            Sort.by(Sort.Direction.fromString(orderType), searchColumn));
        return shopRepository.findAll(pageable).getContent();
    }

    public void delete(final Shop shop) {
        shopRepository.delete(shop);
    }
}
