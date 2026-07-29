package triplog.backend.landmarkvisitlog.entity;

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
 * 랜드마크 방문 로그 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "landmark_visit_log")
public class LandmarkVisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "landmark_visit_log_id", nullable = false)
    private Long landmarkVisitLogId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @Column(name = "landmark_id", nullable = false)
    private Long landmarkId;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    /**
     * 랜드마크 방문 로그를 생성합니다.
     *
     * @param usersId    사용자 식별자
     * @param landmarkId 랜드마크 식별자
     */
    public LandmarkVisitLog(String usersId, Long landmarkId) {
        this.usersId = usersId;
        this.landmarkId = landmarkId;
        this.visitedAt = LocalDateTime.now();
    }
}
