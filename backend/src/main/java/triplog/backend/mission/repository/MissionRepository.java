package triplog.backend.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.mission.entity.Mission;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 미션 엔티티의 데이터 접근을 담당하는 JPA Repository입니다.
 */
@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**
     * 미션 타입으로 미션 목록을 조회합니다.
     *
     * @param missionType 미션 타입
     * @return 해당 타입의 미션 목록
     */
    List<Mission> findByMissionType(String missionType);

    /**
     * 미션 타입과 현재 진행 중인 기간에 해당하는 미션 목록을 조회합니다.
     *
     * @param missionType 미션 타입
     * @param now 현재 시간 (시작일 이후, 종료일 이전인 미션만 조회)
     * @return 현재 활성화된 미션 목록
     */
    List<Mission> findByMissionTypeAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
            String missionType, LocalDateTime now, LocalDateTime now2
    );

    /**
     * 현재 진행 중인 기간에 해당하는 모든 미션을 조회합니다.
     *
     * @param now 현재 시간 (시작일 이후, 종료일 이전인 미션만 조회)
     * @return 현재 활성화된 전체 미션 목록
     */
    List<Mission> findByMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
            LocalDateTime now, LocalDateTime now2
    );

    /**
     * 특정 주간에 같은 이름의 미션이 존재하는지 확인합니다.
     *
     * @param missionWeekStart 주간 시작 시각
     * @param missionName      미션 이름
     * @return 미션이 존재하면 true
     */
    boolean existsByMissionWeekStartAndMissionName(
            LocalDateTime missionWeekStart,
            String missionName
    );

    /**
     * 특정 시각에 활성화된 판정 대상별 미션을 조회합니다.
     *
     * @param missionTarget 미션 판정 대상
     * @param now           기준 시각
     * @param now2          기준 시각
     * @return 활성화된 미션 목록
     */
    List<Mission> findByMissionTargetAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
            String missionTarget,
            LocalDateTime now,
            LocalDateTime now2
    );
}
