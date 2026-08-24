package triplog.backend.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.review.entity.Review;

import java.util.Optional;

/**
 * Review 영속성 처리를 담당하는 Repository입니다.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 사용자가 등록한 방문 인증 리뷰 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 방문 인증 수
     */
    long countByUsersId(String usersId);

    /**
     * 사용자가 방문 인증한 서로 다른 관광 콘텐츠 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 고유 관광 콘텐츠 수
     */
    @Query("SELECT COUNT(DISTINCT r.tourismContent.tourismContentId) "
            + "FROM Review r WHERE r.usersId = :usersId")
    long countDistinctVisitedContents(@Param("usersId") String usersId);

    /** 사용자가 방문 인증한 서로 다른 시·도 수를 조회합니다. */
    @Query("SELECT COUNT(DISTINCT r.tourismContent.region.legalRegionCode) "
            + "FROM Review r WHERE r.usersId = :usersId")
    long countDistinctVisitedProvinces(@Param("usersId") String usersId);

    /**
     * 로그인 사용자의 방문 인증 목록을 최신 생성순으로 조회합니다.
     * 첨부 이미지와 리뷰 로그가 여러 건이면 최초 등록 데이터를 대표값으로 사용합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 페이지 정보
     * @return 방문 인증 목록 조회 결과
     */
    @Query(value = """
            SELECT review.review_id AS reviewId,
                   tourism_content.tourism_content_id AS tourismContentId,
                   tourism_content.title AS contentTitle,
                   review.review_title AS reviewTitle,
                   region.region_id AS regionId,
                   region.region_name AS regionName,
                   (
                       SELECT image.image_url
                       FROM image image
                       WHERE image.review_id = review.review_id
                       ORDER BY image.image_id ASC
                       LIMIT 1
                   ) AS imageUrl,
                   COALESCE((
                       SELECT review_log.review_gain_xp
                       FROM review_log review_log
                       WHERE review_log.review_id = review.review_id
                       ORDER BY review_log.review_log_id ASC
                       LIMIT 1
                   ), 0) AS acquiredXp,
                   review.review_point AS acquiredScore,
                   (
                       SELECT review_log.review_created_at
                       FROM review_log review_log
                       WHERE review_log.review_id = review.review_id
                       ORDER BY review_log.review_log_id ASC
                       LIMIT 1
                   ) AS createdAt
            FROM review review
            JOIN tourism_content tourism_content
              ON tourism_content.tourism_content_id = review.tourism_content_id
            JOIN region region
              ON region.region_id = tourism_content.region_id
            WHERE review.users_id = :usersId
            ORDER BY createdAt DESC, review.review_id DESC
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM review review
            WHERE review.users_id = :usersId
            """,
            nativeQuery = true)
    Page<ReviewListQueryResult> findReviewListByUsersId(
            @Param("usersId") String usersId,
            Pageable pageable
    );

    /**
     * 로그인 사용자가 작성한 랜드마크 방문 인증 상세 정보를 조회합니다.
     * 첨부 이미지와 리뷰 로그가 여러 건이면 최초 등록 데이터를 대표값으로 사용합니다.
     *
     * @param reviewId 방문 인증 리뷰 식별자
     * @param usersId 사용자 식별자
     * @return 방문 인증 상세 조회 결과
     */
    @Query(value = """
            SELECT review.review_id AS reviewId,
                   landmark.landmark_id AS landmarkId,
                   landmark.landmark_name AS landmarkName,
                   region.region_id AS regionId,
                   region.region_name AS regionName,
                   (
                       SELECT image.image_url
                       FROM image image
                       WHERE image.review_id = review.review_id
                       ORDER BY image.image_id ASC
                       LIMIT 1
                   ) AS imageUrl,
                   COALESCE((
                       SELECT review_log.review_gain_xp
                       FROM review_log review_log
                       WHERE review_log.review_id = review.review_id
                       ORDER BY review_log.review_log_id ASC
                       LIMIT 1
                   ), 0) AS acquiredXp,
                   review.review_point AS acquiredScore,
                   (
                       SELECT review_log.review_created_at
                       FROM review_log review_log
                       WHERE review_log.review_id = review.review_id
                       ORDER BY review_log.review_log_id ASC
                       LIMIT 1
                   ) AS createdAt
            FROM review review
            JOIN tourism_content tourism_content
              ON tourism_content.tourism_content_id = review.tourism_content_id
            JOIN landmark landmark
              ON landmark.tourism_content_id = tourism_content.tourism_content_id
            JOIN region region
              ON region.region_id = tourism_content.region_id
            WHERE review.review_id = :reviewId
              AND review.users_id = :usersId
            """, nativeQuery = true)
    Optional<ReviewDetailQueryResult> findReviewDetailByReviewIdAndUsersId(
            @Param("reviewId") Long reviewId,
            @Param("usersId") String usersId
    );

    /**
     * 여행 기록에 서버가 계산한 획득 Score를 저장합니다.
     *
     * @param reviewId   여행 기록 식별자
     * @param rewardScore 서버가 계산한 획득 Score
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Review r set r.reviewPoint = :rewardScore where r.reviewId = :reviewId")
    int updateRewardScore(
            @Param("reviewId") Long reviewId,
            @Param("rewardScore") int rewardScore
    );
}
