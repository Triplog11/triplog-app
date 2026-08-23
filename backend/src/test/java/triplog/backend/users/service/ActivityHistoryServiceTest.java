package triplog.backend.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.users.repository.ActivityHistoryRepository;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * {@link ActivityHistoryService}의 중복 방지 기록 흐름을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class ActivityHistoryServiceTest {

    @Mock
    private ActivityHistoryRepository activityHistoryRepository;

    private ActivityHistoryService activityHistoryService;

    @BeforeEach
    void setUp() {
        activityHistoryService = new ActivityHistoryService(activityHistoryRepository);
    }

    @Test
    @DisplayName("활동 히스토리를 중복 방지 저장소에 전달한다")
    void record() {
        // Given
        ActivityHistoryRecord record = activityRecord();
        given(activityHistoryRepository.insertIfAbsent(record)).willReturn(1);

        // When
        activityHistoryService.record(record);

        // Then
        verify(activityHistoryRepository).insertIfAbsent(record);
    }

    private ActivityHistoryRecord activityRecord() {
        return new ActivityHistoryRecord(
                "013d613e-de7d-4a31-8661-ba8e65ff14c6",
                "LANDMARK",
                "REVIEW",
                "10",
                "REVIEW:10:LANDMARK",
                "수원화성 카드 획득",
                "랜드마크 최초 방문",
                50,
                30,
                10,
                LocalDateTime.of(2026, 8, 23, 14, 30)
        );
    }
}
