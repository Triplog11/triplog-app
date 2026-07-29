package triplog.backend.landmarkvisitlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.landmarkvisitlog.entity.LandmarkVisitLog;

/**
 * LandmarkVisitLog 영속성 처리를 담당하는 Repository입니다.
 */
public interface LandmarkVisitLogRepository extends JpaRepository<LandmarkVisitLog, Long> {
}
