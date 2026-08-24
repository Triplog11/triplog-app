package triplog.backend.mission.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.attractionvisitlog.service.AttractionVisitLogService;
import triplog.backend.landmarkvisitlog.service.LandmarkVisitLogService;
import triplog.backend.mission.entity.Mission;
import triplog.backend.mission.repository.MissionRepository;
import triplog.backend.mission.repository.UsersMissionRepository;
import triplog.backend.regionvisitlog.service.RegionVisitLogService;
import triplog.backend.reviewlog.service.ReviewLogService;
import triplog.backend.stats.service.ActivityRewardResult;
import triplog.backend.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * 미션 최초 완료와 보상 결과 반환 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class MissionAchievementServiceImplTest {

    private static final String USERS_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Mock private MissionRepository missionRepository;
    @Mock private UsersMissionRepository usersMissionRepository;
    @Mock private WeeklyMissionService weeklyMissionService;
    @Mock private StatsService statsService;
    @Mock private AttractionVisitLogService attractionVisitLogService;
    @Mock private LandmarkVisitLogService landmarkVisitLogService;
    @Mock private ReviewLogService reviewLogService;
    @Mock private RegionVisitLogService regionVisitLogService;

    private MissionAchievementServiceImpl missionAchievementService;

    @BeforeEach
    void setUp() {
        missionAchievementService = new MissionAchievementServiceImpl(
                missionRepository,
                usersMissionRepository,
                weeklyMissionService,
                statsService,
                attractionVisitLogService,
                landmarkVisitLogService,
                reviewLogService,
                regionVisitLogService
        );
    }

    @Test
    @DisplayName("미션을 최초 완료하면 보상을 지급하고 완료 정보를 반환한다")
    void evaluateVisit_ReturnsNewCompletion() {
        // Given
        Mission mission = mock(Mission.class);
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        given(mission.getMissionId()).willReturn(101L);
        given(mission.getMissionName()).willReturn("여행 한 걸음");
        given(mission.getMissionType()).willReturn("WEEKLY");
        given(mission.getMissionTarget()).willReturn("TOURISM_CONTENT_VISIT");
        given(mission.getMissionOperator()).willReturn(">=");
        given(mission.getMissionValue()).willReturn(1);
        given(mission.getMissionFilter()).willReturn(
                "{\"contentTypes\":[\"ATTRACTION\"],\"visitType\":\"ANY\"}"
        );
        given(mission.getMissionWeekStart()).willReturn(start);
        given(mission.getMissionWeekEnd()).willReturn(end);
        given(missionRepository
                .findByMissionTargetAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                        eq("TOURISM_CONTENT_VISIT"), any(LocalDateTime.class), any(LocalDateTime.class)
                )).willReturn(List.of(mission));
        given(attractionVisitLogService.countVisits(USERS_ID, start, end, "ANY"))
                .willReturn(1L);
        given(usersMissionRepository.insertIfAbsent(USERS_ID, 101L)).willReturn(1);
        given(statsService.applyActivityPolicies(eq(USERS_ID), any()))
                .willReturn(new ActivityRewardResult(
                        List.of(), 30, 0, 2, "BRONZE", true, false
                ));

        // When
        List<MissionCompletionInfo> completions = missionAchievementService
                .evaluateVisit(USERS_ID, "ATTRACTION");

        // Then
        assertThat(completions).singleElement().satisfies(completion -> {
            assertThat(completion.missionId()).isEqualTo(101L);
            assertThat(completion.missionType()).isEqualTo("WEEKLY");
            assertThat(completion.xp()).isEqualTo(30);
            assertThat(completion.growth().levelUp()).isTrue();
        });
        verify(statsService).applyActivityPolicies(eq(USERS_ID), any());
    }
}
