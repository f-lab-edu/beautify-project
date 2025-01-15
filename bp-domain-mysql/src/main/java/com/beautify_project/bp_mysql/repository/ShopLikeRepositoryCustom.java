package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.ShopLike;
import java.util.List;

public interface ShopLikeRepositoryCustom {

    void bulkInsert(final List<ShopLike> shopLikes);

}
