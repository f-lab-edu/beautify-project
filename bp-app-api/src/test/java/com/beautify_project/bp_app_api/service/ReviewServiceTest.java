package com.beautify_project.bp_app_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.dto.member.UserRoleMemberRegistrationRequest;
import com.beautify_project.bp_app_api.dto.review.FindReviewListRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_app_api.dto.ResponseMessage;
import com.beautify_project.bp_app_api.dto.review.ReviewFindResult;
import com.beautify_project.bp_mysql.entity.Member;
import com.beautify_project.bp_mysql.entity.Operation;
import com.beautify_project.bp_mysql.entity.Reservation;
import com.beautify_project.bp_mysql.entity.Review;
import com.beautify_project.bp_mysql.entity.Shop;
import com.beautify_project.bp_mysql.enums.OrderType;
import com.beautify_project.bp_mysql.enums.ReviewSortBy;
import com.beautify_project.bp_mysql.repository.ReviewRepository;
import com.beautify_project.bp_utils.UUIDGenerator;
import java.time.LocalTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
        final UserRoleMemberRegistrationRequest userRoleMemberRegistrationRequest = new UserRoleMemberRegistrationRequest(
            "dev.sssukho@mgail.com", "password", "임석호", "010-1234-5678");
        final Member mockedMember = memberService.createNewSelfAuthMember(userRoleMemberRegistrationRequest);

        final Operation mockedOperation = Operation.of("시술명", "시술 설명");
        final ShopRegistrationRequest requestForMockedShop = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            Arrays.asList(UUIDGenerator.generate(), UUIDGenerator.generate()),
            Arrays.asList(UUIDGenerator.generate(), UUIDGenerator.generate()),
            Arrays.asList("preSigned-url1", "preSigned-url2"),
            new BusinessTime(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                Arrays.asList("monday", "tuesday")),
            new Address(
                "111",
                "서울시",
                "마포구",
                "상암동",
                "481",
                "월드컵북로",
                "true",
                "131",
                "707",
                "오벨리스크",
                "134-070",
                "주상복합",
                "12345678",
                "34",
                "90"
            )
        );
        final Shop mockedShop = ShopService.createShopEntityFromShopRegistrationRequest(
            requestForMockedShop);
        final Reservation mockedReservation = Reservation.of(System.currentTimeMillis(),
            mockedMember.getEmail(), mockedShop.getId(), mockedOperation.getId());
        final Review mockedReview = Review.of("4.5", "리뷰 내용", mockedMember.getEmail(),
            mockedOperation.getId(),
            mockedShop.getId(), mockedReservation.getId());

        final String mockedReviewId = mockedReview.getId();

        when(reviewRepository.findById(any(String.class))).thenReturn(Optional.of(mockedReview));
        when(memberService.findMemberByEmailOrElseThrow(any(String.class))).thenReturn(mockedMember);
        when(operationService.findOperationById(any(String.class))).thenReturn(mockedOperation);
        when(shopService.findShopById(any(String.class))).thenReturn(mockedShop);
        when(reservationService.findReservationById(any(String.class))).thenReturn(
            mockedReservation);

        // when
        ResponseMessage responseMessage = reviewService.findReview(mockedReviewId);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(ReviewFindResult.class);
        verify(reviewRepository, times(1)).findById(any(String.class));
        verify(memberService, times(1)).findMemberByEmailOrElseThrow(any(String.class));
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
            BpCustomException.class);
    }

    @Test
    @DisplayName("Review 리스트 조회 성공시 List<ReviewListFindResult> 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void given_reviewFindListRequest_when_succeed_then_getResponseMessageWRappingReviewListFindResult() {
        // given
        final UserRoleMemberRegistrationRequest userRoleMemberRegistrationRequest = new UserRoleMemberRegistrationRequest(
            "dev.sssukho@mgail.com", "password", "임석호", "010-1234-5678");
        final Member mockedMember = memberService.createNewSelfAuthMember(userRoleMemberRegistrationRequest);
        final Operation mockedOperation = Operation.of("시술명", "시술 설명");
        final ShopRegistrationRequest requestForMockedShop = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            Arrays.asList(UUIDGenerator.generate(), UUIDGenerator.generate()),
            Arrays.asList(UUIDGenerator.generate(), UUIDGenerator.generate()),
            Arrays.asList("preSigned-url1", "preSigned-url2"),
            new BusinessTime(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                Arrays.asList("monday", "tuesday")),
            new Address(
                "111",
                "서울시",
                "마포구",
                "상암동",
                "481",
                "월드컵북로",
                "true",
                "131",
                "707",
                "오벨리스크",
                "134-070",
                "주상복합",
                "12345678",
                "34",
                "90"
            )
        );
        final Shop mockedShop = ShopService.createShopEntityFromShopRegistrationRequest(requestForMockedShop);
        final Reservation mockedReservation = Reservation.of(System.currentTimeMillis(),
            mockedMember.getEmail(), mockedShop.getId(), mockedOperation.getId());
        final Review mockedReview1 = Review.of("4.5", "리뷰 내용", mockedMember.getEmail(),
            mockedOperation.getId(),
            mockedShop.getId(), mockedReservation.getId());
        final Review mockedReview2 = mockedReview1.of("4.3", "리뷰 내용 2", mockedMember.getEmail(),
            mockedOperation.getId(), mockedShop.getId(),
            mockedReservation.getId());

        final Page<Review> mockedPage = new PageImpl<>(Arrays.asList(mockedReview1, mockedReview2));
        when(reviewRepository.findAll(any(Pageable.class))).thenReturn(
            mockedPage);
        when(memberService.findMemberByEmailOrElseThrow(any(String.class))).thenReturn(mockedMember);
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
        verify(reviewRepository, times(1)).findAll(any(Pageable.class));
        verify(memberService, times(2)).findMemberByEmailOrElseThrow(any(String.class));
        verify(operationService, times(2)).findOperationById(any(String.class));
        verify(reservationService, times(2)).findReservationById(any(String.class));
    }

    @Test
    @DisplayName("존재하지 않는 shopId 로 Review 리스트 조회시 NotFoundException 을 throw 한다.")
    void given_reviewFindListRequestWithNotExistedShopId_when_failed_then_throwNotFoundException() {
        // given
        FindReviewListRequestParameters requestParameters = new FindReviewListRequestParameters(
            UUIDGenerator.generate(), ReviewSortBy.REGISTERED_DATE, 0, 10, OrderType.DESC);

        final Page<Review> mockedPage = new PageImpl<>(new ArrayList<>());
        when(reviewRepository.findAll(any(Pageable.class))).thenReturn(
            mockedPage);

        // when & then
        assertThatThrownBy(
            () -> reviewService.findReviewListInShop(requestParameters)).isInstanceOf(
            BpCustomException.class);
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
