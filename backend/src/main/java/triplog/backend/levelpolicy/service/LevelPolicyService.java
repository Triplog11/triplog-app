package triplog.backend.levelpolicy.service;

import java.util.Optional;

/**
 * 레벨 정책 관련 비즈니스 기능을 정의하는 서비스 인터페이스입니다.
 */
public interface LevelPolicyService {

    /**
     * 현재 레벨보다 높은 조건 중 가장 가까운 다음 레벨 정책을 조회합니다.
     *
     * @param currentLevel 사용자의 현재 레벨
     * @return 다음 레벨 정책 요약 정보, 최고 레벨이면 빈 값
     */
    Optional<LevelPolicyInfo> findNextLevelPolicy(int currentLevel);
}
