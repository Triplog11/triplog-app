package triplog.backend.regionvisitlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.regionvisitlog.entity.RegionVisitLog;
import triplog.backend.regionvisitlog.repository.RegionVisitLogRepository;

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
}
