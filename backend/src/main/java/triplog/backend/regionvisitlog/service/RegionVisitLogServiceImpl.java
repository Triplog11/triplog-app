package triplog.backend.regionvisitlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.regionvisitlog.entity.RegionVisitLog;
import triplog.backend.regionvisitlog.repository.RegionVisitLogRepository;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

/**
 * {@link RegionVisitLogService}의 기본 구현체입니다.
 * 지역 방문 로그를 생성하고 저장합니다.
 */
@Service
@RequiredArgsConstructor
public class RegionVisitLogServiceImpl implements RegionVisitLogService {

    private final RegionVisitLogRepository regionVisitLogRepository;

    /**
     * 지역 방문 로그를 저장합니다.
     *
     * @param usersId  사용자 식별자
     * @param regionId 지역 식별자
     */
    @Override
    @Transactional
    public void createLog(String usersId, Long regionId) {
        regionVisitLogRepository.save(new RegionVisitLog(usersId, regionId));
    }

    @Override
    @Transactional(readOnly = true)
    public int countConsecutiveNewRegionVisits(String usersId) {
        Set<Long> visitedRegionIds = new HashSet<>();
        int currentStreak = 0;
        for (RegionVisitLog visitLog : regionVisitLogRepository
                .findByUsersIdOrderByVisitedAtAscRegionVisitLogIdAsc(usersId)) {
            if (visitedRegionIds.add(visitLog.getRegionId())) {
                currentStreak++;
            } else {
                currentStreak = 0;
            }
        }
        return currentStreak;
    }

    /**
     * 지정 기간에 사용자가 처음 방문한 지역 수를 집계합니다.
     *
     * @param usersId 사용자 식별자
     * @param start 집계 시작 시각
     * @param end 집계 종료 시각
     * @return 처음 방문한 지역 수
     */
    @Override
    @Transactional(readOnly = true)
    public long countFirstVisits(String usersId, LocalDateTime start, LocalDateTime end) {
        return regionVisitLogRepository.countFirstVisits(usersId, start, end);
    }
}
