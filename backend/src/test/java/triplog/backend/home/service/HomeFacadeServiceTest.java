package triplog.backend.home.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.landmark.service.LandmarkHomeCardInfo;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.mission.service.MissionHomeInfo;
import triplog.backend.mission.service.MissionService;
import triplog.backend.region.service.RegionHomeInfo;
import triplog.backend.region.service.RegionService;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * {@link HomeFacadeService}의 홈 정보 조합 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class HomeFacadeServiceTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock
    private UsersService usersService;

    @Mock
    private StatsService statsService;

    @Mock
    private MissionService missionService;

    @Mock
    private LandmarkService landmarkService;

    @Mock
    private RegionService regionService;

    private HomeFacadeService homeFacadeService;

    @BeforeEach
    void setUp() {
        homeFacadeService = new HomeFacadeService(
                usersService,
                statsService,
                missionService,
                landmarkService,
                regionService
        );
    }

    @Test
    @DisplayName("홈 화면에 필요한 여러 도메인의 정보를 한 응답으로 조합한다")
    void getHomeInfo() {
        // Given
        Users users = mock(Users.class);
        given(users.getNickname()).willReturn("로컬 여행자");
        MyStatsResponse stats = new MyStatsResponse(
                5, 720, "BRONZE", 27, 220,
                6, 900, 180, "SILVER", 500
        );
        MissionHomeInfo mission = new MissionHomeInfo(
                12L,
                "랜드마크 1곳 인증하기",
                "WEEKLY",
                "REVIEW_COUNT",
                ">=",
                1,
                "아무 랜드마크나 방문 인증하기",
                LocalDateTime.of(2026, 7, 1, 10, 0),
                LocalDateTime.of(2026, 7, 7, 18, 0),
                100,
                50
        );
        LandmarkHomeCardInfo card = new LandmarkHomeCardInfo(
                301L, "수원화성", "41110", "RARE", "수원 화성", "image.com"
        );
        RegionHomeInfo region = new RegionHomeInfo(
                203L,
                "수원시",
                "수원 설명",
                "41110",
                LocalDateTime.of(2026, 7, 1, 10, 0),
                1
        );
        given(usersService.findById(USERS_ID)).willReturn(users);
        given(statsService.getMyStats(USERS_ID)).willReturn(stats);
        given(missionService.getHomeMissions()).willReturn(List.of(mission));
        given(landmarkService.getRecentObtainedCardInfo(USERS_ID, 3)).willReturn(List.of(card));
        given(regionService.getRecentVisitedRegionInfo(USERS_ID, 3)).willReturn(List.of(region));

        // When
        var response = homeFacadeService.getHomeInfo(USERS_ID);

        // Then
        assertThat(response.getLevelInformation()).singleElement().satisfies(level -> {
            assertThat(level.getLevel()).isEqualTo(5);
            assertThat(level.getNickname()).isEqualTo("로컬 여행자");
            assertThat(level.getXp()).isEqualTo(720);
            assertThat(level.getLevelPolicy()).isEqualTo(900);
        });
        assertThat(response.getRankInformation().getCurrentTier()).isEqualTo("BRONZE");
        assertThat(response.getMissionInformation()).singleElement().satisfies(item -> {
            assertThat(item.getMissionId()).isEqualTo(12L);
            assertThat(item.getMissionWeekStart()).isEqualTo("2026-07-01T10:00:00");
        });
        assertThat(response.getCardInformation()).singleElement()
                .extracting("landmarkId", "landmarkZipcode")
                .containsExactly(301L, "41110");
        assertThat(response.getRegionInformation()).singleElement().satisfies(item -> {
            assertThat(item.getRegionId()).isEqualTo(203L);
            assertThat(item.getVisitedAt()).isEqualTo("2026-07-01T10:00:00");
            assertThat(item.getVisitedCount()).isEqualTo(1);
        });
        verify(landmarkService).getRecentObtainedCardInfo(USERS_ID, 3);
        verify(regionService).getRecentVisitedRegionInfo(USERS_ID, 3);
    }

    @Test
    @DisplayName("획득 카드와 방문 지역 및 활성 미션이 없으면 빈 목록을 반환한다")
    void getHomeInfo_EmptyCollections() {
        // Given
        Users users = mock(Users.class);
        given(users.getNickname()).willReturn("여행자");
        given(usersService.findById(USERS_ID)).willReturn(users);
        given(statsService.getMyStats(USERS_ID)).willReturn(new MyStatsResponse(
                1, 0, "BRONZE", 0, 0,
                2, 100, 100, "SILVER", 500
        ));
        given(missionService.getHomeMissions()).willReturn(List.of());
        given(landmarkService.getRecentObtainedCardInfo(USERS_ID, 3)).willReturn(List.of());
        given(regionService.getRecentVisitedRegionInfo(USERS_ID, 3)).willReturn(List.of());

        // When
        var response = homeFacadeService.getHomeInfo(USERS_ID);

        // Then
        assertThat(response.getMissionInformation()).isEmpty();
        assertThat(response.getCardInformation()).isEmpty();
        assertThat(response.getRegionInformation()).isEmpty();
    }
}
