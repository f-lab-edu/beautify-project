package com.beautify_project.bp_app_api.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.beautify_project.bp_app_api.exception.NotRegisteredReviewException;
import com.beautify_project.bp_app_api.fixtures.ReviewTestFixture;
import com.beautify_project.bp_app_api.repository.ReviewRepository;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @BeforeAll
    static void init() {
        ReviewTestFixture.initMockedValidReviewEntitiesIfNotInitialized();
    }

    @Test
    @DisplayName("존재하지 않는 Review 삭제시 NotRegisteredReviewException 을 던진다.")
    void given_deleteNotExistedReviewId_when_failed_then_throwNotRegisteredReviewException() throws Exception {
        // given
        doThrow(EmptyResultDataAccessException.class).when(reviewRepository)
            .deleteById(any(String.class));
        final String notExistedReviewId = UUIDGenerator.generate();

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(notExistedReviewId)).isInstanceOf(
            NotRegisteredReviewException.class);
    }

    @Test
    @DisplayName("Review 삭제 처리시 이상없이 메서드가 종료된다.")
    void given_deleteReview_when_succeed_then_nothing() throws Exception {
        // given
        final String deleteTargetId = ReviewTestFixture.MOCKED_VALID_REVIEW_ENTITIES[0].getId();
        doNothing().when(reviewRepository).deleteById(any(String.class));

        // when & then
        assertDoesNotThrow(() -> reviewService.deleteReview(deleteTargetId));
    }

}
