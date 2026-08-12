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

    /**
     * 주간 미션을 생성합니다.
     *
     * @param missionName      미션 이름
     * @param missionGroup     미션 그룹
     * @param missionTarget    미션 판정 대상
     * @param missionValue     완료에 필요한 값
     * @param missionFilter    미션 상세 조건
     * @param missionWeekStart 주간 시작 시각
     * @param missionWeekEnd   주간 종료 시각
     * @param missionXp        완료 보상 경험치
     */
    public Mission(
            String missionName,
            Integer missionGroup,
            String missionTarget,
            Integer missionValue,
            String missionFilter,
            LocalDateTime missionWeekStart,
            LocalDateTime missionWeekEnd,
            int missionXp
    ) {
        this.missionName = missionName;
        this.missionGroup = missionGroup;
        this.missionType = "WEEKLY";
        this.missionTarget = missionTarget;
        this.missionOperator = ">=";
        this.missionValue = missionValue;
        this.missionFilter = missionFilter;
        this.missionWeekStart = missionWeekStart;
        this.missionWeekEnd = missionWeekEnd;
        this.missionScore = 0;
        this.missionXp = missionXp;
    }

    /**
     * 일일 미션을 생성합니다.
     *
     * @param missionName      미션 이름
     * @param missionGroup     난이도 그룹
     * @param missionTarget    미션 판정 대상
     * @param missionValue     완료에 필요한 값
     * @param missionFilter    미션 상세 조건
     * @param missionDayStart  일일 시작 시각
     * @param missionDayEnd    일일 종료 시각
     * @param missionXp        완료 보상 경험치
     * @return 생성된 일일 미션
     */
    public static Mission createDaily(
            String missionName,
            Integer missionGroup,
            String missionTarget,
            Integer missionValue,
            String missionFilter,
            LocalDateTime missionDayStart,
            LocalDateTime missionDayEnd,
            int missionXp
    ) {
        Mission mission = new Mission();
        mission.missionName = missionName;
        mission.missionGroup = missionGroup;
        mission.missionType = "DAILY";
        mission.missionTarget = missionTarget;
        mission.missionOperator = ">=";
        mission.missionValue = missionValue;
        mission.missionFilter = missionFilter;
        mission.missionWeekStart = missionDayStart;
        mission.missionWeekEnd = missionDayEnd;
        mission.missionScore = 0;
        mission.missionXp = missionXp;
        return mission;
    }
}
