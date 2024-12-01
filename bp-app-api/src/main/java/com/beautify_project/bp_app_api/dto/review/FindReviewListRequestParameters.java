package com.beautify_project.bp_app_api.dto.review;

import com.beautify_project.bp_app_api.enumeration.OrderType;
import com.beautify_project.bp_app_api.enumeration.ReviewSortBy;
import com.beautify_project.bp_app_api.exception.ParameterOutOfRangeException;

public record FindReviewListRequestParameters(
    String shopId,
    ReviewSortBy sortBy,
    Integer page,
    Integer count,
    OrderType orderType) {

    public FindReviewListRequestParameters {
        validateCount(count);
    }

    private void validateCount(Integer count) {
        if (count > 100 || count < 0) {
            throw new ParameterOutOfRangeException("count", String.valueOf(count));
        }
    }
}
