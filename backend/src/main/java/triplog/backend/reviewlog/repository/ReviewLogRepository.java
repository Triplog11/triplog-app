package triplog.backend.reviewlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.reviewlog.entity.ReviewLog;

/**
 * ReviewLog 영속성 처리를 담당하는 Repository입니다.
 */
public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {
}
