package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.entity.Review;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.repository.ReviewRepository;
import com.bp.app.api.request.review.FindReviewListRequestParameters;
import com.bp.app.api.response.review.ReviewFindResult;
import com.bp.app.api.response.review.ReviewListFindResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final OperationService operationService;
    private final ShopService shopService;
    private final ReservationService reservationService;

    public ResponseMessage findReview(final String reviewId) {
        final Review foundReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.RE001));

        final Member reviewedWriter = memberService.findMemberByEmailOrElseThrow(foundReview.getMemberEmail());
        final Operation reviewedOperation = operationService.findOperationById(
            foundReview.getOperationId());
        final Shop reviewedShop = shopService.findShopById(foundReview.getShopId());
        final Reservation reviewedReservation = reservationService.findReservationById(
            foundReview.getReservationId());

        return ResponseMessage.createResponseMessage(new ReviewFindResult(foundReview.getId(),
            foundReview.getRate(), foundReview.getContent(), foundReview.getRegisteredTime(),
            reviewedWriter.getEmail(), reviewedWriter.getName(),
            reviewedOperation.getId(), reviewedOperation.getName(), reviewedShop.getId(),
            reviewedShop.getName(), reviewedReservation.getId(), reviewedReservation.getDate()));
    }

    public ResponseMessage findReviewListInShop(final FindReviewListRequestParameters parameters) {
        // TODO: join 으로 개선 필요
        Pageable pageable = PageRequest.of(parameters.page(), parameters.count(),
            Sort.by(Sort.Direction.fromString(parameters.orderType().name()),
                parameters.sortBy().name()));

        List<Review> foundReviews = reviewRepository.findAll(pageable).getContent();
        if (foundReviews.isEmpty()) {
            throw new BpCustomException(ErrorCode.RE001);
        }

        final List<ReviewListFindResult> result =  foundReviews.stream().map(review ->
                new ReviewListFindResult(review.getId(),
                    review.getRate(), review.getRegisteredTime(),
                    memberService.findMemberByEmailOrElseThrow(review.getMemberEmail()).getName(),
                    operationService.findOperationById(
                        review.getOperationId()).getName(),
                    reservationService.findReservationById(review.getReservationId()).getDate()))
            .toList();

        return ResponseMessage.createResponseMessage(result);
    }

    @Transactional
    public void deleteReview(final String reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
