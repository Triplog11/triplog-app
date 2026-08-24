package triplog.backend.regionvisitlog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import triplog.backend.regionvisitlog.entity.RegionVisitLog;
import triplog.backend.regionvisitlog.repository.RegionVisitLogRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RegionVisitLogServiceImplTest {

    @Mock
    private RegionVisitLogRepository regionVisitLogRepository;

    private RegionVisitLogServiceImpl regionVisitLogService;

    @BeforeEach
    void setUp() {
        regionVisitLogService = new RegionVisitLogServiceImpl(regionVisitLogRepository);
    }

    @Test
    @DisplayName("재방문 이후 최근에 연속으로 방문한 새로운 지역 수를 계산한다")
    void countConsecutiveNewRegionVisits() {
        // Given
        List<RegionVisitLog> visitLogs = List.of(
                visit(1L), visit(2L), visit(1L), visit(3L), visit(4L), visit(5L)
        );
        given(regionVisitLogRepository
                .findByUsersIdOrderByVisitedAtAscRegionVisitLogIdAsc("user-id"))
                .willReturn(visitLogs);

        // When
        int result = regionVisitLogService.countConsecutiveNewRegionVisits("user-id");

        // Then
        assertThat(result).isEqualTo(3);
    }

    private RegionVisitLog visit(Long regionId) {
        RegionVisitLog visitLog = mock(RegionVisitLog.class);
        given(visitLog.getRegionId()).willReturn(regionId);
        return visitLog;
    }
}
