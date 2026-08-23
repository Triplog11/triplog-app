package triplog.backend.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.badge.service.BadgeService;
import triplog.backend.landmark.service.LandmarkService;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.service.ReviewService;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.entity.Users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * {@link MyPageFacadeService}의 마이페이지 정보 조합 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class MyPageFacadeServiceTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock
    private UsersService usersService;

    @Mock
    private StatsService statsService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private RegionService regionService;

    @Mock
    private BadgeService badgeService;

    @Mock
    private LandmarkService landmarkService;

    private MyPageFacadeService myPageFacadeService;

    @BeforeEach
    void setUp() {
        myPageFacadeService = new MyPageFacadeService(
                usersService,
                statsService,
                reviewService,
                regionService,
                badgeService,
                landmarkService
        );
    }

    @Test
    @DisplayName("마이페이지에 필요한 프로필과 활동 요약 정보를 조합한다")
    void getMyPageInfo() {
        // Given
        Users users = mock(Users.class);
        given(users.getNickname()).willReturn("여행자");
        given(users.getProfileUrl()).willReturn("https://example.com/profile.png");
        MyStatsResponse stats = new MyStatsResponse(
                3, 340, "BRONZE", 1250, 220,
                4, 500, 160, "SILVER", 1500
        );
        given(usersService.findById(USERS_ID)).willReturn(users);
        given(statsService.getMyStats(USERS_ID)).willReturn(stats);
        given(reviewService.countCertifications(USERS_ID)).willReturn(12);
        given(regionService.countVisitedRegions(USERS_ID)).willReturn(5);
        given(badgeService.countAcquiredBadges(USERS_ID)).willReturn(4);
        given(landmarkService.countCollectedCards(USERS_ID)).willReturn(8);

        // When
        var response = myPageFacadeService.getMyPageInfo(USERS_ID);

        // Then
        assertThat(response.getNickname()).isEqualTo("여행자");
        assertThat(response.getProfileUrl()).isEqualTo("https://example.com/profile.png");
        assertThat(response.getLevel()).isEqualTo(3);
        assertThat(response.getXp()).isEqualTo(340);
        assertThat(response.getTier()).isEqualTo("BRONZE");
        assertThat(response.getOverallScore()).isEqualTo(1250);
        assertThat(response.getMonthScore()).isEqualTo(220);
        assertThat(response.getTotalCertificationCount()).isEqualTo(12);
        assertThat(response.getVisitedRegionCount()).isEqualTo(5);
        assertThat(response.getAcquiredBadgeCount()).isEqualTo(4);
        assertThat(response.getCollectedCardCount()).isEqualTo(8);
        verify(reviewService).countCertifications(USERS_ID);
        verify(regionService).countVisitedRegions(USERS_ID);
        verify(badgeService).countAcquiredBadges(USERS_ID);
        verify(landmarkService).countCollectedCards(USERS_ID);
    }
}
