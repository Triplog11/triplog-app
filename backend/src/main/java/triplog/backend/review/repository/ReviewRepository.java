package triplog.backend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.review.entity.Review;

/**
 * Review 영속성 처리를 담당하는 Repository입니다.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

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
