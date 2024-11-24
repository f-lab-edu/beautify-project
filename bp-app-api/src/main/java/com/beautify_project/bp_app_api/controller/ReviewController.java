package com.beautify_project.bp_app_api.controller;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.review.FindReviewListRequestParameters;
import com.beautify_project.bp_app_api.enumeration.OrderType;
import com.beautify_project.bp_app_api.enumeration.ReviewSortBy;
import com.beautify_project.bp_app_api.service.ReviewService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 상세 조회
     *
     * @param reviewId 조회 대상 리뷰 아이디
     * @return ResponseMessage 형태의 ResponseBody
     */
    @GetMapping("/v1/reviews/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage findReview(
        @PathVariable(value = "id") @NotBlank @NotNull final String reviewId)
        throws RuntimeException {
        return reviewService.findReview(reviewId);
    }

    /**
     * 샵에 속한 리뷰 목록 조회
     *
     * @param shopId 조회 대상 샵 아이디
     * @param page   페이징 시 페이지 수
     * @param count  페이징 시 한 페이지에 출력할 결과 개수
     * @param order  리뷰 등록일자 기준 오름 차순 / 내림 차순
     * @return ResponseMessage 형태의 ResponseBody
     */
    @GetMapping("/v1/reviews/shops/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseMessage findReviewList(@PathVariable(value = "id") @NotBlank final String shopId,
        @RequestParam(name = "sort", required = false, defaultValue = "registeredDate") final String sort,
        @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
        @RequestParam(name = "count", required = false, defaultValue = "10") final int count,
        @RequestParam(name = "order", required = false, defaultValue = "asc") final String order)
        throws RuntimeException {
        return reviewService.findReviewList(
            FindReviewListRequestParameters.builder().shopId(shopId).sortBy(ReviewSortBy.from(sort))
                .page(page).count(count).orderType(OrderType.from(order)).build());
    }

    /**
     * 리뷰 삭제
     *
     * @param reviewId 삭제 대상 리뷰 아이디
     */
    @DeleteMapping("/v1/reviews/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable(value = "id") @NotBlank final String reviewId)
        throws RuntimeException {
        reviewService.deleteReview(reviewId);
    }
}
