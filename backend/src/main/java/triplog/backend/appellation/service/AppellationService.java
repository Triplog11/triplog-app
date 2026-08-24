package triplog.backend.appellation.service;

import triplog.backend.achievement.service.AchievementContext;
import triplog.backend.appellation.dto.response.AppellationResponse.RepresentativeResponse;
import triplog.backend.appellation.dto.response.AppellationResponse.AcquiredListResponse;

import java.util.List;
import java.util.Optional;

/**
 * 칭호 조건 판정과 최초 획득 기능을 정의합니다.
 */
public interface AppellationService {

    /**
     * 로그인 사용자의 대표 칭호를 조회합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @return 대표 칭호, 지정하지 않았으면 빈 값
     */
    Optional<RepresentativeAppellationInfo> getRepresentativeAppellation(
            String usersId
    );

    /**
     * 로그인 사용자가 획득한 칭호를 모두 조회합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @return 획득 칭호 목록
     */
    AcquiredListResponse getAcquiredAppellations(String usersId);

    /**
     * 현재 활동 지표로 미획득 칭호를 판정하고 최초 획득 상태를 저장합니다.
     */
    List<AcquiredAppellationInfo> acquireEligibleAppellations(
            String usersId,
            AchievementContext context
    );

    /**
     * 사용자가 획득한 칭호 중 하나를 대표 칭호로 지정합니다.
     *
     * @param usersId 로그인 사용자 식별자
     * @param appellationId 대표로 지정할 칭호 식별자
     * @return 지정된 대표 칭호 정보
     */
    RepresentativeResponse changeRepresentativeAppellation(
            String usersId,
            Long appellationId
    );
}
