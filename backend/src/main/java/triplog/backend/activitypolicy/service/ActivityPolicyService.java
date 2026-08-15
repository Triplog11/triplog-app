package triplog.backend.activitypolicy.service;

import triplog.backend.activitypolicy.entity.ActivityPolicy;

import java.util.List;
import java.util.Optional;

/**
 * 활동 정책 조회 기능을 정의하는 서비스 인터페이스입니다.
 */
public interface ActivityPolicyService {

    /**
     * 활동 정책 식별자로 정책을 조회합니다.
     *
     * @param policyId 활동 정책 식별자
     * @return 조회된 활동 정책
     */
    Optional<ActivityPolicy> findById(String policyId);

    /**
     * 여러 활동 정책 식별자에 해당하는 정책을 조회합니다.
     *
     * @param policyIds 활동 정책 식별자 목록
     * @return 조회된 활동 정책 목록
     */
    List<ActivityPolicy> findAllByIds(List<String> policyIds);
}
