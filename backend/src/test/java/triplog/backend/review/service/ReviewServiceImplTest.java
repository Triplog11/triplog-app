package triplog.backend.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.entity.Review;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewDetailQueryResult;
import triplog.backend.review.repository.ReviewRepository;
import triplog.backend.review.repository.ReviewListQueryResult;
import triplog.backend.tourismcontent.entity.TourismContent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static triplog.backend.review.exception.ReviewErrorCode.REVIEW_NOT_FOUND;

/**
 * {@link ReviewServiceImpl}의 리뷰 도메인 처리를 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock
    private ReviewRepository reviewRepository;

    private ReviewServiceImpl reviewService;

    /**
     * 테스트 대상과 Mock 의존성을 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository);
    }

    @Test
    @DisplayName("사용자의 전체 방문 인증 수를 조회한다")
    void countCertifications() {
        // Given
        given(reviewRepository.countByUsersId(USERS_ID)).willReturn(12L);

        // When
        int result = reviewService.countCertifications(USERS_ID);

        // Then
        assertThat(result).isEqualTo(12);
    }

    @Test
    @DisplayName("로그인 사용자가 작성한 방문 인증 상세 정보를 조회한다")
    void getReviewDetail() {
        // Given
        ReviewDetailQueryResult result = mock(ReviewDetailQueryResult.class);
        given(result.getReviewId()).willReturn(7001L);
        given(result.getLandmarkId()).willReturn(301L);
        given(result.getLandmarkName()).willReturn("수원화성");
        given(result.getRegionId()).willReturn(101L);
        given(result.getRegionName()).willReturn("수원시");
        given(result.getImageUrl()).willReturn("https://cdn.triplog.com/images/visit-001.png");
        given(result.getAcquiredXp()).willReturn(80);
        given(result.getAcquiredScore()).willReturn(50);
        given(result.getCreatedAt()).willReturn(LocalDateTime.of(2026, 6, 20, 14, 30));
        given(reviewRepository.findReviewDetailByReviewIdAndUsersId(7001L, USERS_ID))
                .willReturn(Optional.of(result));

        // When
        var response = reviewService.getReviewDetail(USERS_ID, 7001L);

        // Then
        assertThat(response.getReviewId()).isEqualTo(7001L);
        assertThat(response.getLandmarkId()).isEqualTo(301L);
        assertThat(response.getLandmarkName()).isEqualTo("수원화성");
        assertThat(response.getRegionId()).isEqualTo(101L);
        assertThat(response.getRegionName()).isEqualTo("수원시");
        assertThat(response.getImageUrl()).isEqualTo("https://cdn.triplog.com/images/visit-001.png");
        assertThat(response.getAcquiredXp()).isEqualTo(80);
        assertThat(response.getAcquiredScore()).isEqualTo(50);
        assertThat(response.getCreatedAt()).isEqualTo("2026-06-20T14:30:00");
    }

    @Test
    @DisplayName("다른 사용자의 방문 인증은 상세 조회할 수 없다")
    void getReviewDetail_NotFoundOrNotOwned() {
        // Given
        given(reviewRepository.findReviewDetailByReviewIdAndUsersId(7001L, USERS_ID))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.getReviewDetail(USERS_ID, 7001L))
                .isInstanceOf(ReviewException.class)
                .extracting("errorCode")
                .isEqualTo(REVIEW_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 사용자의 방문 인증 목록을 페이지 단위로 조회한다")
    void getReviews() {
        // Given
        PageRequest pageable = PageRequest.of(0, 20);
        ReviewListQueryResult item = mock(ReviewListQueryResult.class);
        given(item.getReviewId()).willReturn(7001L);
        given(item.getTourismContentId()).willReturn(1001L);
        given(item.getContentTitle()).willReturn("수원화성");
        given(item.getReviewTitle()).willReturn("수원화성 방문");
        given(item.getRegionId()).willReturn(101L);
        given(item.getRegionName()).willReturn("수원시");
        given(item.getImageUrl()).willReturn("https://cdn.triplog.com/images/visit-001.png");
        given(item.getAcquiredXp()).willReturn(80);
        given(item.getAcquiredScore()).willReturn(50);
        given(item.getCreatedAt()).willReturn(LocalDateTime.of(2026, 6, 20, 14, 30));
        given(reviewRepository.findReviewListByUsersId(USERS_ID, pageable))
                .willReturn(new PageImpl<>(List.of(item), pageable, 1));

        // When
        var response = reviewService.getReviews(USERS_ID, pageable);

        // Then
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(20);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getItems()).singleElement().satisfies(review -> {
            assertThat(review.getReviewId()).isEqualTo(7001L);
            assertThat(review.getTourismContentId()).isEqualTo(1001L);
            assertThat(review.getContentTitle()).isEqualTo("수원화성");
            assertThat(review.getReviewTitle()).isEqualTo("수원화성 방문");
            assertThat(review.getRegionId()).isEqualTo(101L);
            assertThat(review.getRegionName()).isEqualTo("수원시");
            assertThat(review.getImageUrl()).isEqualTo("https://cdn.triplog.com/images/visit-001.png");
            assertThat(review.getAcquiredXp()).isEqualTo(80);
            assertThat(review.getAcquiredScore()).isEqualTo(50);
            assertThat(review.getCreatedAt()).isEqualTo("2026-06-20T14:30:00");
        });
    }

    @Test
    @DisplayName("방문 인증이 없으면 빈 목록을 반환한다")
    void getReviews_Empty() {
        // Given
        PageRequest pageable = PageRequest.of(0, 20);
        given(reviewRepository.findReviewListByUsersId(USERS_ID, pageable))
                .willReturn(new PageImpl<>(List.of(), pageable, 0));

        // When
        var response = reviewService.getReviews(USERS_ID, pageable);

        // Then
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    @DisplayName("검증된 관광 콘텐츠로 방문 인증 리뷰를 저장한다")
    void createReview() {
        // Given
        CreateRequest request = new CreateRequest(
                1L, "41", "110", "수원화성 방문", "방문 완료", 5.0F
        );
        TourismContent content = mock(TourismContent.class);
        Review savedReview = mock(Review.class);
        given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

        // When
        Review result = reviewService.createReview(USERS_ID, request, content);

        // Then
        assertThat(result).isSameAs(savedReview);
        verify(reviewRepository).save(any(Review.class));
    }
}
