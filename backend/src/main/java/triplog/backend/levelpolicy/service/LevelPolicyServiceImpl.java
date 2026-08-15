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

    /**
     * 누적 XP를 레벨별 필요 XP와 비교하여 현재 레벨을 계산합니다.
     *
     * @param cumulativeXp 누적 XP
     * @return 계산된 현재 레벨
     */
    @Override
    public int calculateLevel(int cumulativeXp) {
        int level = 1;
        int accumulatedRequirement = 0;
        var policies = levelPolicyRepository.findAllByOrderByLevelPolicyNumberAsc();

        while (true) {
            int currentLevel = level;
            int requiredXp = policies.stream()
                    .filter(policy -> policy.getLevelPolicyNumber() == currentLevel)
                    .findFirst()
                    .map(policy -> policy.getLevelPolicyCondition())
                    .orElse(700);
            if (cumulativeXp < accumulatedRequirement + requiredXp) {
                return level;
            }
            accumulatedRequirement += requiredXp;
            level++;
        }
    }
}
