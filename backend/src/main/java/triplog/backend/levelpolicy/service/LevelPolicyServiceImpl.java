package triplog.backend.levelpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.levelpolicy.repository.LevelPolicyRepository;
import triplog.backend.levelpolicy.entity.LevelPolicy;
import triplog.backend.levelpolicy.exception.LevelPolicyErrorCode;
import triplog.backend.levelpolicy.exception.LevelPolicyException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
     * 현재 레벨까지의 구간 XP를 합산하여 다음 레벨의 누적 XP 기준을 계산합니다.
     * DB에 등록된 마지막 레벨 이후에는 마지막 행의 구간 XP를 계속 적용합니다.
     *
     * @param currentLevel 사용자의 현재 레벨
     * @return 다음 레벨 정책 요약 정보, 최고 레벨이면 빈 값
     */
    @Override
    public Optional<LevelPolicyInfo> findNextLevelPolicy(int currentLevel) {
        PolicyTable policyTable = loadPolicyTable();
        int normalizedLevel = Math.max(currentLevel, 1);
        int requiredXp = Math.toIntExact(
                policyTable.cumulativeRequirementThrough(normalizedLevel)
        );
        return Optional.of(new LevelPolicyInfo(normalizedLevel + 1, requiredXp));
    }

    /**
     * 누적 XP를 레벨별 필요 XP와 비교하여 현재 레벨을 계산합니다.
     *
     * @param cumulativeXp 누적 XP
     * @return 계산된 현재 레벨
     */
    @Override
    public int calculateLevel(int cumulativeXp) {
        PolicyTable policyTable = loadPolicyTable();
        long normalizedXp = Math.max(cumulativeXp, 0);
        long configuredRequirement = policyTable.configuredCumulativeRequirement();

        if (normalizedXp >= configuredRequirement) {
            long additionalLevels =
                    (normalizedXp - configuredRequirement) / policyTable.extensionRequirement();
            return Math.toIntExact(
                    (long) policyTable.maximumConfiguredLevel() + 1 + additionalLevels
            );
        }

        long accumulatedRequirement = 0;
        for (int level = 1; level <= policyTable.maximumConfiguredLevel(); level++) {
            accumulatedRequirement += policyTable.requirementAt(level);
            if (normalizedXp < accumulatedRequirement) {
                return level;
            }
        }
        throw new LevelPolicyException(LevelPolicyErrorCode.INVALID_LEVEL_POLICY);
    }

    /**
     * DB 정책을 연속된 레벨 구간표로 검증하고 계산용 테이블로 변환합니다.
     */
    private PolicyTable loadPolicyTable() {
        List<LevelPolicy> policies =
                levelPolicyRepository.findAllByOrderByLevelPolicyNumberAsc();
        if (policies.isEmpty()) {
            throw new LevelPolicyException(LevelPolicyErrorCode.LEVEL_POLICY_NOT_FOUND);
        }

        Map<Integer, Integer> requirements = new LinkedHashMap<>();
        int expectedLevel = 1;
        for (LevelPolicy policy : policies) {
            if (policy.getLevelPolicyNumber() != expectedLevel
                    || policy.getLevelPolicyCondition() <= 0) {
                throw new LevelPolicyException(LevelPolicyErrorCode.INVALID_LEVEL_POLICY);
            }
            requirements.put(
                    policy.getLevelPolicyNumber(),
                    policy.getLevelPolicyCondition()
            );
            expectedLevel++;
        }
        int maximumLevel = policies.getLast().getLevelPolicyNumber();
        int extensionRequirement = policies.getLast().getLevelPolicyCondition();
        return new PolicyTable(requirements, maximumLevel, extensionRequirement);
    }

    /**
     * 레벨별 구간 XP와 마지막 구간의 확장 규칙을 보관합니다.
     */
    private record PolicyTable(
            Map<Integer, Integer> requirements,
            int maximumConfiguredLevel,
            int extensionRequirement
    ) {

        /** 지정 레벨 구간의 필요 XP를 반환합니다. */
        private int requirementAt(int level) {
            return requirements.getOrDefault(level, extensionRequirement);
        }

        /** DB에 등록된 전체 레벨 구간의 누적 필요 XP를 반환합니다. */
        private long configuredCumulativeRequirement() {
            return requirements.values().stream()
                    .mapToLong(Integer::longValue)
                    .sum();
        }

        /** 지정 레벨까지의 누적 필요 XP를 확장 정책까지 반영해 계산합니다. */
        private long cumulativeRequirementThrough(int level) {
            long configured = requirements.entrySet().stream()
                    .filter(entry -> entry.getKey() <= level)
                    .mapToLong(entry -> entry.getValue().longValue())
                    .sum();
            if (level <= maximumConfiguredLevel) {
                return configured;
            }
            return configuredCumulativeRequirement()
                    + (long) (level - maximumConfiguredLevel) * extensionRequirement;
        }
    }
}
