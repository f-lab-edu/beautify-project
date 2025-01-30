package com.bp.app.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.request.review.FindReviewListRequestParameters;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.review.ReviewFindResult;
import com.bp.app.api.service.MemberService;
import com.bp.app.api.service.OperationService;
import com.bp.app.api.service.ReservationService;
import com.bp.app.api.service.ReviewService;
import com.bp.app.api.service.ShopService;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.entity.Review;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.entity.listener.CustomEntityListener;
import com.bp.domain.mysql.enums.OrderType;
import com.bp.domain.mysql.enums.ReviewSortBy;
import com.bp.domain.mysql.repository.ReviewAdapterRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewAdapterRepository reviewAdapterRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private OperationService operationService;

    @Mock
    private ShopService shopService;

    @Mock
    private ReservationService reservationService;

    @Test
    @DisplayName("Review 상세 조회 성공시 ReviewFindResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void given_reviewFindRequest_when_succeed_then_returnResponseMessageWrappingReviewFindResult() {
        final Review mockedReview = Review.newReview("4.5", "리뷰 내용",
            "dev.sssukho@gmail.com", 1L, 1L, 1L);
        CustomEntityListener entityListener = new CustomEntityListener();
        entityListener.prePersist(mockedReview);

        final Member mockedMember = Member.newMember("dev.sssukho@gmail.com", "password", "임석호",
            "010-1234-5678",
            AuthType.BP, UserRole.USER, MemberStatus.ACTIVE, System.currentTimeMillis());
        final Operation mockedOperation = Operation.newOperation("시술1", "시술1설명");
        final Shop mockedShop = Shop.newShop("샵이름", "010-1234-5678", "www.naver.com",
            "소개글", Arrays.asList("image_id1", "image_id2"), null, null);
        final Reservation mockedReservation = Reservation.newReservation(System.currentTimeMillis(),
            "dev.sssukho@gmail.com", 1L, 1L);

        when(reviewAdapterRepository.findById(any())).thenReturn(
            Optional.of(mockedReview));
        when(memberService.findMemberByEmailOrElseThrow(any())).thenReturn(
            mockedMember);
        when(operationService.findOperationById(any())).thenReturn(mockedOperation);
        when(shopService.findShopById(any())).thenReturn(mockedShop);
        when(reservationService.findReservationById(any())).thenReturn(mockedReservation);

        // when
        ResponseMessage responseMessage = reviewService.findReview(any());

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ReviewFindResult.class);
        verify(reviewAdapterRepository, times(1)).findById(any());
        verify(memberService, times(1)).findMemberByEmailOrElseThrow(any(String.class));
        verify(operationService, times(1)).findOperationById(any());
        verify(shopService, times(1)).findShopById(any());
        verify(reservationService, times(1)).findReservationById(any());
    }

    @Test
    @DisplayName("존재하지 않는 reviewId 로 상세 조회시 NotFoundException 을 throw 한다.")
    void given_reviewFindRequestWithNotExistedReviewId_when_failed_then_throwNotFoundException() {
        // given
        final Long notExistedReviewId = Long.MAX_VALUE;

        // when & then
        assertThatThrownBy(() -> reviewService.findReview(notExistedReviewId)).isInstanceOf(
            BpCustomException.class);
    }

    @Test
    @DisplayName("Review 리스트 조회 성공시 List<ReviewListFindResult> 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void given_reviewFindListRequest_when_succeed_then_getResponseMessageWRappingReviewListFindResult() {
        // given
        final Review mockedReview1 = Review.newReview("4.5", "리뷰1",
            "s1@gmail.com", 1L, 1L, 1L);
        final Review mockedReview2 = Review.newReview("4.3", "리뷰2",
            "s2@gmail.com", 2L, 1L, 2L);

        CustomEntityListener entityListener = new CustomEntityListener();
        entityListener.prePersist(mockedReview1);
        entityListener.prePersist(mockedReview2);

        final List<Review> mockedReviews = Arrays.asList(mockedReview1, mockedReview2);
        final Member mockedMember = Member.newMember("dev.sssukho@gmail.com", "password",
            "name", "010-1234-5678", AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
            System.currentTimeMillis());
        final Operation mockedOperation = Operation.newOperation("시술1", "시술1설명");
        final Reservation mockedReservation = Reservation.newReservation(System.currentTimeMillis(),
            "dev.sssukho@gmail.com", 1L, 1L);

        when(reviewAdapterRepository.findAll(
            any(), any(), any(), any())).thenReturn(mockedReviews);
        when(memberService.findMemberByEmailOrElseThrow(any())).thenReturn(mockedMember);
        when(operationService.findOperationById(any())).thenReturn(mockedOperation);
        when(reservationService.findReservationById(any())).thenReturn(mockedReservation);

        // when
        final FindReviewListRequestParameters params = new FindReviewListRequestParameters(1L,
            ReviewSortBy.RATE, 0, 30, OrderType.DESC);
        final ResponseMessage responseMessage = reviewService.findReviewListInShop(params);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(List.class);
        verify(reviewAdapterRepository, times(1)).findAll(any(), any(), any(), any());
        verify(memberService, times(2)).findMemberByEmailOrElseThrow(any());
        verify(operationService, times(2)).findOperationById(any());
        verify(reservationService, times(2)).findReservationById(any());
    }

    @Test
    @DisplayName("존재하지 않는 shopId 로 Review 리스트 조회시 NotFoundException 을 throw 한다.")
    void given_reviewFindListRequestWithNotExistedShopId_when_failed_then_throwNotFoundException() {
        // given
        final FindReviewListRequestParameters mockedRequestParams = new FindReviewListRequestParameters(
            1L, ReviewSortBy.CREATED_DATE, 0, 10, OrderType.ASC);

        when(reviewAdapterRepository.findAll(any(), any(), any(), any()))
            .thenReturn(new ArrayList<>());

        // when & then
        assertThatThrownBy(
            () -> reviewService.findReviewListInShop(mockedRequestParams))
            .isInstanceOf(BpCustomException.class);
    }

    @Test
    @DisplayName("Review 삭제 성공시 이상없이 메서드가 종료된다.")
    void given_deleteReview_when_succeed_then_doNothing() {
        // given
        final Long deleteTargetId = Long.MAX_VALUE;
        doNothing().when(reviewAdapterRepository).deleteById(any());

        // when & then
        assertDoesNotThrow(() -> reviewService.deleteReview(deleteTargetId));
    }
}
