package triplog.backend.mission.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.mission.dto.response.MissionResponse.MissionEntry;
import triplog.backend.mission.dto.response.MissionResponse.MissionListResponse;
import triplog.backend.mission.dto.response.MissionResponse.MyMissionListResponse;
import triplog.backend.mission.entity.Mission;
import triplog.backend.mission.entity.UsersMission;
import triplog.backend.mission.exception.MissionException;
import triplog.backend.mission.repository.MissionRepository;
import triplog.backend.mission.repository.UsersMissionRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static triplog.backend.mission.exception.MissionErrorCode.MISSION_NOT_FOUND;
import static triplog.backend.mission.exception.MissionErrorCode.MISSION_PROGRESS_NOT_FOUND;

/**
 * {@link MissionServiceImpl}의 미션 조회 흐름을 검증하는 테스트입니다.
 */
@ExtendWith(MockitoExtension.class)
class MissionServiceImplTest {

    private static final String USERS_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private UsersMissionRepository usersMissionRepository;

    @Mock
    private MissionAchievementService missionAchievementService;

    private MissionServiceImpl missionService;

    @BeforeEach
    void setUp() {
        missionService = new MissionServiceImpl(
                missionRepository,
                usersMissionRepository,
                missionAchievementService
        );
    }

    /**
     * 미션 타입으로 조회한 활성 미션 목록과 사용자의 완료 여부를 매핑하여 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("미션 진행 현황을 정상 조회한다")
    void getMyMissions() {
        // given
        Mission mission1 = createMission(10L, "이번 주 지역 3곳 방문", "WEEKLY", "REGION_VISIT", "\"주간 내 지역 3곳 방문\"");
        Mission mission2 = createMission(11L, "랜드마크 1곳 인증", "WEEKLY", "LANDMARK_VERIFY", "\"주간 내 랜드마크 1곳 인증\"");

        UsersMission usersMission = mock(UsersMission.class);
        when(usersMission.getMission()).thenReturn(mission1);
        when(usersMission.getUsersMissionCreatedAt()).thenReturn(LocalDateTime.of(2026, 6, 25, 14, 30, 0));

        given(missionRepository.findByMissionTypeAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                eq("WEEKLY"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(mission1, mission2));
        given(usersMissionRepository.findByUsersUsersIdAndMissionMissionIdIn(eq(USERS_ID), any()))
                .willReturn(List.of(usersMission));
        given(missionAchievementService.getProgress(USERS_ID, mission1)).willReturn(3L);
        given(missionAchievementService.getProgress(USERS_ID, mission2)).willReturn(0L);

        // when
        MyMissionListResponse response = missionService.getMyMissions(USERS_ID, "WEEKLY");

        // then
        assertThat(response.getMissions()).hasSize(2);

        MissionEntry entry1 = response.getMissions().get(0);
        assertThat(entry1.getMissionId()).isEqualTo(10L);
        assertThat(entry1.getMissionName()).isEqualTo("이번 주 지역 3곳 방문");
        assertThat(entry1.getMissionCondition()).isEqualTo("주간 내 지역 3곳 방문");
        assertThat(entry1.getCompleted()).isTrue();
        assertThat(entry1.getCompletedAt()).isEqualTo("2026-06-25T14:30");
        assertThat(entry1.getCurrentValue()).isEqualTo(3L);
        assertThat(entry1.getTargetValue()).isEqualTo(3);

        MissionEntry entry2 = response.getMissions().get(1);
        assertThat(entry2.getMissionId()).isEqualTo(11L);
        assertThat(entry2.getCompleted()).isFalse();
        assertThat(entry2.getCompletedAt()).isNull();
    }

    /**
     * missionType이 null이면 전체 활성 미션을 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("missionType 미입력 시 전체 활성 미션을 조회한다")
    void getMyMissions_AllTypes() {
        // given
        Mission weekly = createMission(10L, "주간 미션", "WEEKLY", "REGION_VISIT", null);
        Mission daily = createMission(11L, "일일 미션", "DAILY", "PHOTO_UPLOAD", null);

        given(missionRepository.findByMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(weekly, daily));
        given(usersMissionRepository.findByUsersUsersIdAndMissionMissionIdIn(eq(USERS_ID), any()))
                .willReturn(List.of());

        // when
        MyMissionListResponse response = missionService.getMyMissions(USERS_ID, null);

        // then
        assertThat(response.getMissions()).hasSize(2);
    }

    /**
     * 활성 미션이 없으면 빈 목록을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("활성 미션이 없으면 빈 목록을 반환한다")
    void getMyMissions_Empty() {
        // given
        given(missionRepository.findByMissionTypeAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                eq("WEEKLY"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        MyMissionListResponse response = missionService.getMyMissions(USERS_ID, "WEEKLY");

        // then
        assertThat(response.getMissions()).isEmpty();
        verify(usersMissionRepository, never()).findByUsersUsersIdAndMissionMissionIdIn(any(), any());
    }

    /**
     * 유효하지 않은 missionType이면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("유효하지 않은 missionType이면 예외가 발생한다")
    void getMyMissions_InvalidType() {
        // when
        // then
        assertThatThrownBy(() -> missionService.getMyMissions(USERS_ID, "INVALID"))
                .isInstanceOf(MissionException.class)
                .extracting("errorCode")
                .isEqualTo(MISSION_PROGRESS_NOT_FOUND);
    }

    /**
     * 미션 타입별 활성 미션 목록을 정상 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("미션 목록을 정상 조회한다")
    void getMissions() {
        // given
        Mission mission = createMission(10L, "이번 주 지역 3곳 방문", "WEEKLY", "REGION_VISIT", "\"주간 내 지역 3곳 방문\"");

        given(missionRepository.findByMissionTypeAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                eq("WEEKLY"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(mission));

        // when
        MissionListResponse response = missionService.getMissions("WEEKLY");

        // then
        assertThat(response.getMissions()).hasSize(1);
        assertThat(response.getMissions().get(0).getMissionId()).isEqualTo(10L);
        assertThat(response.getMissions().get(0).getMissionCondition()).isEqualTo("주간 내 지역 3곳 방문");
    }

    /**
     * missionType이 null이면 전체 활성 미션을 조회하는지 검증합니다.
     */
    @Test
    @DisplayName("missionType 미입력 시 전체 미션 목록을 조회한다")
    void getMissions_AllTypes() {
        // given
        Mission weekly = createMission(10L, "주간 미션", "WEEKLY", "REGION_VISIT", null);
        Mission daily = createMission(11L, "일일 미션", "DAILY", "PHOTO_UPLOAD", null);

        given(missionRepository.findByMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(weekly, daily));

        // when
        MissionListResponse response = missionService.getMissions(null);

        // then
        assertThat(response.getMissions()).hasSize(2);
    }

