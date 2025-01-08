package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.ShopLike;
import java.util.List;

public interface ShopLikeRepositoryCustom {

    void bulkInsert(final List<ShopLike> shopLikes);

}
