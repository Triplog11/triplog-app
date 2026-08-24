package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import triplog.backend.users.repository.ActivityHistoryRepository;
import triplog.backend.users.repository.ActivityHistoryQueryResult;

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

    /**
     * 사용자의 활동 히스토리를 최신순으로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 페이지 정보
     * @return 활동 히스토리 조회 결과
     */
    @Transactional(readOnly = true)
    public Page<ActivityHistoryQueryResult> getHistory(String usersId, Pageable pageable) {
        return activityHistoryRepository.findByUsersId(usersId, pageable);
    }
}
