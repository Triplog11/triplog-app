package triplog.backend.regionvisitlog.entity;

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
 * 지역 방문 로그 엔티티입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "region_visit_log")
public class RegionVisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_visit_log_id", nullable = false)
    private Long regionVisitLogId;

    @Column(name = "users_id", nullable = false, length = 36)
    private String usersId;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    /**
     * 지역 방문 로그를 생성합니다.
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 식별자
     */
    public RegionVisitLog(String usersId, Long regionId) {
        this.usersId = usersId;
        this.regionId = regionId;
        this.visitedAt = LocalDateTime.now();
    }
}
