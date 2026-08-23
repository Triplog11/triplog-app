package triplog.backend.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import triplog.backend.users.repository.ActivityHistoryQueryResult;
import triplog.backend.users.repository.ActivityHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * {@link ActivityHistoryFacadeService}의 활동 히스토리 변환 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class ActivityHistoryFacadeServiceTest {

    private static final String USERS_ID = "013d613e-de7d-4a31-8661-ba8e65ff14c6";

    @Mock
    private ActivityHistoryRepository activityHistoryRepository;

    private ActivityHistoryFacadeService activityHistoryFacadeService;

    @BeforeEach
    void setUp() {
        activityHistoryFacadeService = new ActivityHistoryFacadeService(activityHistoryRepository);
    }

    @Test
    @DisplayName("통합 활동 로그를 명세의 페이지 응답으로 변환한다")
    void getActivityHistory() {
        // Given
        PageRequest pageable = PageRequest.of(0, 10);
        ActivityHistoryQueryResult activity = new ActivityHistoryQueryResult(
                101L,
                "BADGE",
                "여행 입문자 뱃지 획득",
                "첫 방문 인증을 완료했습니다.",
                100,
                30,
                LocalDateTime.of(2026, 8, 23, 14, 30)
        );
        given(activityHistoryRepository.findByUsersId(USERS_ID, pageable))
                .willReturn(new PageImpl<>(List.of(activity), pageable, 24));

        // When
        var response = activityHistoryFacadeService.getActivityHistory(USERS_ID, pageable);

        // Then
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(24);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getActivities()).hasSize(1);
        var item = response.getActivities().getFirst();
        assertThat(item.getActivityId()).isEqualTo(101L);
        assertThat(item.getActivityType()).isEqualTo("BADGE");
        assertThat(item.getTitle()).isEqualTo("여행 입문자 뱃지 획득");
        assertThat(item.getContent()).isEqualTo("첫 방문 인증을 완료했습니다.");
        assertThat(item.getScore()).isEqualTo(100);
        assertThat(item.getXp()).isEqualTo(30);
        assertThat(item.getCreatedAt()).isEqualTo("2026-08-23T14:30:00");
        verify(activityHistoryRepository).findByUsersId(USERS_ID, pageable);
    }
}
