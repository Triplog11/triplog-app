package triplog.backend.review.repository;

import java.time.LocalDateTime;

/**
 * 방문 인증 상세 조회에 필요한 리뷰, 랜드마크, 지역, 이미지, 보상 정보를 조합한 projection입니다.
 */
public interface ReviewDetailQueryResult {

    Long getReviewId();

    Long getLandmarkId();

    String getLandmarkName();

    Long getRegionId();

    String getRegionName();

    String getImageUrl();

    Integer getAcquiredXp();

    Integer getAcquiredScore();

    LocalDateTime getCreatedAt();
}
