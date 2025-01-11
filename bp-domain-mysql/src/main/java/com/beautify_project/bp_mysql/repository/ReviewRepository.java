package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

}
