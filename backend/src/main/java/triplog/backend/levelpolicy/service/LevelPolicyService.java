package triplog.backend.levelpolicy.service;

import java.util.Optional;

/**
 * 레벨 정책 관련 비즈니스 기능을 정의하는 서비스 인터페이스입니다.
 */
public interface LevelPolicyService {

    /**
     * 현재 레벨에서 다음 레벨로 가기 위한 누적 경험치 기준을 계산합니다.
     *
     * @param currentLevel 사용자의 현재 레벨
     * @return 다음 레벨과 누적 경험치 기준
     */
    Optional<LevelPolicyInfo> findNextLevelPolicy(int currentLevel);

    /**
     * 누적 XP에 해당하는 현재 레벨을 계산합니다.
     *
     * @param cumulativeXp 누적 XP
     * @return 현재 레벨
     */
    int calculateLevel(int cumulativeXp);
}
