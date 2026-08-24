package triplog.backend.attractionvisitlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.attractionvisitlog.entity.AttractionVisitLog;
import triplog.backend.attractionvisitlog.repository.AttractionVisitLogRepository;

import java.time.LocalDateTime;

/**
 * {@link AttractionVisitLogService}의 기본 구현체입니다.
 * 일반 관광지 방문 로그를 생성하고 조회합니다.
 */
@Service
@RequiredArgsConstructor
public class AttractionVisitLogServiceImpl implements AttractionVisitLogService {

    private final AttractionVisitLogRepository attractionVisitLogRepository;

    /**
     * 사용자의 일반 관광지 방문 기록 존재 여부를 확인합니다.
     *
     * @param usersId      사용자 식별자
     * @param attractionId 일반 관광지 식별자
     * @return 방문 기록이 존재하면 true
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasVisited(String usersId, Long attractionId) {
        return attractionVisitLogRepository.existsByUsersIdAndAttractionId(usersId, attractionId);
    }

    /**
     * 일반 관광지 방문 로그를 저장합니다.
     *
     * @param usersId      사용자 식별자
     * @param attractionId 일반 관광지 식별자
     */
    @Override
    @Transactional
    public void createLog(String usersId, Long attractionId) {
        attractionVisitLogRepository.save(new AttractionVisitLog(usersId, attractionId));
    }

    /**
     * 방문한 일수를 반환합니다.
     * @param usersId 사용자 식별자
     * @param attractionId 일반 관광지 식별자
     * @return 방문한 일수
     */
    @Override
    @Transactional(readOnly = true)
    public long countDistinctVisitDates(String usersId, Long attractionId) {
        return attractionVisitLogRepository.countDistinctVisitDates(usersId, attractionId);
    }

    /**
     * 주간 방문 횟수를 반환합니다.
     * @param usersId 사용자 식별자
     * @return 주간 방문 횟수
     */
    @Override
    @Transactional(readOnly = true)
    public long countWeekendVisits(String usersId) {
        return attractionVisitLogRepository.countWeekendVisits(usersId);
    }

    /**
     * 지정 기간의 일반 관광지 방문 횟수를 방문 유형에 따라 집계합니다.
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
        return attractionVisitLogRepository.countVisits(usersId, start, end, visitType);
    }
}
