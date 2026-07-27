package triplog.backend.levelpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.levelpolicy.repository.LevelPolicyRepository;

import java.util.Optional;

/**
 * 레벨 정책 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LevelPolicyServiceImpl implements LevelPolicyService {

    private final LevelPolicyRepository levelPolicyRepository;

    /**
     * 현재 레벨보다 높은 조건 중 가장 가까운 다음 레벨 정책을 조회합니다.
     *
     * @param currentLevel 사용자의 현재 레벨
     * @return 다음 레벨 정책 요약 정보, 최고 레벨이면 빈 값
     */
    @Override
    public Optional<LevelPolicyInfo> findNextLevelPolicy(int currentLevel) {
        return levelPolicyRepository
                .findFirstByLevelPolicyNumberGreaterThanOrderByLevelPolicyNumberAsc(currentLevel)
                .map(levelPolicy -> new LevelPolicyInfo(
                        levelPolicy.getLevelPolicyNumber(),
                        levelPolicy.getLevelPolicyCondition()
                ));
    }
}
