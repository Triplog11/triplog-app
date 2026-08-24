package triplog.backend.mission.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.attractionvisitlog.service.AttractionVisitLogService;
import triplog.backend.landmarkvisitlog.service.LandmarkVisitLogService;
import triplog.backend.mission.entity.Mission;
import triplog.backend.mission.repository.MissionRepository;
import triplog.backend.mission.repository.UsersMissionRepository;
import triplog.backend.regionvisitlog.service.RegionVisitLogService;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.StatsService;
import triplog.backend.stats.service.ActivityRewardGrant;
import triplog.backend.stats.service.ActivityRewardResult;
import triplog.backend.stats.service.GrowthUpdateResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * {@link MissionAchievementService}의 기본 구현체입니다.
 * 실제 활동 로그를 미션 기간으로 집계하여 완료 여부를 판정합니다.
 */
@Service
@RequiredArgsConstructor
public class MissionAchievementServiceImpl implements MissionAchievementService {

    private final MissionRepository missionRepository;
    private final UsersMissionRepository usersMissionRepository;
    private final WeeklyMissionService weeklyMissionService;
    private final StatsService statsService;
    private final AttractionVisitLogService attractionVisitLogService;
    private final LandmarkVisitLogService landmarkVisitLogService;
    private final ReviewLogService reviewLogService;
    private final RegionVisitLogService regionVisitLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 사용자의 미션 진행 값을 실제 활동 로그에서 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param mission 미션
     * @return 현재 진행 값
     */
    @Override
    @Transactional(readOnly = true)
    public long getProgress(String usersId, Mission mission) {
        return progress(usersId, mission);
    }

    /**
     * 관광 콘텐츠 방문으로 달성 가능한 미션을 판정합니다.
     * 전달받은 콘텐츠 유형은 판정 대상을 좁히며 실제 진행 값은 방문 로그에서 집계합니다.
     *
     * @param usersId    사용자 식별자
     * @param contentType 방문 콘텐츠 유형
     */
    @Override
    @Transactional
    public List<MissionCompletionInfo> evaluateVisit(String usersId, String contentType) {
        LocalDateTime now = LocalDateTime.now();
        ensureWeeklyMissions(now);
        List<MissionCompletionInfo> completions = new ArrayList<>(
                evaluateTarget(usersId, MissionTarget.TOURISM_CONTENT_VISIT, now)
        );
        if ("LANDMARK".equals(contentType)) {
            completions.addAll(evaluateTarget(
                    usersId, MissionTarget.LANDMARK_VISIT, now
            ));
        }
        return List.copyOf(completions);
    }

    /**
     * 여행 기록 작성으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     */
    @Override
    @Transactional
    public List<MissionCompletionInfo> evaluateReview(String usersId) {
        LocalDateTime now = LocalDateTime.now();
        ensureWeeklyMissions(now);
        return evaluateTarget(usersId, MissionTarget.REVIEW, now);
    }

    /**
     * 지역 방문으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     */
    @Override
    @Transactional
    public List<MissionCompletionInfo> evaluateRegion(String usersId) {
        LocalDateTime now = LocalDateTime.now();
        ensureWeeklyMissions(now);
        return evaluateTarget(usersId, MissionTarget.REGION_VISIT, now);
    }

    /**
     * 현재 주간 미션이 존재하도록 보장합니다.
     *
     * @param now 미션 활성 기간을 판단할 기준 시각
     */
    private void ensureWeeklyMissions(LocalDateTime now) {
        weeklyMissionService.ensureWeeklyMissions(now);
    }

    /**
     * 활성 미션의 실제 진행 값을 집계하고 조건을 만족하면 완료 처리합니다.
     *
     * @param usersId 사용자 식별자
     * @param target 판정할 미션 대상
     * @param now 활성 미션 조회 기준 시각
     * @return 이번 판정에서 새로 완료된 미션 목록
     */
    private List<MissionCompletionInfo> evaluateTarget(
            String usersId, MissionTarget target, LocalDateTime now
    ) {
        return findActiveMissions(target, now).stream()
                .filter(mission -> compare(progress(usersId, mission), mission))
                .map(mission -> complete(usersId, mission))
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * 판정 대상과 기준 시각으로 활성 미션을 조회합니다.
     *
     * @param target 판정할 미션 대상
     * @param now 활성 기간 조회 기준 시각
     * @return 활성 미션 목록
     */
    private List<Mission> findActiveMissions(MissionTarget target, LocalDateTime now) {
        return missionRepository
                .findByMissionTargetAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                        target.name(), now, now
                );
    }

