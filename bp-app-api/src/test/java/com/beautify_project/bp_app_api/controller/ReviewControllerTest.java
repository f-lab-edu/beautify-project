package com.beautify_project.bp_app_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beautify_project.bp_app_api.dto.review.FindReviewListRequestParameters;
import com.beautify_project.bp_app_api.fixtures.ReviewTestFixture;
import com.beautify_project.bp_app_api.service.ReviewService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @BeforeAll
    public static void setUp() {
        ReviewTestFixture.initMockedEmptyResponseMessage();
        ReviewTestFixture.initMockedFindReviewSuccessResponse();
        ReviewTestFixture.initMockedFindReviewListSuccessResponse();
    }

    @Test
    @DisplayName("Review 상세조회 요청 성공시 ResponseMessage 를 응답받는다.")
    void given_findReviewRequest_when_succeed_then_getResponseMessage() throws Exception {
        // given
        final String reviewId = ReviewTestFixture.MOCKED_REVIEW_ID;

        when(reviewService.findReview(any(String.class))).thenCallRealMethod();

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/" + reviewId).contentType(
                MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andDo(print());
    }


    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Review 상세조회 요청시 ReviewId 값이 empty string 인 경우 errorCode, errorMessage 를 담은 에러 응답을 받는다.")
    void given_requestWithNullOrEmptyStringReviewId_then_responseIncludingErrorCodeAndErrorMessageWith4xxHttpStatusCode(
        final String reviewId) throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions.andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 에 속한 Review 리스트 조회 요청 성공시 returnValue 객체 안에 id, rate, member, operation 등의 정보가 list 안에 포함하여 응답한다.")
    void given_normalRequest_when_hasResult_then_responseIncludingIdAndRateAndContentAndMemberAndOperationInList()
        throws Exception {
        // given
        final String requestShopId = ReviewTestFixture.MOCKED_SHOP_ID;
        when(reviewService.findReviewList(
            any(FindReviewListRequestParameters.class))).thenCallRealMethod();

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + requestShopId)
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
            .andExpect(jsonPath("$.returnValue[0].member").exists())
            .andExpect(jsonPath("$.returnValue[0].operation").exists())
            .andExpect(jsonPath("$.returnValue[1].id").exists())
            .andExpect(jsonPath("$.returnValue[1].rate").exists())
            .andExpect(jsonPath("$.returnValue[1].member").exists())
            .andExpect(jsonPath("$.returnValue[1].operation").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 에 속한 Review 리스트 조회 요청 실패시 errorCode, errorMessage 를 담은 에러 응답을 받는다.")
    void given_abnormalRequest_when_failed_then_getErrorCodeAndErrorMessage() throws Exception {
        // given
        final String requestShopId = ReviewTestFixture.MOCKED_SHOP_ID;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + requestShopId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("sort", "registeredDat")
                .param("page", "0")
                .param("count", "10")
                .param("order", "asc")
        );

        // then
        resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

//    @Test
////    @ParameterizedTest
//    @DisplayName("Shop 에 속한 Review 리스트 조회 요청시 올바르지 않은 파라미터 값은 실패한다")
//    void given_c() throws Exception {
//        // given
//
//        // when
//
//        // then
//    }
//
//    @Test
//    @DisplayName("리뷰 삭제 요청 성공시 No Content 상태 코드로 응답이 나간다.")
//    void given_d() throws Exception {
//
//    }
//
//    @Test
//    @DisplayName("리뷰 삭제 요청 실패시 errorCode, errorMessage 를 담은 에러 응답을 받는다.")
//    void given_e() throws Exception {
//
//    }
}
