package triplog.backend.review.service;

import org.springframework.data.domain.Pageable;
import triplog.backend.review.dto.request.ReviewRequest.CreateRequest;
import triplog.backend.review.dto.response.ReviewResponse.DetailResponse;
import triplog.backend.review.dto.response.ReviewResponse.ListResponse;
import triplog.backend.review.entity.Review;
import triplog.backend.tourismcontent.entity.TourismContent;


/**
 * 리뷰 생성 기능을 정의하는 도메인 서비스입니다.
 */
public interface ReviewService {

    /**
     * 로그인 사용자가 작성한 방문 인증 상세 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param reviewId 방문 인증 리뷰 식별자
     * @return 방문 인증 상세 응답
     */
    DetailResponse getReviewDetail(String usersId, Long reviewId);

    /**
     * 로그인 사용자의 방문 인증 목록을 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 페이지 정보
     * @return 방문 인증 목록 응답
     */
    ListResponse getReviews(String usersId, Pageable pageable);

    /**
     * 사용자의 전체 방문 인증 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 방문 인증 수
     */
    int countCertifications(String usersId);

    /**
     * 방문 인증 리뷰를 등록합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 방문 인증 등록 요청
     * @param tourismContent 검증된 관광 콘텐츠
     * @return 저장된 방문 인증 리뷰
     */
    Review createReview(String usersId, CreateRequest request, TourismContent tourismContent);

    /**
     * 방문 인증에 지급된 총 점수를 저장합니다.
     *
     * @param reviewId 방문 인증 식별자
     * @param rewardScore 지급된 총 점수
     */
    void updateRewardScore(Long reviewId, int rewardScore);
}
