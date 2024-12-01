package com.beautify_project.bp_app_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.review.FindReviewListRequestParameters;
import com.beautify_project.bp_app_api.dto.review.ReviewFindResult;
import com.beautify_project.bp_app_api.entity.Member;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.entity.Reservation;
import com.beautify_project.bp_app_api.entity.Review;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.enumeration.OrderType;
import com.beautify_project.bp_app_api.enumeration.ReviewSortBy;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.ReviewRepository;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
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
    private ReviewRepository reviewRepository;

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
        // given
        final Member mockedMember = Member.builder()
            .email("dev.sssukho@gmail.com")
            .name("임석호")
            .contact("010-1234-5678")
            .registered(System.currentTimeMillis())
            .build();

        final Operation mockedOperation = Operation.builder()
            .name("시술명")
            .registered(System.currentTimeMillis())
            .description("시술 설명")
            .build();

        final Shop mockedShop = Shop.builder()
            .name("미용시술소1")
            .contact("010-5678-1234")
            .url("www.naver.com")
            .introduction("소개글")
            .rate("4.3")
            .likes(132)
            .registered(System.currentTimeMillis())
            .updated(System.currentTimeMillis())
            .build();

        final Reservation mockedReservation = Reservation.builder()
            .date(System.currentTimeMillis())
            .registered(System.currentTimeMillis())
            .memberEmail(mockedMember.getEmail())
            .shopId(mockedShop.getId())
            .operationId(mockedOperation.getId())
            .build();

        final Review mockedReview = Review.builder()
            .rate("4.5")
            .content("리뷰 내용")
            .registered(1730437200000L)
            .memberEmail(mockedMember.getEmail())
            .operationId(mockedOperation.getId())
            .shopId(mockedShop.getId())
            .reservationId(mockedReservation.getId())
            .build();

        final String mockedReviewId = mockedReview.getId();

        when(reviewRepository.findById(any(String.class))).thenReturn(Optional.of(mockedReview));
        when(memberService.findMemberByEmail(any(String.class))).thenReturn(mockedMember);
        when(operationService.findOperationById(any(String.class))).thenReturn(mockedOperation);
        when(shopService.findShopById(any(String.class))).thenReturn(mockedShop);
        when(reservationService.findReservationById(any(String.class))).thenReturn(
            mockedReservation);

        // when
        ResponseMessage responseMessage = reviewService.findReview(mockedReviewId);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ReviewFindResult.class);
        verify(reviewRepository, times(1)).findById(any(String.class));
        verify(memberService, times(1)).findMemberByEmail(any(String.class));
        verify(operationService, times(1)).findOperationById(any(String.class));
        verify(shopService, times(1)).findShopById(any(String.class));
        verify(reservationService, times(1)).findReservationById(any(String.class));
    }

    @Test
    @DisplayName("존재하지 않는 reviewId 로 상세 조회시 NotFoundException 을 throw 한다.")
    void given_reviewFindRequestWithNotExistedReviewId_when_failed_then_throwNotFoundException() {
        // given
        final String notExistedReviewId = UUIDGenerator.generate();

        // when & then
        assertThatThrownBy(() -> reviewService.findReview(notExistedReviewId)).isInstanceOf(
            NotFoundException.class);
    }

    @Test
    @DisplayName("Review 리스트 조회 성공시 List<ReviewListFindResult> 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void given_reviewFindListRequest_when_succeed_then_getResponseMessageWRappingReviewListFindResult() {
        // given
        final Member mockedMember = Member.builder()
            .email("dev.sssukho@gmail.com")
            .name("임석호")
            .contact("010-1234-5678")
            .registered(System.currentTimeMillis())
            .build();

        final Operation mockedOperation = Operation.builder()
            .name("시술명")
            .registered(System.currentTimeMillis())
            .description("시술 설명")
            .build();

        final Shop mockedShop = Shop.builder()
            .name("미용시술소1")
            .contact("010-5678-1234")
            .url("www.naver.com")
            .introduction("소개글")
            .rate("4.3")
            .likes(132)
            .registered(System.currentTimeMillis())
            .updated(System.currentTimeMillis())
            .build();

        final Reservation mockedReservation = Reservation.builder()
            .date(System.currentTimeMillis())
            .registered(System.currentTimeMillis())
            .memberEmail(mockedMember.getEmail())
            .shopId(mockedShop.getId())
            .operationId(mockedOperation.getId())
            .build();

        final Review mockedReview1 = Review.builder()
            .rate("4.5")
            .content("리뷰 내용")
            .registered(1730437200000L)
            .memberEmail(mockedMember.getEmail())
            .operationId(mockedOperation.getId())
            .shopId(mockedShop.getId())
            .reservationId(mockedReservation.getId())
            .build();

        final Review mockedReview2 = Review.builder()
            .rate("4.3")
            .content("리뷰 내용 2")
            .registered(System.currentTimeMillis())
            .memberEmail(mockedMember.getEmail())
            .operationId(mockedOperation.getId())
            .shopId(mockedShop.getId())
            .reservationId(mockedReservation.getId())
            .build();

        when(reviewRepository.findReviewsInShop(any(String.class), any(String.class),
            any(String.class), any(Integer.class), any(Integer.class))).thenReturn(
            Arrays.asList(mockedReview1, mockedReview2));

        when(memberService.findMemberByEmail(any(String.class))).thenReturn(mockedMember);
        when(operationService.findOperationById(any(String.class))).thenReturn(mockedOperation);
        when(reservationService.findReservationById(any(String.class))).thenReturn(
            mockedReservation);

        final FindReviewListRequestParameters mockedRequestParams = new FindReviewListRequestParameters(
            mockedShop.getId(),
            ReviewSortBy.REGISTERED_DATE, 0, 10, OrderType.ASC);

        // when
        ResponseMessage responseMessage = reviewService.findReviewListInShop(mockedRequestParams);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(List.class);
        verify(reviewRepository, times(1))
            .findReviewsInShop(any(String.class), any(String.class), any(String.class),
                any(Integer.class), any(Integer.class));
        verify(memberService, times(2)).findMemberByEmail(any(String.class));
        verify(operationService, times(2)).findOperationById(any(String.class));
        verify(reservationService, times(2)).findReservationById(any(String.class));
    }

    @Test
    @DisplayName("존재하지 않는 shopId 로 Review 리스트 조회시 NotFoundException 을 throw 한다.")
    void given_reviewFindListRequestWithNotExistedShopId_when_failed_then_throwNotFoundException() {
        // given
        FindReviewListRequestParameters requestParameters = new FindReviewListRequestParameters(
            UUIDGenerator.generate(), ReviewSortBy.REGISTERED_DATE, 0, 10, OrderType.DESC);

        when(reviewRepository.findReviewsInShop(
            any(String.class), any(String.class), any(String.class), any(Integer.class),
            any(Integer.class))).thenReturn(new ArrayList<>());

        // when & then
        assertThatThrownBy(
            () -> reviewService.findReviewListInShop(requestParameters)).isInstanceOf(
            NotFoundException.class);
    }

    @Test
    @DisplayName("Review 삭제 성공시 이상없이 메서드가 종료된다.")
    void given_deleteReview_when_succeed_then_doNothing() {
        // given
        final String deleteTargetId = UUIDGenerator.generate();
        doNothing().when(reviewRepository).deleteById(any(String.class));

        // when & then
        assertDoesNotThrow(() -> reviewService.deleteReview(deleteTargetId));
    }
}
