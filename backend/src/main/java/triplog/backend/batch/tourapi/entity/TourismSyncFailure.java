package triplog.backend.batch.tourapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 콘텐츠 단위 TourAPI 동기화 실패와 재처리 상태를 관리하는 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tourism_sync_failure",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tourism_sync_failure_target",
                columnNames = {"sync_type", "external_content_id"}
        ),
        indexes = @Index(
                name = "idx_tourism_sync_failure_retry",
                columnList = "sync_failure_status,last_retried_at"
        )
)
public class TourismSyncFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tourism_sync_failure_id", nullable = false)
    private Long tourismSyncFailureId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_type", nullable = false, length = 30)
    private TourismSyncType syncType;

    @Column(name = "external_content_id", nullable = false, length = 32)
    private String externalContentId;

    @Column(name = "legal_region_code", length = 10)
    private String legalRegionCode;

    @Column(name = "legal_district_code", length = 10)
    private String legalDistrictCode;

    @Column(name = "error_code", nullable = false, length = 50)
    private String errorCode;

    @Column(name = "error_message", nullable = false, length = 500)
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_failure_status", nullable = false, length = 20)
    private TourismSyncFailureStatus syncFailureStatus;

    @Column(name = "failed_at", nullable = false)
    private LocalDateTime failedAt;

    @Column(name = "last_retried_at")
    private LocalDateTime lastRetriedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /**
     * 콘텐츠 동기화 실패 이력을 생성합니다.
     *
     * @param syncType 동기화 작업 유형
     * @param externalContentId TourAPI contentId
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @param errorCode 애플리케이션 오류 코드
     * @param errorMessage 비밀정보를 제외한 실패 메시지
     * @param failedAt 최초 실패 시각
     */
    public TourismSyncFailure(
            TourismSyncType syncType,
            String externalContentId,
            String legalRegionCode,
            String legalDistrictCode,
            String errorCode,
            String errorMessage,
            LocalDateTime failedAt
    ) {
        this.syncType = syncType;
        this.externalContentId = externalContentId;
        this.legalRegionCode = legalRegionCode;
        this.legalDistrictCode = legalDistrictCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.failedAt = failedAt;
        this.syncFailureStatus = TourismSyncFailureStatus.PENDING;
    }

    /**
     * 동일 콘텐츠의 최신 실패 정보와 재시도 시각을 반영합니다.
     *
     * @param legalRegionCode 법정동 시도 코드
     * @param legalDistrictCode 법정동 시군구 코드
     * @param errorCode 애플리케이션 오류 코드
     * @param errorMessage 비밀정보를 제외한 실패 메시지
     * @param retriedAt 재시도 실패 시각
     */
    public void retryFailed(
            String legalRegionCode,
            String legalDistrictCode,
            String errorCode,
            String errorMessage,
            LocalDateTime retriedAt
    ) {
        this.legalRegionCode = legalRegionCode;
        this.legalDistrictCode = legalDistrictCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.retryCount++;
        this.lastRetriedAt = retriedAt;
        this.syncFailureStatus = TourismSyncFailureStatus.PENDING;
        this.resolvedAt = null;
    }

    /**
     * 재처리를 시작한 상태로 변경합니다.
     *
     * @param retriedAt 재처리 시작 시각
     */
    public void markRetrying(LocalDateTime retriedAt) {
        this.lastRetriedAt = retriedAt;
        this.syncFailureStatus = TourismSyncFailureStatus.RETRYING;
    }

    /**
     * 재처리가 성공한 실패 이력을 해결 상태로 변경합니다.
     *
     * @param resolvedAt 해결 시각
     */
    public void resolve(LocalDateTime resolvedAt) {
        this.syncFailureStatus = TourismSyncFailureStatus.RESOLVED;
        this.resolvedAt = resolvedAt;
    }
}
