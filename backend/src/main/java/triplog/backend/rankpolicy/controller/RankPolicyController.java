package triplog.backend.rankpolicy.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.rankpolicy.service.RankPolicyService;

/**
 * 랭크 정책 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rank-policies")
@Tag(name = "Rank Policy API", description = "랭크 정책 API")
public class RankPolicyController {

    private final RankPolicyService rankPolicyService;
}
