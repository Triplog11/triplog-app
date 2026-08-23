package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.users.repository.ActivityHistoryRepository;

/**
 * 사용자에게 표시할 통합 활동 히스토리를 기록합니다.
 */
@Service
@RequiredArgsConstructor
public class ActivityHistoryService {

    private final ActivityHistoryRepository activityHistoryRepository;

    /**
     * 이벤트 키가 중복되지 않은 경우에만 활동을 기록합니다.
     *
     * @param record 기록할 활동 정보
     */
    @Transactional
    public void record(ActivityHistoryRecord record) {
        activityHistoryRepository.insertIfAbsent(record);
    }
}
