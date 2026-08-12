package triplog.backend.mission.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.attractionvisitlog.repository.AttractionVisitLogRepository;
import triplog.backend.landmarkvisitlog.repository.LandmarkVisitLogRepository;
import triplog.backend.mission.entity.Mission;
import triplog.backend.mission.repository.MissionRepository;
import triplog.backend.mission.repository.UsersMissionRepository;
import triplog.backend.regionvisitlog.repository.RegionVisitLogRepository;
import triplog.backend.reviewlog.repository.ReviewLogRepository;
import triplog.backend.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

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
    private final DailyMissionService dailyMissionService;
    private final StatsService statsService;
    private final AttractionVisitLogRepository attractionVisitLogRepository;
    private final LandmarkVisitLogRepository landmarkVisitLogRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final RegionVisitLogRepository regionVisitLogRepository;
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
     * 전달받은 이벤트 정보는 판정 시점을 나타내며 실제 진행 값은 방문 로그에서 집계합니다.
     *
     * @param usersId    사용자 식별자
     * @param contentType 방문 콘텐츠 유형
     * @param firstVisit 최초 방문 여부
     */
    @Override
    @Transactional
    public void evaluateVisit(String usersId, String contentType, boolean firstVisit) {
        LocalDateTime now = LocalDateTime.now();
        ensureMissions(now);
        evaluateTarget(usersId, MissionTarget.TOURISM_CONTENT_VISIT, now);
        if ("LANDMARK".equals(contentType)) {
            evaluateTarget(usersId, MissionTarget.LANDMARK_VISIT, now);
        }
    }

    /**
     * 여행 기록 작성으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     */
    @Override
    @Transactional
    public void evaluateReview(String usersId) {
        LocalDateTime now = LocalDateTime.now();
        ensureMissions(now);
        evaluateTarget(usersId, MissionTarget.REVIEW, now);
    }

    /**
     * 지역 방문으로 달성 가능한 미션을 판정합니다.
     *
     * @param usersId 사용자 식별자
     */
    @Override
    @Transactional
    public void evaluateRegion(String usersId) {
        LocalDateTime now = LocalDateTime.now();
        ensureMissions(now);
        evaluateTarget(usersId, MissionTarget.REGION_VISIT, now);
    }

    /**
     * 현재 일일 및 주간 미션이 존재하도록 보장합니다.
     */
    private void ensureMissions(LocalDateTime now) {
        dailyMissionService.ensureDailyMissions(now);
        weeklyMissionService.ensureWeeklyMissions(now);
    }

    /**
     * 활성 미션의 실제 진행 값을 집계하고 조건을 만족하면 완료 처리합니다.
     */
    private void evaluateTarget(String usersId, MissionTarget target, LocalDateTime now) {
        findActiveMissions(target, now).stream()
                .filter(mission -> compare(progress(usersId, mission), mission))
                .forEach(mission -> complete(usersId, mission));
    }

    /**
     * 판정 대상과 기준 시각으로 활성 미션을 조회합니다.
     */
    private List<Mission> findActiveMissions(MissionTarget target, LocalDateTime now) {
        return missionRepository
                .findByMissionTargetAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                        target.name(), now, now
                );
    }

    /**
     * 미션 대상과 상세 조건에 따라 기간 내 진행 값을 집계합니다.
     */
    private long progress(String usersId, Mission mission) {
        JsonNode filter = readFilter(mission);
        return switch (MissionTarget.valueOf(mission.getMissionTarget())) {
            case TOURISM_CONTENT_VISIT -> countTourismContentVisits(usersId, mission, filter);
            case LANDMARK_VISIT -> landmarkVisitLogRepository.countVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd(), visitType(filter)
            );
            case REVIEW -> reviewLogRepository.countTravelRecords(
                    usersId,
                    mission.getMissionWeekStart(),
                    mission.getMissionWeekEnd(),
                    filter.path("imageRequired").asBoolean(false)
            );
            case REGION_VISIT -> regionVisitLogRepository.countFirstVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd()
            );
        };
    }

    /**
     * 일반 관광지와 랜드마크 포함 조건을 반영해 관광 콘텐츠 방문 횟수를 집계합니다.
     */
    private long countTourismContentVisits(String usersId, Mission mission, JsonNode filter) {
        String visitType = visitType(filter);
        boolean attractionIncluded = includesContentType(filter, "ATTRACTION");
        boolean landmarkIncluded = includesContentType(filter, "LANDMARK");
        long count = 0;
        if (attractionIncluded) {
            count += attractionVisitLogRepository.countVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd(), visitType
            );
        }
        if (landmarkIncluded) {
            count += landmarkVisitLogRepository.countVisits(
                    usersId, mission.getMissionWeekStart(), mission.getMissionWeekEnd(), visitType
            );
        }
        return count;
    }

    /**
     * 상세 조건에서 방문 유형을 읽습니다.
     */
    private String visitType(JsonNode filter) {
        return filter.path("visitType").asText("ANY");
    }

    /**
     * 상세 조건이 콘텐츠 유형을 허용하는지 확인합니다.
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
     */
    private void complete(String usersId, Mission mission) {
        if (usersMissionRepository.insertIfAbsent(usersId, mission.getMissionId()) == 1) {
            statsService.addXpAndScore(usersId, mission.getMissionXp(), 0);
        }
    }
}
