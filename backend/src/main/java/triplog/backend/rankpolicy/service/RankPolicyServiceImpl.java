package triplog.backend.rankpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.rankpolicy.repository.RankPolicyRepository;

/**
 * 랭크 정책 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankPolicyServiceImpl implements RankPolicyService {

    private final RankPolicyRepository rankPolicyRepository;
}
