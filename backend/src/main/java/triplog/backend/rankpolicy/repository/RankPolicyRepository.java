package triplog.backend.rankpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.rankpolicy.entity.RankPolicy;

/**
 * 랭크 정책 엔티티의 저장과 조회를 담당하는 Repository입니다.
 */
@Repository
public interface RankPolicyRepository extends JpaRepository<RankPolicy, Long> {
}
