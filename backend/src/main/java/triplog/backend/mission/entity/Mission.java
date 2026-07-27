package triplog.backend.mission.entity;

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
 * 미션 정보를 관리하는 엔티티 클래스입니다.
 * <p>
 * 데이터베이스의 {@code mission} 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mission")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id", nullable = false, unique = true)
    private Long missionId;

    @Column(name = "mission_name", nullable = false, length = 100)
    private String missionName;

    @Column(name = "mission_group")
    private Integer missionGroup;

    @Column(name = "mission_type", nullable = false, length = 50)
    private String missionType;

    @Column(name = "mission_target", nullable = false, length = 50)
    private String missionTarget;

    @Column(name = "mission_operator", nullable = false, length = 10)
    private String missionOperator;

    @Column(name = "mission_value")
    private Integer missionValue;

    @Column(name = "mission_filter", nullable = false, columnDefinition = "JSON")
    private String missionFilter;

    @Column(name = "mission_week_start", nullable = false)
    private LocalDateTime missionWeekStart;

    @Column(name = "mission_week_end", nullable = false)
    private LocalDateTime missionWeekEnd;

    @Column(name = "mission_score", nullable = false)
    private int missionScore;

    @Column(name = "mission_xp", nullable = false)
    private int missionXp;
}
