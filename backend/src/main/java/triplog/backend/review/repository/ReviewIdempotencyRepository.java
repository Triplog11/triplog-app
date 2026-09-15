package triplog.backend.review.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import triplog.backend.review.entity.ReviewIdempotency;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 방문 인증 멱등성 요청의 원자적 선점과 잠금 조회를 담당합니다.
 * 서비스 계층의 사전 존재 여부 조회만으로는 동시 요청 둘 다 통과할 수 있으므로,
 * 데이터베이스 유일 제약과 원자적 INSERT를 최종 동시성 제어 수단으로 사용합니다.
 */
public interface ReviewIdempotencyRepository extends JpaRepository<ReviewIdempotency, Long> {

    /**
     * 사용자별 요청 키가 처음 등록된 경우에만 인증 처리 권한을 선점합니다.
     * <p>
     * 동일 키를 가진 요청이 동시에 실행되면 MySQL의 유일 키 잠금에 의해 후에 실행되는 INSERT가
     * 선행 트랜잭션의 완료를 기다립니다. 선행 요청이 커밋되면 추후 요청은 {@code 0}을 받고
     * 저장된 응답을 조회하며, 선행 요청이 롤백되면 추후 요청이 새 소유자가 될 수 있습니다.
     *
     * @param usersId 요청 사용자 식별자
     * @param idempotencyKey 클라이언트가 전달한 멱등성 키
     * @param requestFingerprint 요청 DTO와 첨부파일을 포함한 SHA-256 지문
     * @param createdAt 처리 권한 선점 시각
     * @return 새로 선점했으면 1, 이미 같은 사용자·키가 존재하면 0
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            INSERT IGNORE INTO review_idempotency (
                users_id,
                idempotency_key,
                request_fingerprint,
                created_at
            ) VALUES (
                :usersId,
                :idempotencyKey,
                :requestFingerprint,
                :createdAt
            )
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("usersId") String usersId,
            @Param("idempotencyKey") String idempotencyKey,
            @Param("requestFingerprint") String requestFingerprint,
            @Param("createdAt") LocalDateTime createdAt
    );

    /**
     * 사용자와 멱등성 키로 최초 요청 기록을 잠금 조회합니다.
     * 비관적 쓰기 잠금은 동시 요청이 같은 응답 행을 읽거나 완료 처리할 때 일관된 상태를
     * 보도록 하며, 일반 스냅샷 조회로 인해 직전에 커밋된 응답을 놓치는 상황을 방지합니다.
     *
     * @param usersId 요청 사용자 식별자
     * @param idempotencyKey 조회할 멱등성 키
     * @return 최초 요청의 지문과 완료 응답, 존재하지 않으면 빈 값
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select ri
            from ReviewIdempotency ri
            where ri.usersId = :usersId
              and ri.idempotencyKey = :idempotencyKey
            """)
    Optional<ReviewIdempotency> findByRequestKeyForUpdate(
            @Param("usersId") String usersId,
            @Param("idempotencyKey") String idempotencyKey
    );
}
