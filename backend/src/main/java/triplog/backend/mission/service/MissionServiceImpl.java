package triplog.backend.mission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.mission.dto.response.MissionResponse.MissionEntry;
import triplog.backend.mission.dto.response.MissionResponse.MissionListResponse;
import triplog.backend.mission.dto.response.MissionResponse.MissionSummary;
import triplog.backend.mission.dto.response.MissionResponse.MyMissionListResponse;
import triplog.backend.mission.entity.Mission;
import triplog.backend.mission.entity.UsersMission;
import triplog.backend.mission.exception.MissionException;
import triplog.backend.mission.repository.MissionRepository;
import triplog.backend.mission.repository.UsersMissionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static triplog.backend.mission.exception.MissionErrorCode.MISSION_NOT_FOUND;
import static triplog.backend.mission.exception.MissionErrorCode.MISSION_PROGRESS_NOT_FOUND;

/**
 * {@link MissionService}의 구현 클래스입니다.
 * <p>
 * 사용자의 미션 진행 현황을 조회하는 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final UsersMissionRepository usersMissionRepository;
    private final MissionAchievementService missionAchievementService;

    /**
     * 홈 화면에 노출할 현재 활성 미션 정보를 조회합니다.
     *
     * @return 활성 미션 목록
     */
    @Override
    public List<MissionHomeInfo> getHomeMissions() {
        LocalDateTime now = LocalDateTime.now();
        return findActiveMissions(null, now).stream()
                .map(MissionHomeInfo::from)
                .toList();
    }

    /**
     * 로그인 사용자의 미션 진행 현황을 조회합니다.
     * <p>
     * 현재 진행 중인 미션(시작일 &lt;= 현재 &lt;= 종료일)만 조회한 후,
     * 사용자의 완료 여부를 매핑하여 반환합니다.
     * missionType이 null이면 전체 타입을 조회합니다.
     * 결과가 없으면 빈 목록을 반환합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @param missionType 미션 타입 (필터 조건, null이면 전체)
     * @return 미션 진행 현황 목록
     * @throws MissionException 유효하지 않은 미션 타입인 경우
     */
    @Override
    public MyMissionListResponse getMyMissions(String usersId, String missionType) {
        if (missionType != null && !"WEEKLY".equals(missionType)) {
            throw new MissionException(MISSION_PROGRESS_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        List<Mission> missions = findActiveMissions(missionType, now);

        if (missions.isEmpty()) {
            return new MyMissionListResponse(List.of());
        }

        List<Long> missionIds = missions.stream()
                .map(Mission::getMissionId)
                .toList();

        List<UsersMission> completedMissions =
                usersMissionRepository.findByUsersUsersIdAndMissionMissionIdIn(usersId, missionIds);

        Map<Long, UsersMission> completedMap = completedMissions.stream()
                .collect(Collectors.toMap(
                        um -> um.getMission().getMissionId(),
                        um -> um
                ));

        List<MissionEntry> entries = missions.stream()
                .map(mission -> MissionEntry.toDto(
                        mission,
                        completedMap.get(mission.getMissionId()),
                        missionAchievementService.getProgress(usersId, mission)
                ))
                .toList();

        return new MyMissionListResponse(entries);
    }

    /**
     * 미션 타입별 현재 진행 중인 미션 목록을 조회합니다.
     * <p>
     * 현재 시간 기준으로 시작일 &lt;= 현재 &lt;= 종료일인 미션만 반환합니다.
     * missionType이 null이면 전체 타입을 조회합니다.
     * 결과가 없으면 빈 목록을 반환합니다.
     *
     * @param missionType 미션 타입 (필터 조건, null이면 전체)
     * @return 미션 목록
     * @throws MissionException 유효하지 않은 미션 타입인 경우
     */
    @Override
    public MissionListResponse getMissions(String missionType) {
        if (missionType != null && !"WEEKLY".equals(missionType)) {
            throw new MissionException(MISSION_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        List<Mission> missions = findActiveMissions(missionType, now);

        if (missions.isEmpty()) {
            return new MissionListResponse(List.of());
        }

        List<MissionSummary> summaries = missions.stream()
                .map(MissionSummary::toDto)
                .toList();

        return new MissionListResponse(summaries);
    }

    /**
     * 미션 타입과 현재 시간 기준으로 활성화된 미션을 조회합니다.
     *
     * @param missionType 미션 타입 (null이면 전체)
     * @param now 현재 시간
     * @return 활성화된 미션 목록
     */
    private List<Mission> findActiveMissions(String missionType, LocalDateTime now) {
        if (missionType == null) {
            return missionRepository
                    .findByMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(now, now);
        }
        return missionRepository
                .findByMissionTypeAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                        missionType, now, now);
    }
}