    /**
     * 활성 미션이 없으면 빈 목록을 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("활성 미션이 없으면 빈 목록을 반환한다")
    void getMissions_Empty() {
        // given
        given(missionRepository.findByMissionTypeAndMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                eq("DAILY"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        MissionListResponse response = missionService.getMissions("DAILY");

        // then
        assertThat(response.getMissions()).isEmpty();
    }

    @Test
    @DisplayName("홈 화면용 활성 미션 정보를 조회한다")
    void getHomeMissions() {
        // given
        Mission mission = createMission(
                12L,
                "랜드마크 1곳 인증하기",
                "WEEKLY",
                "REVIEW_COUNT",
                "\"아무 랜드마크나 방문 인증하기\""
        );
        given(missionRepository.findByMissionWeekStartLessThanEqualAndMissionWeekEndGreaterThanEqual(
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(mission));

        // when
        List<MissionHomeInfo> result = missionService.getHomeMissions();

        // then
        assertThat(result).singleElement().satisfies(item -> {
            assertThat(item.missionId()).isEqualTo(12L);
            assertThat(item.missionName()).isEqualTo("랜드마크 1곳 인증하기");
            assertThat(item.missionFilter()).isEqualTo("아무 랜드마크나 방문 인증하기");
        });
    }

    /**
     * 유효하지 않은 missionType이면 예외가 발생하는지 검증합니다.
     */
    @Test
    @DisplayName("미션 목록 조회 시 유효하지 않은 missionType이면 예외가 발생한다")
    void getMissions_InvalidType() {
        // when
        // then
        assertThatThrownBy(() -> missionService.getMissions("INVALID"))
                .isInstanceOf(MissionException.class)
                .extracting("errorCode")
                .isEqualTo(MISSION_NOT_FOUND);
    }

    /**
     * 테스트에 사용할 Mission mock을 생성합니다.
     */
    private Mission createMission(Long id, String name, String type, String target, String filter) {
        Mission mission = mock(Mission.class);
        when(mission.getMissionId()).thenReturn(id);
        when(mission.getMissionName()).thenReturn(name);
        when(mission.getMissionType()).thenReturn(type);
        when(mission.getMissionTarget()).thenReturn(target);
        when(mission.getMissionFilter()).thenReturn(filter);
        when(mission.getMissionWeekStart()).thenReturn(LocalDateTime.of(2026, 7, 27, 0, 0, 0));
        when(mission.getMissionWeekEnd()).thenReturn(LocalDateTime.of(2026, 8, 2, 23, 59, 59));
        when(mission.getMissionScore()).thenReturn(100);
        when(mission.getMissionXp()).thenReturn(150);
        lenient().when(mission.getMissionValue()).thenReturn(3);
        return mission;
    }
}