    /**
     * 미션 대상과 상세 조건에 따라 기간 내 진행 값을 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param mission 진행 값을 계산할 미션
     * @return 실제 활동 로그로 계산한 진행 값
     */
    private long progress(String usersId, Mission mission) {
        JsonNode filter = readFilter(mission);
        return switch (MissionTarget.valueOf(mission.getMissionTarget())) {
            case TOURISM_CONTENT_VISIT -> countTourismContentVisits(usersId, mission, filter);
            case LANDMARK_VISIT -> landmarkVisitLogService.countVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd(), visitType(filter)
            );
            case REVIEW -> reviewLogService.countTravelRecords(
                    usersId,
                    mission.getMissionWeekStart(),
                    mission.getMissionWeekEnd(),
                    filter.path("imageRequired").asBoolean(false)
            );
            case REGION_VISIT -> regionVisitLogService.countFirstVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd()
            );
        };
    }

    /**
     * 일반 관광지와 랜드마크 포함 조건을 반영해 관광 콘텐츠 방문 횟수를 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param mission 진행 값을 계산할 미션
     * @param filter 미션 상세 조건
     * @return 허용된 콘텐츠 유형의 방문 횟수 합계
     */
    private long countTourismContentVisits(String usersId, Mission mission, JsonNode filter) {
        String visitType = visitType(filter);
        boolean attractionIncluded = includesContentType(filter, "ATTRACTION");
        boolean landmarkIncluded = includesContentType(filter, "LANDMARK");
        long count = 0;
        if (attractionIncluded) {
            count += attractionVisitLogService.countVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd(), visitType
            );
        }
        if (landmarkIncluded) {
            count += landmarkVisitLogService.countVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd(), visitType
            );
        }
        return count;
    }

    /**
     * 상세 조건에서 방문 유형을 읽습니다.
     *
     * @param filter 미션 상세 조건
     * @return 방문 유형. 조건이 없으면 {@code ANY}
     */
    private String visitType(JsonNode filter) {
        return filter.path("visitType").asText("ANY");
    }

    /**
     * 상세 조건이 콘텐츠 유형을 허용하는지 확인합니다.
     *
     * @param filter 미션 상세 조건
     * @param contentType 확인할 콘텐츠 유형
     * @return 허용되면 {@code true}
     */
    private boolean includesContentType(JsonNode filter, String contentType) {
        JsonNode contentTypes = filter.path("contentTypes");
        if (!contentTypes.isArray() || contentTypes.isEmpty()) {
            return true;
        }
        for (JsonNode element : contentTypes) {
            if (contentType.equals(element.asText())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 미션 상세 조건 JSON을 읽습니다.
     *
     * @param mission 상세 조건을 가진 미션
     * @return 파싱된 상세 조건
     * @throws IllegalStateException 상세 조건이 유효한 JSON이 아닌 경우
     */
    private JsonNode readFilter(Mission mission) {
        try {
            return objectMapper.readTree(mission.getMissionFilter());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("미션 상세 조건 JSON을 해석할 수 없습니다.", exception);
        }
    }

    /**
     * 집계한 진행 값과 목표 값을 미션 연산자로 비교합니다.
     *
     * @param currentValue 현재 진행 값
     * @param mission 목표와 비교 연산자를 가진 미션
     * @return 완료 조건을 충족하면 {@code true}
     * @throws IllegalStateException 지원하지 않는 연산자인 경우
     */
    private boolean compare(long currentValue, Mission mission) {
        long targetValue = mission.getMissionValue();
        return switch (mission.getMissionOperator()) {
            case ">=" -> currentValue >= targetValue;
            case ">" -> currentValue > targetValue;
            case "=" -> currentValue == targetValue;
            case "<" -> currentValue < targetValue;
            case "<=" -> currentValue <= targetValue;
            default -> throw new IllegalStateException("지원하지 않는 미션 연산자입니다.");
        };
    }

    /**
     * 미션 완료 기록을 중복 없이 저장하고 새 완료인 경우에만 경험치를 지급합니다.
     *
     * @param usersId 사용자 식별자
     * @param mission 완료 처리할 미션
     * @return 최초 완료 정보. 이미 완료한 미션이면 빈 값
     */
    private Optional<MissionCompletionInfo> complete(String usersId, Mission mission) {
        if (usersMissionRepository.insertIfAbsent(usersId, mission.getMissionId()) == 1) {
            String missionId = mission.getMissionId().toString();
            ActivityRewardResult reward = statsService.applyActivityPolicies(
                    usersId,
                    List.of(new ActivityRewardGrant(
                            "WEEKLY_MISSION_COMPLETE:MISSION:" + missionId,
                            null,
                            "MISSION",
                            missionId,
                            "WEEKLY_MISSION_COMPLETE"
                    ))
            );
            return Optional.of(new MissionCompletionInfo(
                    mission.getMissionId(),
                    mission.getMissionName(),
                    mission.getMissionType(),
                    reward.totalXp(),
                    reward.totalScore(),
                    new GrowthUpdateResult(
                            reward.currentLevel(),
                            reward.currentTier(),
                            reward.levelUp(),
                            reward.rankUp()
                    )
            ));
        }
        return Optional.empty();
    }
}
