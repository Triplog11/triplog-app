package triplog.backend.activitypolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.activitypolicy.entity.ActivityPolicy;
import triplog.backend.activitypolicy.repository.ActivityPolicyRepository;

import java.util.List;
import java.util.Optional;

/**
 * 활동 정책 조회를 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityPolicyServiceImpl implements ActivityPolicyService {

    private final ActivityPolicyRepository activityPolicyRepository;

    /**
     * 활동 정책 식별자로 정책을 조회합니다.
     *
     * @param policyId 활동 정책 식별자
     * @return 조회된 활동 정책
     */
    @Override
    public Optional<ActivityPolicy> findById(String policyId) {
        return activityPolicyRepository.findById(policyId);
    }

    /**
     * 여러 활동 정책 식별자에 해당하는 정책을 조회합니다.
     *
     * @param policyIds 활동 정책 식별자 목록
     * @return 조회된 활동 정책 목록
     */
    @Override
    public List<ActivityPolicy> findAllByIds(List<String> policyIds) {
        return activityPolicyRepository.findAllById(policyIds);
    }
}
