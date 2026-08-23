package triplog.backend.review.repository;

import java.time.LocalDateTime;

/**
 * 방문 인증 목록 조회에 필요한 리뷰, 관광 콘텐츠, 지역, 이미지, 보상 정보를 조합한 projection입니다.
 */
public interface ReviewListQueryResult {

    Long getReviewId();

    Long getTourismContentId();

    String getContentTitle();

    String getReviewTitle();

    Long getRegionId();

    String getRegionName();

    String getImageUrl();

    Integer getAcquiredXp();

    Integer getAcquiredScore();

    LocalDateTime getCreatedAt();
}
