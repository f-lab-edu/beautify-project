package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.ShopLike;
import com.beautify_project.bp_mysql.entity.ShopLike.ShopLikeId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    public List<ShopLike> findByShopLikeIdIn(final List<ShopLikeId> shopLikeIds) {
        return defaultRepository.findByIdIn(shopLikeIds);
    }

}
