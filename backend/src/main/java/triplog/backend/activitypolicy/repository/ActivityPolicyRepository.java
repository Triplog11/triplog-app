package triplog.backend.activitypolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.activitypolicy.entity.ActivityPolicy;

/**
 * 활동 정책의 저장과 조회를 담당하는 Repository입니다.
 */
public interface ActivityPolicyRepository extends JpaRepository<ActivityPolicy, String> {
}
