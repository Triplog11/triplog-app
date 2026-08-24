package triplog.backend.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.badge.service.BadgeService;
import triplog.backend.badge.service.RepresentativeBadgeInfo;
import triplog.backend.appellation.service.AppellationService;
import triplog.backend.appellation.service.RepresentativeAppellationInfo;
import triplog.backend.landmark.service.UsersCardLandmarkService;
import triplog.backend.region.service.RegionService;
import triplog.backend.review.service.ReviewService;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.service.StatsService;
import triplog.backend.users.entity.Users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Optional;

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
    private UsersCardLandmarkService usersCardLandmarkService;

    @Mock
    private AppellationService appellationService;

    private MyPageFacadeService myPageFacadeService;

    @BeforeEach
    void setUp() {
        myPageFacadeService = new MyPageFacadeService(
                usersService,
                statsService,
                reviewService,
                regionService,
                badgeService,
                usersCardLandmarkService,
                appellationService
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
                3, 240, "SILVER", 1250, 220,
                4, 300, 60, "GOLD", 1500
        );
        given(usersService.findById(USERS_ID)).willReturn(users);
        given(statsService.getMyStats(USERS_ID)).willReturn(stats);
        given(reviewService.countCertifications(USERS_ID)).willReturn(12);
        given(regionService.countVisitedRegions(USERS_ID)).willReturn(5);
        given(badgeService.countAcquiredBadges(USERS_ID)).willReturn(4);
        given(usersCardLandmarkService.countCollectedCards(USERS_ID)).willReturn(8);
        given(appellationService.getRepresentativeAppellation(USERS_ID))
                .willReturn(Optional.of(new RepresentativeAppellationInfo(
                        2L, "랜드마크 탐험가"
                )));
        given(badgeService.getRepresentativeBadge(USERS_ID))
                .willReturn(Optional.of(new RepresentativeBadgeInfo(
                        1L,
                        "첫 발자국",
                        "https://cdn.triplog.com/badges/first-step.png"
                )));

        // When
        var response = myPageFacadeService.getMyPageInfo(USERS_ID);

        // Then
        assertThat(response.getNickname()).isEqualTo("여행자");
        assertThat(response.getProfileUrl()).isEqualTo("https://example.com/profile.png");
        assertThat(response.getLevel()).isEqualTo(3);
        assertThat(response.getXp()).isEqualTo(240);
        assertThat(response.getTier()).isEqualTo("SILVER");
        assertThat(response.getOverallScore()).isEqualTo(1250);
        assertThat(response.getMonthScore()).isEqualTo(220);
        assertThat(response.getTotalCertificationCount()).isEqualTo(12);
        assertThat(response.getVisitedRegionCount()).isEqualTo(5);
        assertThat(response.getAcquiredBadgeCount()).isEqualTo(4);
        assertThat(response.getCollectedCardCount()).isEqualTo(8);
        assertThat(response.getRepresentativeAppellation()).isNotNull();
        assertThat(response.getRepresentativeAppellation().getAppellationId())
                .isEqualTo(2L);
        assertThat(response.getRepresentativeAppellation().getAppellationName())
                .isEqualTo("랜드마크 탐험가");
        assertThat(response.getRepresentativeBadge()).isNotNull();
        assertThat(response.getRepresentativeBadge().getBadgeId()).isEqualTo(1L);
        assertThat(response.getRepresentativeBadge().getBadgeName()).isEqualTo("첫 발자국");
        assertThat(response.getRepresentativeBadge().getBadgeUrl())
                .isEqualTo("https://cdn.triplog.com/badges/first-step.png");
        verify(reviewService).countCertifications(USERS_ID);
        verify(regionService).countVisitedRegions(USERS_ID);
        verify(badgeService).countAcquiredBadges(USERS_ID);
        verify(usersCardLandmarkService).countCollectedCards(USERS_ID);
        verify(appellationService).getRepresentativeAppellation(USERS_ID);
        verify(badgeService).getRepresentativeBadge(USERS_ID);
    }
}
