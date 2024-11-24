package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.review.FindReviewResult;
import com.beautify_project.bp_app_api.dto.review.FindReviewResult.Member;
import com.beautify_project.bp_app_api.dto.review.FindReviewResult.Operation;
import com.beautify_project.bp_app_api.dto.review.FindReviewListRequestParameters;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReviewService {

    public ResponseMessage findReview(final String reviewId) {
        return ResponseMessage.createResponseMessage(createFindReviewDummySuccessResponseBody());
    }

    public ResponseMessage findReviewList(final FindReviewListRequestParameters parameters) {
        return ResponseMessage.createResponseMessage(
            createFindReviewListDummySuccessResponseBody());
    }

    public void deleteReview(final String reviewId) {

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
