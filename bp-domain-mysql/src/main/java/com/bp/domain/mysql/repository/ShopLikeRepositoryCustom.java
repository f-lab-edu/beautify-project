package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.ShopLike;
import java.util.List;

public interface ShopLikeRepositoryCustom {

    void bulkInsert(final List<ShopLike> shopLikes);

}
