package triplog.backend.batch.tourapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 동기화 유형별 마지막 성공시각을 관리하는 체크포인트 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "tourism_sync_checkpoint",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tourism_sync_checkpoint_type",
                columnNames = "sync_type"
        )
)
public class TourismSyncCheckpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tourism_sync_checkpoint_id", nullable = false)
    private Long tourismSyncCheckpointId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_type", nullable = false, length = 30)
    private TourismSyncType syncType;

    @Column(name = "last_succeeded_at", nullable = false)
    private LocalDateTime lastSucceededAt;

    /**
     * 동기화 유형과 최초 성공시각으로 체크포인트를 생성합니다.
     *
     * @param syncType 동기화 작업 유형
     * @param lastSucceededAt 마지막 성공시각
     */
    public TourismSyncCheckpoint(TourismSyncType syncType, LocalDateTime lastSucceededAt) {
        this.syncType = syncType;
        this.lastSucceededAt = lastSucceededAt;
    }

    /**
     * 작업이 완전히 성공한 시각으로 체크포인트를 갱신합니다.
     *
     * @param succeededAt 성공 완료 시각
     */
    public void update(LocalDateTime succeededAt) {
        this.lastSucceededAt = succeededAt;
    }
}
