package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.users.dto.response.MyPageResponse.ActivityHistoryResponse;

/**
 * 통합 활동 로그 조회와 API 응답 변환 흐름을 조합합니다.
 */
@Service
@RequiredArgsConstructor
public class ActivityHistoryFacadeService {

    private final ActivityHistoryService activityHistoryService;

    /**
     * 로그인 사용자의 활동 히스토리를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 페이지 정보
     * @return 활동 히스토리 응답
     */
    @Transactional(readOnly = true)
    public ActivityHistoryResponse getActivityHistory(String usersId, Pageable pageable) {
        return ActivityHistoryResponse.toDto(
                activityHistoryService.getHistory(usersId, pageable)
        );
    }
}
