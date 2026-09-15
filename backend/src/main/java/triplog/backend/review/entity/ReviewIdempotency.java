package triplog.backend.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 방문 인증 요청의 처리 권한과 최초 성공 응답을 보관하는 멱등성 엔티티입니다.
 * <p>
 * {@code users_id + idempotency_key} 유일 제약을 통해 같은 사용자의 동일 요청이
 * 리뷰·이미지·방문 로그·지역 방문 횟수 등과 연계되어 효과를 두 번 발생시키지 못하게 합니다.
 * 요청 지문은 같은 키에 다른 요청 내용이 재사용되는 것을 식별하며, 응답 본문은 정상적인
 * 네트워크 재시도에 최초 처리 결과를 그대로 돌려주기 위해 저장합니다.
 * <p>
 * 이 행의 생성과 응답 저장은 방문 인증 전체 흐름과 같은 트랜잭션에서 처리됩니다. 따라서
 * 인증 도중 예외가 발생하면 이 행도 함께 롤백되어 같은 키로 안전하게 다시 시도할 수 있습니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_idempotency")
public class ReviewIdempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_idempotency_id", nullable = false)
    private Long reviewIdempotencyId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_fingerprint", nullable = false, length = 64)
    private String requestFingerprint;

    /** 최초 요청에서 반환한 보상 목록과 합계의 JSON 표현입니다. */
    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 최초 요청의 응답과 완료 시각을 기록합니다.
     * 트랜잭션이 커밋된 멱등성 행은 반드시 완료 응답을 가지도록 인증 흐름의 마지막에 호출합니다.
     *
     * @param responsePayload 재요청에 반환할 최초 성공 응답 JSON
     * @param completedAt 최초 요청의 처리 완료 시각
     */
    public void complete(String responsePayload, LocalDateTime completedAt) {
        this.responsePayload = responsePayload;
        this.completedAt = completedAt;
    }
}
