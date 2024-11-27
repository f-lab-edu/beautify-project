package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.review.FindReviewResult;
import com.beautify_project.bp_app_api.dto.review.FindReviewResult.Member;
import com.beautify_project.bp_app_api.dto.review.FindReviewResult.Operation;
import com.beautify_project.bp_app_api.dto.review.FindReviewListRequestParameters;
import com.beautify_project.bp_app_api.exception.NotRegisteredReviewException;
import com.beautify_project.bp_app_api.repository.ReviewRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ResponseMessage findReview(final String reviewId) {
        return ResponseMessage.createResponseMessage(createFindReviewDummySuccessResponseBody());
    }

    public ResponseMessage findReviewList(final FindReviewListRequestParameters parameters) {
        return ResponseMessage.createResponseMessage(
            createFindReviewListDummySuccessResponseBody());
    }

    @Transactional
    public void deleteReview(final String reviewId) {
        try {
            reviewRepository.deleteById(reviewId);
        } catch (EmptyResultDataAccessException exception) {
            log.error("", exception);
            throw new NotRegisteredReviewException("존재하지 않는 리뷰입니다.");
        }
    }

    private FindReviewResult createFindReviewDummySuccessResponseBody() {
        return FindReviewResult.builder()
            .id("bd1cc4f9")
            .rate("4.5")
            .registeredDate(1732452699631L)
            .member(new Member("sssukho", "임석호"))
            .operation(new Operation("d939f8ed", "두피문신", 1730437200000L))
            .build();
    }

    private List<FindReviewResult> createFindReviewListDummySuccessResponseBody() {
        FindReviewResult data1 = createFindReviewDummySuccessResponseBody();
        FindReviewResult data2 = FindReviewResult.builder()
            .id("9f142f61")
            .rate("3.0")
            .registeredDate(1732464417840L)
            .member(new Member("sssukho2", "임석호2"))
            .operation(new Operation("7fced931", "타투", 1732464506633L))
            .build();
        return Arrays.asList(data1, data2);
    }
}
