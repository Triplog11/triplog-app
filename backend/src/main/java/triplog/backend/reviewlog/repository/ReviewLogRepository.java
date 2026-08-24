package triplog.backend.reviewlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.reviewlog.entity.ReviewLog;

import java.time.LocalDateTime;

/**
 * ReviewLog 영속성 처리를 담당하는 Repository입니다.
 */
public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {

    /**
     * 지정 기간에 작성된 여행 기록 수를 이미지 조건에 따라 집계합니다.
     *
     * @param usersId      사용자 식별자
     * @param start        집계 시작 시각
     * @param end          집계 종료 시각
     * @param imageRequired 이미지 필수 여부
     * @return 조건에 맞는 여행 기록 수
     */
    @Query(value = """
            SELECT COUNT(DISTINCT review.review_id)
            FROM review_log review_log
            JOIN review review ON review.review_id = review_log.review_id
            WHERE review.users_id = :usersId
              AND review_log.review_created_at BETWEEN :start AND :end
              AND (:imageRequired = FALSE OR EXISTS (
                  SELECT 1 FROM image image
                  WHERE image.review_id = review.review_id
              ))
            """, nativeQuery = true)
    long countTravelRecords(
            @Param("usersId") String usersId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("imageRequired") boolean imageRequired
    );

    /**
     * 사용자의 유효한 전체 여행 기록 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 저장된 여행 기록 수
     */
    @Query(value = """
            SELECT COUNT(DISTINCT review.review_id)
            FROM review_log review_log
            JOIN review review ON review.review_id = review_log.review_id
            WHERE review.users_id = :usersId
            """, nativeQuery = true)
    long countTravelRecords(@Param("usersId") String usersId);

    /** 사용자의 사진 포함 유효 여행 기록 수를 조회합니다. */
    @Query(value = """
            SELECT COUNT(DISTINCT review.review_id)
            FROM review_log review_log
            JOIN review review ON review.review_id = review_log.review_id
            WHERE review.users_id = :usersId
              AND EXISTS (SELECT 1 FROM image image WHERE image.review_id = review.review_id)
            """, nativeQuery = true)
    long countPhotoTravelRecords(@Param("usersId") String usersId);
}
