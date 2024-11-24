package com.beautify_project.bp_app_api.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beautify_project.bp_app_api.fixtures.ReviewTestFixture;
import com.beautify_project.bp_app_api.fixtures.ShopTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("integration-test")
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Review 상세조회 요청 성공시 FindReviewResult 를 wrapping 한 ResponseMessage 객체 응답을 받는다.")
    void given_reviewFindRequest_when_succeed_then_getResponseMessageWrappingFindReviewResult()
        throws Exception {
        // given
        final String reviewId = ReviewTestFixture.MOCKED_REVIEW_ID;

        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.id").exists())
            .andExpect(jsonPath("$.returnValue.rate").exists())
            .andExpect(jsonPath("$.returnValue.member").exists())
            .andExpect(jsonPath("$.returnValue.operation").exists()).andDo(print());
    }

    @Test
    @DisplayName("Review 상세조회 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
    void given_reviewFindRequest_when_failed_then_getErrorResponseMessage() throws Exception {
        // given
        final String reviewId = " ";

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions.andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists()).andDo(print());
    }

    @Test
    @DisplayName("Shop 에 속한 Review 리스트 조회 요청 성공시 List<FindReviewResult> 를 wrapping 한 ResponseMessage 객체 응답을 받는다.")
    void given_reviewListInShopRequest_when_succeed_then_getResponseMessageWrappingFindReviewResultList()
        throws Exception {
        // given
        final String shopId = ShopTestFixture.MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID;
        final String sort = "registeredDate";
        final String page = "0";
        final String count = "10";
        final String order = "asc";

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + shopId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", shopId)
                .param("sort", sort).param("count", count).param("page", page)
                .param("order", order));

        // then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.returnValue[0].id").exists())
            .andExpect(jsonPath("$.returnValue[0].rate").exists())
            .andExpect(jsonPath("$.returnValue[0].member").isMap())
            .andExpect(jsonPath("$.returnValue[0].operation").isMap()).andDo(print());
    }

    @Test
    @DisplayName("Shop 에 속한 Review 리스트 조회 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
    void given_reviewListInShopRequest_when_failed_then_getErrorResponseMessage() throws Exception {
        // given
        final String shopId = ShopTestFixture.MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID;
        final String count = "1000";

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + shopId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("id", shopId)
                .param("count", count));

        // then
        resultActions.andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists()).andDo(print());
    }

    @Test
    @DisplayName("Review 삭제 요청 성공시 No Content 응답을 받는다.")
    void given_reviewDeleteRequest_when_succeed_then_getNoContent() throws Exception {
        // given
        final String reviewId = ReviewTestFixture.MOCKED_REVIEW_ID;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/v1/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions.andExpect(status().isNoContent()).andDo(print());
    }

    @Test
    @DisplayName("Review 삭제 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
    void given_reviewDeleteRequest_when_failed_then_getErrorResponseMessage() throws Exception {
        // given
        final String reviewId = "  ";

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/v1/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions.andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists()).andDo(print());
    }
}
