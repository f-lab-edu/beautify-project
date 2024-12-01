package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    @Query("SELECT r FROM Review r "
        + "WHERE r.shopId = :shopId "
        + "ORDER BY :orderBy :orderType "
        + "OFFSET :page "
        + "LIMIT :count")
    List<Review> findReviewsInShop(@Param("shopId") String shopId,
        @Param("orderBy") String orderBy,
        @Param("orderType") String orderType,
        @Param("page") Integer page,
        @Param("count") Integer count);

}
