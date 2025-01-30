package com.bp.app.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.request.review.FindReviewListRequestParameters;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.review.ReviewFindResult;
import com.bp.app.api.response.review.ReviewListFindResult;
import com.bp.app.api.service.ReviewService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    @DisplayName("Review 상세조회 요청 성공시 FindReviewResult 를 wrapping 한 ResponseMessage 객체를 응답받는다.")
    void given_findReviewRequest_when_succeed_then_getResponseMessageWrappingFindReviewListInShop()
        throws Exception {
        // given
        final ReviewFindResult mockedReviewFindResult = new ReviewFindResult(1L, "4.5",
            "리뷰 내용", 1730437200000L,
            "dev.sssukho@gmail.com", "임석호", 1L, "시술1",
            1L, "미용시술소1", 1L, 1730437200000L);

        when(reviewService.findReview(any(Long.class))).thenReturn(
            ResponseMessage.createResponseMessage(mockedReviewFindResult));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/" + 1L).contentType(
                MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.id").exists())
            .andExpect(jsonPath("$.returnValue.rate").exists())
            .andExpect(jsonPath("$.returnValue.content").exists())
            .andExpect(jsonPath("$.returnValue.reviewRegisteredDate").exists())
            .andExpect(jsonPath("$.returnValue.memberEmail").exists())
            .andExpect(jsonPath("$.returnValue.memberName").exists())
            .andExpect(jsonPath("$.returnValue.operationId").exists())
            .andExpect(jsonPath("$.returnValue.operationName").exists())
            .andExpect(jsonPath("$.returnValue.shopId").exists())
            .andExpect(jsonPath("$.returnValue.shopName").exists())
            .andExpect(jsonPath("$.returnValue.reservationId").exists())
            .andExpect(jsonPath("$.returnValue.reservationDate").exists())
            .andDo(print());
    }


    @Test
    @DisplayName("Shop 에 속한 Review 리스트 조회 요청 성공시 FindReviewListResult 를 wrapping 한 ResponseMessage 객체를 응답받는다.")
    void given_findReviewList_when_succeed_then_getResponseMessageWrappingFindReviewListInShopResult()
        throws Exception {
        // given
        List<ReviewListFindResult> reviewListFindResults = Arrays.asList(
            new ReviewListFindResult(1L, "4.0", 1730437200000L, "임석호1", "시술1",
                1730437200000L),
            new ReviewListFindResult(2L, "4.5", 1733042452808L, "임석호2", "시술2",
                1733042452808L)
        );

        when(reviewService.findReviewListInShop(any(FindReviewListRequestParameters.class))).thenReturn(
            ResponseMessage.createResponseMessage(reviewListFindResults));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + 1L)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sort", "registeredDate")
                .param("page", "0")
                .param("count", "10")
                .param("order", "asc")
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue[0].id").exists())
            .andExpect(jsonPath("$.returnValue[0].rate").exists())
            .andExpect(jsonPath("$.returnValue[0].registeredDate").exists())
            .andExpect(jsonPath("$.returnValue[0].memberName").exists())
            .andExpect(jsonPath("$.returnValue[0].operationName").exists())
            .andExpect(jsonPath("$.returnValue[0].reservationDate").exists())
            .andExpect(jsonPath("$.returnValue[1].id").exists())
            .andExpect(jsonPath("$.returnValue[1].rate").exists())
            .andExpect(jsonPath("$.returnValue[1].registeredDate").exists())
            .andExpect(jsonPath("$.returnValue[1].memberName").exists())
            .andExpect(jsonPath("$.returnValue[1].operationName").exists())
            .andExpect(jsonPath("$.returnValue[1].reservationDate").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 에 속한 Review 리스트 조회 요청 실패시 errorCode, errorMessage 를 담은 에러 응답을 받는다.")
    void given_findReviewList_InShop_when_failed_then_getErrorMessage() throws Exception {
        // given
        when(reviewService.findReviewListInShop(any(FindReviewListRequestParameters.class))).thenThrow(
            new BpCustomException(ErrorCode.SH001));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + 1L)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sort", "registeredDate")
                .param("page", "0")
                .param("count", "10")
                .param("order", "asc")
        );

        // then
        resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists());
    }
}
