package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.ShopLike;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Shop saveAndFlush(final Shop shopToSave) {
        return shopRepository.saveAndFlush(shopToSave);
    }

    @Transactional
    public void deleteAllInBatch() {
        shopRepository.deleteAllInBatch();
    }
}
