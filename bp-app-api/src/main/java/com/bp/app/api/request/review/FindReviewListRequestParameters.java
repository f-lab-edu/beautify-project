package com.bp.app.api.request.review;

import com.bp.domain.mysql.enums.OrderType;
import com.bp.domain.mysql.enums.ReviewSortBy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FindReviewListRequestParameters(
    Long shopId,
    ReviewSortBy sortBy,
    @Min(0)
    Integer page,
    @Min(0)
    @Max(100)
    Integer count,
    OrderType orderType) {
}
