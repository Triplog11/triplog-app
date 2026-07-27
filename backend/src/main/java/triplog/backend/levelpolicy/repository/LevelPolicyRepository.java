package triplog.backend.levelpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.levelpolicy.entity.LevelPolicy;

import java.util.Optional;

/**
 * 레벨 정책 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
@Repository
public interface LevelPolicyRepository extends JpaRepository<LevelPolicy, Long> {

    /**
     * 현재 레벨보다 높은 레벨 중 가장 가까운 다음 레벨 정책을 조회합니다.
     *
     * @param levelPolicyNumber 사용자의 현재 레벨
     * @return 다음 레벨 정책, 최고 레벨이면 빈 값
     */
    Optional<LevelPolicy> findFirstByLevelPolicyNumberGreaterThanOrderByLevelPolicyNumberAsc(
            int levelPolicyNumber
    );
}
