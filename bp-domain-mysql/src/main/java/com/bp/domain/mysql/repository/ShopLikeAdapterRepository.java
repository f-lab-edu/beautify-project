package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.ShopLike;
import com.bp.domain.mysql.entity.ShopLike.ShopLikeId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ShopLikeAdapterRepository {

    private final ShopLikeRepositoryImpl customRepository;
    private final ShopLikeRepository defaultRepository;

    @Transactional
    public void deleteAllByIdInBatch(final List<ShopLikeId> shopLikeIdsToRemove) {
        defaultRepository.deleteAllByIdInBatch(shopLikeIdsToRemove);
    }

    @Transactional
    public void bulkInsert(final List<ShopLike> shopLikes) {
        customRepository.bulkInsert(shopLikes);
    }

    public ShopLike saveAndFlush(final ShopLike shopLikeToSave) {
        return defaultRepository.saveAndFlush(shopLikeToSave);
    }

    @ReadOnlyTransactional
    public List<ShopLike> findByShopLikeIdIn(final List<ShopLikeId> shopLikeIds) {
        return defaultRepository.findByIdIn(shopLikeIds);
    }

    @ReadOnlyTransactional
    public Long count() {
        return defaultRepository.count();
    }

    @Transactional
    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }

    public Optional<ShopLike> findByShopLikeId(final ShopLikeId shopLikeIdToFind) {
        return defaultRepository.findById(shopLikeIdToFind);
    }

}
