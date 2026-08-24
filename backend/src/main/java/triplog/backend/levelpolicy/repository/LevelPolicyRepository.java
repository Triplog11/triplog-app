package triplog.backend.levelpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.levelpolicy.entity.LevelPolicy;

import java.util.List;

/**
 * 레벨 정책 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
@Repository
public interface LevelPolicyRepository extends JpaRepository<LevelPolicy, Long> {

    /**
     * 모든 레벨 정책을 레벨 번호 오름차순으로 조회합니다.
     *
     * @return 레벨 번호순 정책 목록
     */
    List<LevelPolicy> findAllByOrderByLevelPolicyNumberAsc();

}
