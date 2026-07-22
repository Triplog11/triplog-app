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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadgeServiceImplTest {

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private BadgeServiceImpl badgeService;

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
