package triplog.backend.regionvisitlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.regionvisitlog.entity.RegionVisitLog;

/**
 * RegionVisitLog 영속성 처리를 담당하는 Repository입니다.
 */
public interface RegionVisitLogRepository extends JpaRepository<RegionVisitLog, Long> {
}
