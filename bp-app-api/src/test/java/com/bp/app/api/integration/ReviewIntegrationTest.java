package com.bp.app.api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bp.app.api.AuthorizationHelper;
import com.bp.app.api.integration.config.TestContainerConfig;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.entity.Review;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.bp.domain.mysql.repository.OperationAdapterRepository;
import com.bp.domain.mysql.repository.ReservationAdapterRepository;
import com.bp.domain.mysql.repository.ReviewAdapterRepository;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
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
public class ReviewIntegrationTest extends TestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewAdapterRepository reviewAdapterRepository;

    @Autowired
    private MemberAdapterRepository memberAdapterRepository;

    @Autowired
    private OperationAdapterRepository operationAdapterRepository;

    @Autowired
    private ShopAdapterRepository shopAdapterRepository;

    @Autowired
    private ReservationAdapterRepository reservationAdapterRepository;

    @Autowired
    private AuthorizationHelper authHelper;

    @BeforeEach
    void beforeEach() {
        memberAdapterRepository.deleteAllInBatch();
        reviewAdapterRepository.deleteAllInBatch();
        reservationAdapterRepository.deleteAllInBatch();
        operationAdapterRepository.deleteAllInBatch();
        shopAdapterRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Review 상세조회 요청 성공시 FindReviewResult 를 wrapping 한 ResponseMessage 객체 응답을 받는다.")
    void given_reviewFindRequest_when_succeed_then_getResponseMessageWrappingFindReviewResult()
        throws Exception {
        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");

        memberAdapterRepository.saveAndFlush(
            Member.newMember("dev.sssukho@gmail.com", "password", "임석호",
                "010-1234-5678", AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
                System.currentTimeMillis()));

        final Operation insertedMockedOperation = operationAdapterRepository.saveAndFlush(
            Operation.newOperation("시술1", "시술1설명"));

        final Shop insertedMockedShop = shopAdapterRepository.saveAndFlush(
            Shop.newShop("shop이름1", "010-1111-2222", "www.naver.com", "소개글1",
                Arrays.asList("imageUrl1", "imageUrl2"), null, null));

        final Reservation insertedMockedReservation = reservationAdapterRepository.saveAndFlush(
            Reservation.newReservation(System.currentTimeMillis(), System.currentTimeMillis(),
                "dev.sssukho@gmail.com", insertedMockedShop.getId(),
                insertedMockedOperation.getId(), 1L));

        final Review insertedReview = reviewAdapterRepository.saveAndFlush(
            Review.newReview("4.5", "리뷰내용", "dev.sssukho@gmail.com",
                insertedMockedOperation.getId(), insertedMockedShop.getId(),
                insertedMockedReservation.getId()));

        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/" + insertedReview.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + userRoleAccessToken)
        );

        // then
        resultActions.andExpect(status().isOk())
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
    @DisplayName("Review 상세조회 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
    void given_reviewFindRequest_when_failed_then_getErrorResponseMessage() throws Exception {
        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");
        final long reviewId = Long.MIN_VALUE;

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + userRoleAccessToken)
        );

        // then
        resultActions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists()).andDo(print());
    }

    @Test
    @DisplayName("Shop 에 속한 Review 리스트 조회 요청 성공시 List<FindReviewResult> 를 wrapping 한 ResponseMessage 객체 응답을 받는다.")
    void given_reviewListInShopRequest_when_succeed_then_getResponseMessageWrappingFindReviewResultList()
        throws Exception {
        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");

        memberAdapterRepository.saveAndFlush(
            Member.newMember("dev.sssukho@gmail.com", "password", "임석호",
                "010-1234-5678", AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
                System.currentTimeMillis()));

        final Operation insertedMockedOperation = operationAdapterRepository.saveAndFlush(
            Operation.newOperation("시술1", "시술1설명"));

        final Shop insertedMockedShop = shopAdapterRepository.saveAndFlush(
            Shop.newShop("shop이름1", "010-1111-2222", "www.naver.com", "소개글1",
                Arrays.asList("imageUrl1", "imageUrl2"), null, null));

        final Reservation insertedMockedReservation = reservationAdapterRepository.saveAndFlush(
            Reservation.newReservation(System.currentTimeMillis(), System.currentTimeMillis(),
                "dev.sssukho@gmail.com", insertedMockedShop.getId(),
                insertedMockedOperation.getId(), 1L));

        final Review insertedReview = reviewAdapterRepository.saveAndFlush(
            Review.newReview("4.5", "리뷰내용", "dev.sssukho@gmail.com",
                insertedMockedOperation.getId(), insertedMockedShop.getId(),
                insertedMockedReservation.getId()));

        final Long shopId = insertedMockedShop.getId();
        final String sort = "createdDate";
        final String page = "0";
        final String count = "10";
        final String order = "desc";

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + shopId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + userRoleAccessToken)
                .param("id", String.valueOf(shopId))
                .param("sort", sort)
                .param("count", count).param("page", page)
                .param("order", order));

        // then
        resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.returnValue[0].id").exists())
            .andExpect(jsonPath("$.returnValue[0].id").exists())
            .andExpect(jsonPath("$.returnValue[0].rate").exists())
            .andExpect(jsonPath("$.returnValue[0].registeredDate").exists())
            .andExpect(jsonPath("$.returnValue[0].memberName").exists())
            .andExpect(jsonPath("$.returnValue[0].operationName").exists())
            .andExpect(jsonPath("$.returnValue[0].reservationDate").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 에 속한 Review 가 없을 경우 empty list 를 응답으로 받는다.")
    void given_reviewListInShopRequest_when_failed_then_getEmptyListResponseMessage() throws Exception {
        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");

        final Shop insertedMockedShop = shopAdapterRepository.saveAndFlush(
            Shop.newShop("shop이름1", "010-1111-2222", "www.naver.com", "소개글1",
                Arrays.asList("imageUrl1", "imageUrl2"), null, null));

        final String shopId = String.valueOf(insertedMockedShop.getId());

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/reviews/shops/" + shopId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + userRoleAccessToken)
                .param("id", shopId)
                .param("count", "1000"));

        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("returnValue").isArray());
    }

    @Test
    @DisplayName("Review 삭제 요청 성공시 No Content 응답을 받는다.")
    void given_reviewDeleteRequest_when_succeed_then_getNoContent() throws Exception {
        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");
        final Review insertedReview = reviewAdapterRepository.saveAndFlush(
            Review.newReview("4.5", "리뷰내용", "dev.sssukho@gmail.com",
                1L, 1L, 1L));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/v1/reviews/" + insertedReview.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + userRoleAccessToken)
        );

        // then
        resultActions.andExpect(status().isNoContent())
            .andDo(print());

        assertThat(reviewAdapterRepository.count()).isZero();
    }

    @Test
    @DisplayName("Review 삭제 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
    void given_reviewDeleteRequest_when_failed_then_getErrorResponseMessage() throws Exception {
        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");
        final String reviewId = "  ";

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.delete("/v1/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + userRoleAccessToken)
        );

        // then
        resultActions.andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists()).andDo(print());
    }
}
