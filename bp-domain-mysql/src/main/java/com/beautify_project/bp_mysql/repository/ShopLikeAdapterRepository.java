package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.ShopLike;
import com.beautify_project.bp_mysql.entity.adapter.ShopLikeAdapter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopLikeAdapterRepository {

    private final ShopLikeRepositoryImpl shopLikeRepositoryImpl;

    @Transactional
    public void bulkInsert(final List<ShopLikeAdapter> shopLikeAdapters) {
        final List<ShopLike> shopLikeEntities = shopLikeAdapters.stream()
            .map(ShopLikeAdapter::toEntity).toList();
        shopLikeRepositoryImpl.bulkInsert(shopLikeEntities);
    }

}
