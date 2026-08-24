package triplog.backend.badge.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import triplog.backend.badge.dto.response.BadgeListResult;
import triplog.backend.badge.dto.response.BadgeResponse;
import triplog.backend.badge.exception.BadgeErrorCode;
import triplog.backend.badge.exception.BadgeException;
import triplog.backend.badge.repository.BadgeDetailQueryResult;
import triplog.backend.badge.repository.BadgeQueryResult;
import triplog.backend.badge.repository.BadgeRepository;
import triplog.backend.badge.repository.UsersBadgeRepository;
import triplog.backend.badge.entity.Badge;
import triplog.backend.badge.entity.UsersBadge;
import triplog.backend.achievement.service.AchievementContext;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgeServiceImplTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UsersBadgeRepository usersBadgeRepository;

    @InjectMocks
    private BadgeServiceImpl badgeService;

    @Test
    @DisplayName("획득한 배지를 대표 배지로 변경하면 기존 대표 배지는 해제된다")
    void changeRepresentativeBadge() {
        // Given
        Badge previousBadge = org.mockito.Mockito.mock(Badge.class);
        when(previousBadge.getBadgeId()).thenReturn(1L);
        Badge selectedBadge = org.mockito.Mockito.mock(Badge.class);
        when(selectedBadge.getBadgeId()).thenReturn(2L);
        when(selectedBadge.getBadgeName()).thenReturn("여행 새싹");
        when(selectedBadge.getBadgeUrl()).thenReturn("https://cdn.triplog.com/badge.png");

        UsersBadge previous = org.mockito.Mockito.mock(UsersBadge.class);
        when(previous.getBadge()).thenReturn(previousBadge);
        UsersBadge selected = org.mockito.Mockito.mock(UsersBadge.class);
        when(selected.getBadge()).thenReturn(selectedBadge);
        when(usersBadgeRepository.findAllByUsersIdForUpdate("user-id"))
                .thenReturn(List.of(previous, selected));

        // When
        BadgeResponse.RepresentativeResponse result =
                badgeService.changeRepresentativeBadge("user-id", 2L);

        // Then
        assertThat(result.badgeId()).isEqualTo(2L);
        assertThat(result.badgeName()).isEqualTo("여행 새싹");
        assertThat(result.badgeUrl()).isEqualTo("https://cdn.triplog.com/badge.png");
        assertThat(result.representative()).isTrue();
        verify(previous).changeRepresentative(false);
        verify(selected).changeRepresentative(true);
    }

    @Test
    @DisplayName("획득하지 않은 배지는 대표 배지로 지정할 수 없다")
    void changeRepresentativeBadge_NotAcquired() {
        // Given
        Badge acquiredBadge = org.mockito.Mockito.mock(Badge.class);
        when(acquiredBadge.getBadgeId()).thenReturn(1L);
        UsersBadge acquired = org.mockito.Mockito.mock(UsersBadge.class);
        when(acquired.getBadge()).thenReturn(acquiredBadge);
        when(usersBadgeRepository.findAllByUsersIdForUpdate("user-id"))
                .thenReturn(List.of(acquired));

        // When & Then
        assertThatThrownBy(() -> badgeService
                .changeRepresentativeBadge("user-id", 99L))
                .isInstanceOf(BadgeException.class)
                .extracting(exception -> ((BadgeException) exception).getErrorCode())
                .isEqualTo(BadgeErrorCode.BADGE_NOT_ACQUIRED);
    }

    @Test
    @DisplayName("대표 배지를 마이페이지 조합용 정보로 조회한다")
    void getRepresentativeBadge() {
        // Given
        Badge badge = org.mockito.Mockito.mock(Badge.class);
        when(badge.getBadgeId()).thenReturn(1L);
        when(badge.getBadgeName()).thenReturn("첫 발자국");
        when(badge.getBadgeUrl()).thenReturn("https://cdn.triplog.com/badge.png");
        UsersBadge representative = org.mockito.Mockito.mock(UsersBadge.class);
        when(representative.getBadge()).thenReturn(badge);
        when(usersBadgeRepository.findRepresentativeByUsersId("user-id"))
                .thenReturn(Optional.of(representative));

        // When
        Optional<RepresentativeBadgeInfo> result =
                badgeService.getRepresentativeBadge("user-id");

        // Then
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().badgeId()).isEqualTo(1L);
        assertThat(result.orElseThrow().badgeName()).isEqualTo("첫 발자국");
        assertThat(result.orElseThrow().badgeUrl())
                .isEqualTo("https://cdn.triplog.com/badge.png");
    }

    @Test
    @DisplayName("한 이벤트에서 조건을 충족한 여러 뱃지를 최초 획득한다")
    void acquireEligibleBadges() {
        // Given
        Badge first = org.mockito.Mockito.mock(Badge.class);
        Badge second = org.mockito.Mockito.mock(Badge.class);
        when(first.getBadgeId()).thenReturn(1L);
        when(first.getBadgeName()).thenReturn("첫 발자국");
        when(first.getBadgeTarget()).thenReturn("VISIT_COUNT");
        when(first.getBadgeOperator()).thenReturn(">=");
        when(first.getBadgeValue()).thenReturn(1);
        when(second.getBadgeId()).thenReturn(2L);
        when(second.getBadgeName()).thenReturn("여행 새싹");
        when(second.getBadgeTarget()).thenReturn("VISIT_COUNT");
        when(second.getBadgeOperator()).thenReturn(">=");
        when(second.getBadgeValue()).thenReturn(5);
        when(badgeRepository.findUnacquiredBadges("user-id"))
                .thenReturn(List.of(first, second));
        when(usersBadgeRepository.insertIfAbsent("user-id", 1L)).thenReturn(1);
        when(usersBadgeRepository.insertIfAbsent("user-id", 2L)).thenReturn(1);

        // When
        List<AcquiredBadgeInfo> result = badgeService.acquireEligibleBadges(
                "user-id",
                new AchievementContext(Map.of("VISIT_COUNT", 5L))
        );

        // Then
        assertThat(result).extracting(AcquiredBadgeInfo::badgeName)
                .containsExactly("첫 발자국", "여행 새싹");
    }

    @Test
    @DisplayName("이미 획득한 뱃지는 동시 판정에서도 획득 결과에 포함하지 않는다")
    void acquireEligibleBadges_AlreadyAcquired() {
        // Given
        Badge badge = org.mockito.Mockito.mock(Badge.class);
        when(badge.getBadgeId()).thenReturn(1L);
        when(badge.getBadgeTarget()).thenReturn("VISIT_COUNT");
        when(badge.getBadgeOperator()).thenReturn(">=");
        when(badge.getBadgeValue()).thenReturn(1);
        when(badgeRepository.findUnacquiredBadges("user-id")).thenReturn(List.of(badge));
        when(usersBadgeRepository.insertIfAbsent("user-id", 1L)).thenReturn(0);

        // When
        List<AcquiredBadgeInfo> result = badgeService.acquireEligibleBadges(
                "user-id",
                new AchievementContext(Map.of("VISIT_COUNT", 1L))
        );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자가 획득한 배지 수를 조회한다")
    void countAcquiredBadges() {
        // Given
        when(badgeRepository.countAcquiredBadgesByUsersId("user-id")).thenReturn(4L);

        // When
        int result = badgeService.countAcquiredBadges("user-id");

        // Then
        assertThat(result).isEqualTo(4);
    }

    @Test
    @DisplayName("배지 ID로 상세 정보와 사용자 획득 상태를 조회한다")
    void getBadgeDetail() {
        // Given
        BadgeDetailQueryResult result = new BadgeDetailQueryResult(
                1L, "첫 발자국", "https://cdn.triplog.com/badge.png", 1,
                "REVIEW", "REVIEW_COUNT", ">=", 1, 1, 0);
        when(badgeRepository.findBadgeDetail(1L, "user-id")).thenReturn(Optional.of(result));

        // When
        BadgeResponse.BadgeDetailResponse response = badgeService.getBadgeDetail("user-id", 1L);

        // Then
        assertThat(response.badgeId()).isEqualTo(1L);
        assertThat(response.badgeGroup()).isEqualTo(1);
        assertThat(response.badgeOperator()).isEqualTo(">=");
        assertThat(response.acquired()).isTrue();
        assertThat(response.representative()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 배지 ID로 상세 조회하면 404 예외를 던진다")
    void getMissingBadgeDetail() {
        // Given
        when(badgeRepository.findBadgeDetail(999L, "user-id")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> badgeService.getBadgeDetail("user-id", 999L))
                .isInstanceOf(BadgeException.class)
                .extracting(exception -> ((BadgeException) exception).getErrorCode())
                .isEqualTo(BadgeErrorCode.BADGE_DETAIL_NOT_FOUND);
    }

    @Test
    @DisplayName("전체 목록은 획득 상태와 배지 조건을 포함한다")
    void getAllBadges() {
        PageRequest pageable = PageRequest.of(0, 10);
        BadgeQueryResult item = item();
        when(badgeRepository.findBadges("user-id", "REVIEW", null, pageable))
                .thenReturn(new PageImpl<>(List.of(item), pageable, 1));

        BadgeListResult response = badgeService.getBadges("user-id", " review ", null, pageable);

        assertThat(response).isInstanceOf(BadgeResponse.BadgeListResponse.class);
        BadgeResponse.BadgeListResponse badgeResponse = (BadgeResponse.BadgeListResponse) response;
        assertThat(badgeResponse.items()).hasSize(1);
        assertThat(badgeResponse.items().getFirst().acquired()).isTrue();
        verify(badgeRepository).findBadges("user-id", "REVIEW", null, pageable);
    }

    @Test
    @DisplayName("획득 목록은 간소화된 응답을 반환한다")
    void getAcquiredBadges() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(badgeRepository.findBadges("user-id", null, true, pageable))
                .thenReturn(new PageImpl<>(List.of(item()), pageable, 1));

        BadgeListResult response = badgeService.getBadges("user-id", " ", true, pageable);

        assertThat(response).isInstanceOf(BadgeResponse.BadgeListAcquiredResponse.class);
        BadgeResponse.BadgeListAcquiredResponse badgeResponse =
                (BadgeResponse.BadgeListAcquiredResponse) response;
        assertThat(badgeResponse.items().getFirst().representative()).isFalse();
    }

    @Test
    @DisplayName("요청 페이지가 전체 페이지 범위를 벗어나면 404 예외를 던진다")
    void getBadgesWithOutOfRangePage() {
        // Given
        PageRequest pageable = PageRequest.of(2, 10);
        when(badgeRepository.findBadges("user-id", null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 14));

        // When & Then
        assertThatThrownBy(() -> badgeService.getBadges("user-id", null, null, pageable))
                .isInstanceOf(BadgeException.class)
                .extracting(exception -> ((BadgeException) exception).getErrorCode())
                .isEqualTo(BadgeErrorCode.BADGES_NOT_FOUND);
    }

    @Test
    @DisplayName("배지가 없어도 첫 페이지 요청은 빈 목록을 반환한다")
    void getEmptyFirstPage() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        when(badgeRepository.findBadges("user-id", null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        // When
        BadgeListResult response = badgeService.getBadges("user-id", null, null, pageable);

        // Then
        assertThat(response).isInstanceOf(BadgeResponse.BadgeListResponse.class);
        assertThat(((BadgeResponse.BadgeListResponse) response).items()).isEmpty();
    }

    private BadgeQueryResult item() {
        return new BadgeQueryResult(1L, "첫 발자국", "https://cdn.triplog.com/badge.png",
                "REVIEW", "REVIEW_COUNT", 1, 1, 0);
    }
}
