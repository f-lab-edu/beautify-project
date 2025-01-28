package com.bp.app.api.controller;

import com.bp.app.api.request.review.FindReviewListRequestParameters;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.service.ReviewService;
import com.bp.domain.mysql.enums.OrderType;
import com.bp.domain.mysql.enums.ReviewSortBy;
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
        @PathVariable(value = "id") @NotBlank @NotNull final Long reviewId) {
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
    public ResponseMessage findReviewListInShop(
        @PathVariable(value = "id") @NotBlank final Long shopId,
        @RequestParam(name = "sort", required = false, defaultValue = "reviewRegisteredDate") final String sort,
        @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
        @RequestParam(name = "count", required = false, defaultValue = "10") final int count,
        @RequestParam(name = "order", required = false, defaultValue = "asc") final String order) {
        return reviewService.findReviewListInShop(
            new FindReviewListRequestParameters(shopId, ReviewSortBy.from(sort), page, count,
                OrderType.from(order)));
    }

    /**
     * 리뷰 삭제
     *
     * @param reviewId 삭제 대상 리뷰 아이디
     */
    @DeleteMapping("/v1/reviews/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable(value = "id") @NotBlank final Long reviewId) {
        reviewService.deleteReview(reviewId);
    }
}
