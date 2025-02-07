package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.request.review.FindReviewListRequestParameters;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.review.ReviewFindResult;
import com.bp.app.api.response.review.ReviewListFindResult;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.entity.Review;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.repository.ReviewAdapterRepository;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewAdapterRepository reviewAdapterRepository;
    private final MemberService memberService;
    private final OperationService operationService;
    private final ShopService shopService;
    private final ReservationService reservationService;

    public ResponseMessage findReview(final Long reviewId) {
        final Review foundReview = reviewAdapterRepository.findById(reviewId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.RE001));

        final Member reviewedWriter = memberService.findMemberByEmailOrElseThrow(foundReview.getMemberEmail());
        final Operation reviewedOperation = operationService.findOperationById(
            foundReview.getOperationId());
        final Shop reviewedShop = shopService.findShopById(foundReview.getShopId());
        final Reservation reviewedReservation = reservationService.findReservationById(
            foundReview.getReservationId());

        return ResponseMessage.createResponseMessage(new ReviewFindResult(foundReview.getId(),
            foundReview.getRate(), foundReview.getContent(), foundReview.getCreatedDate().atZone(
            ZoneId.systemDefault()).toInstant().toEpochMilli(),
            reviewedWriter.getEmail(), reviewedWriter.getName(),
            reviewedOperation.getId(), reviewedOperation.getName(), reviewedShop.getId(),
            reviewedShop.getName(), reviewedReservation.getId(), reviewedReservation.getStartDate()));
    }

    public ResponseMessage findReviewListInShop(final FindReviewListRequestParameters parameters) {
        final List<Review> foundReviews = reviewAdapterRepository.findAll(
            parameters.sortBy().getValue(),
            parameters.page(), parameters.count(), parameters.orderType().name());

        if (foundReviews.isEmpty()) {
            return ResponseMessage.createEmptyListResponseMessage();
        }

        final List<ReviewListFindResult> result = foundReviews.stream().map(review ->
                new ReviewListFindResult(review.getId(),
                    review.getRate(), review.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    memberService.findMemberByEmailOrElseThrow(review.getMemberEmail()).getName(),
                    operationService.findOperationById(
                        review.getOperationId()).getName(),
                    reservationService.findReservationById(review.getReservationId()).getStartDate()))
            .toList();

        return ResponseMessage.createResponseMessage(result);
    }

    public void deleteReview(final Long reviewId) {
        reviewAdapterRepository.deleteById(reviewId);
    }
}
