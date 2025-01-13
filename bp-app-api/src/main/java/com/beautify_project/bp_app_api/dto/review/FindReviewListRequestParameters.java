package com.beautify_project.bp_app_api.dto.review;


import com.beautify_project.bp_mysql.enums.OrderType;
import com.beautify_project.bp_mysql.enums.ReviewSortBy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FindReviewListRequestParameters(
    String shopId,
    ReviewSortBy sortBy,
    @Min(0)
    Integer page,
    @Min(0)
    @Max(100)
    Integer count,
    OrderType orderType) {
}
