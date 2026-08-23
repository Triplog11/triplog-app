package triplog.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.dto.response.ReviewResponse.DetailResponse;
import triplog.backend.review.dto.response.ReviewResponse.ListResponse;
import triplog.backend.review.entity.Review;
import triplog.backend.review.exception.ReviewException;
import triplog.backend.review.repository.ReviewRepository;
import triplog.backend.review.repository.ReviewDetailQueryResult;
import triplog.backend.review.repository.ReviewListQueryResult;
import triplog.backend.tourismcontent.entity.TourismContent;

import static triplog.backend.review.exception.ReviewErrorCode.REVIEW_NOT_FOUND;

/**
 * 리뷰 도메인의 저장과 조회를 담당하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    /**
     * 로그인 사용자가 작성한 방문 인증 상세 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param reviewId 방문 인증 리뷰 식별자
     * @return 방문 인증 상세 응답
     */
    @Override
    @Transactional(readOnly = true)
    public DetailResponse getReviewDetail(String usersId, Long reviewId) {
        ReviewDetailQueryResult review = reviewRepository
                .findReviewDetailByReviewIdAndUsersId(reviewId, usersId)
                .orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));
        return DetailResponse.toDto(review);
    }

    /**
     * 로그인 사용자의 방문 인증 목록을 최신 생성순으로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 페이지 정보
     * @return 방문 인증 목록 응답
     */
    @Override
    @Transactional(readOnly = true)
    public ListResponse getReviews(String usersId, Pageable pageable) {
        Page<ReviewListQueryResult> reviews = reviewRepository.findReviewListByUsersId(usersId, pageable);
        return ListResponse.toDto(reviews);
    }

    /**
     * 사용자의 전체 방문 인증 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 방문 인증 수
     */
    @Override
    @Transactional(readOnly = true)
    public int countCertifications(String usersId) {
        return Math.toIntExact(reviewRepository.countByUsersId(usersId));
    }

    /**
     * 검증된 관광 콘텐츠로 방문 인증 리뷰를 저장합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 방문 인증 및 여행 기록 요청
     * @param tourismContent 검증된 관광 콘텐츠
     * @return 저장된 방문 인증 리뷰
     */
    @Override
    @Transactional
    public Review createReview(
            String usersId, CreateRequest request, TourismContent tourismContent
    ) {
        return reviewRepository.save(request.toEntity(usersId, tourismContent));
    }

    /**
     * 방문 인증에 지급된 총 점수를 저장합니다.
     *
     * @param reviewId 방문 인증 식별자
     * @param rewardScore 지급된 총 점수
     */
    @Override
    @Transactional
    public void updateRewardScore(Long reviewId, int rewardScore) {
        reviewRepository.updateRewardScore(reviewId, rewardScore);
    }
}
