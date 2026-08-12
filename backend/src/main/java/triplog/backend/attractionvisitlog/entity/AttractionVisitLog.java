package triplog.backend.attractionvisitlog.entity;

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
 * 일반 관광지 방문 로그 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "attraction_visit_log")
public class AttractionVisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attraction_visit_log_id", nullable = false)
    private Long attractionVisitLogId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @Column(name = "attraction_id", nullable = false)
    private Long attractionId;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    /**
     * 일반 관광지 방문 로그를 생성합니다.
     *
     * @param usersId      사용자 식별자
     * @param attractionId 일반 관광지 식별자
     */
    public AttractionVisitLog(String usersId, Long attractionId) {
        this.usersId = usersId;
        this.attractionId = attractionId;
        this.visitedAt = LocalDateTime.now();
    }
}
