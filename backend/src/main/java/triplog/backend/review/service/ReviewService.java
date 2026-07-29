package triplog.backend.review.service;

import org.springframework.web.multipart.MultipartFile;
import triplog.backend.review.dto.request.ReviewRequest.CreateReviewRequest;
import triplog.backend.review.dto.response.ReviewResponse.CreateReviewResponse;

import java.util.List;

/**
 * 리뷰 생성 기능을 정의하는 도메인 서비스입니다.
 */
public interface ReviewService {

    /**
     * 방문 인증 리뷰를 등록합니다.
     *
     * @param usersId 사용자 식별자
     * @param request 방문 인증 등록 요청
     * @param files   인증 이미지 파일 목록
     * @return 방문 인증 등록 응답
     */
    CreateReviewResponse createReview(String usersId, CreateReviewRequest request, List<MultipartFile> files);
}
