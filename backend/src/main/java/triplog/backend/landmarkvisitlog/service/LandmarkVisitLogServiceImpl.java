package triplog.backend.landmarkvisitlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.landmarkvisitlog.entity.LandmarkVisitLog;
import triplog.backend.landmarkvisitlog.repository.LandmarkVisitLogRepository;

/**
 * {@link LandmarkVisitLogService}의 기본 구현체입니다.
 * 랜드마크 방문 로그를 생성하고 저장합니다.
 */
@Service
@RequiredArgsConstructor
public class LandmarkVisitLogServiceImpl implements LandmarkVisitLogService {

    private final LandmarkVisitLogRepository landmarkVisitLogRepository;

    /**
     * 사용자의 랜드마크 방문 기록 존재 여부를 확인합니다.
     *
     * @param usersId   사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 방문 기록이 존재하면 true
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasVisited(String usersId, Long landmarkId) {
        return landmarkVisitLogRepository.existsByUsersIdAndLandmarkId(usersId, landmarkId);
    }

    /**
     * 랜드마크 방문 로그를 저장합니다.
     *
     * @param usersId    사용자 식별자
     * @param landmarkId 랜드마크 식별자
     */
    @Override
    @Transactional
    public void createLog(String usersId, Long landmarkId) {
        landmarkVisitLogRepository.save(new LandmarkVisitLog(usersId, landmarkId));
    }
}
