package triplog.backend.landmarkvisitlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.landmarkvisitlog.entity.LandmarkVisitLog;
import triplog.backend.landmarkvisitlog.repository.LandmarkVisitLogRepository;

import java.time.LocalDateTime;

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

    @Override
    @Transactional(readOnly = true)
    public long countDistinctVisitDates(String usersId, Long landmarkId) {
        return landmarkVisitLogRepository.countDistinctVisitDates(usersId, landmarkId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countWeekendVisits(String usersId) {
        return landmarkVisitLogRepository.countWeekendVisits(usersId);
    }

    /**
     * 지정 기간의 랜드마크 방문 횟수를 방문 유형에 따라 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start 집계 시작 시각
     * @param end 집계 종료 시각
     * @param visitType 방문 유형
     * @return 조건에 맞는 방문 횟수
     */
    @Override
    @Transactional(readOnly = true)
    public long countVisits(
            String usersId, LocalDateTime start, LocalDateTime end, String visitType
    ) {
        return landmarkVisitLogRepository.countVisits(usersId, start, end, visitType);
    }
}
