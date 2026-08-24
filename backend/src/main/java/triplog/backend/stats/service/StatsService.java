package triplog.backend.stats.service;

import triplog.backend.stats.dto.response.StatsResponse.MyRankingResponse;
import triplog.backend.stats.dto.response.StatsResponse.MyStatsResponse;
import triplog.backend.stats.dto.response.StatsResponse.RankingListResponse;
import triplog.backend.users.entity.Users;

import java.util.List;

/**
 * 사용자 통계(Stats)와 관련된 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * 사용자 통계 정보 생성, 조회, 수정 및 점수 관리 등
 * Stats 도메인의 비즈니스 기능을 선언합니다.
 */
public interface StatsService {

    /**
     * 모든 사용자의 월간 Score만 0으로 초기화합니다.
     *
     * @return 초기화된 사용자 수
     */
    int resetMonthlyScores();

    /**
     * 로그인 사용자의 전체 및 월간 랭킹 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 내 랭킹 정보
     */
    MyRankingResponse getMyRanking(String usersId);

    /**
     * 로그인 성공 응답에 포함할 사용자의 통계 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 사용자의 레벨, 경험치, 티어 정보
     */
    StatsLoginInfo getLoginStats(String usersId);

    /**
     * 신규 사용자의 초기 통계와 회원가입 보상을 생성합니다.
     *
     * @param users 생성 대상 사용자
     * @param addressSi 시 주소
     * @param addressDoGun 도·군 주소
     * @param addressGu 구 주소
     * @return 생성된 초기 통계 정보
     */
    StatsLoginInfo createInitialStats(
            Users users, String addressSi, String addressDoGun, String addressGu
    );

    /**
     * 사용자 주소 프로필 정보를 수정하고 수정 후 주소 요약 정보를 조회합니다.
     *
     * @param usersId 수정할 사용자 ID
     * @param addressSi 변경할 시
     * @param addressDoGun 변경할 도/군
     * @param addressGu 변경할 구
     * @return 수정 후 주소 프로필 요약 정보
     */
    StatsProfileInfo updateProfileAddress(String usersId, String addressSi, String addressDoGun, String addressGu);

    /**
     * 전체 랭킹을 페이지 단위로 조회합니다.
     *
     * @param rankingType 랭킹 타입 (TOTAL, MONTHLY)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 랭킹 목록 응답
     */
    RankingListResponse getRankings(String rankingType, int page, int size);

    /**
     * 로그인 사용자의 스탯 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 내 스탯 정보
     */
    MyStatsResponse getMyStats(String usersId);

    /**
     * 사용자에게 XP와 Score를 지급합니다.
     *
     * @param usersId 사용자 ID
     * @param xp      추가할 경험치
     * @param score   추가할 점수
     * @return 지급 후 레벨·랭크 성장 결과
     */
    GrowthUpdateResult addXpAndScore(String usersId, int xp, int score);

    /**
     * 지정한 활동 정책을 조회하여 XP와 Score를 지급하고 성장 정보를 갱신합니다.
     *
     * @param usersId  사용자 식별자
     * @param grants 중복 방지 키와 원본 정보를 포함한 지급 요청 목록
     * @return 정책별 보상과 지급 후 성장 정보
     */
    ActivityRewardResult applyActivityPolicies(
            String usersId,
            List<ActivityRewardGrant> grants
    );

    /**
     * 무효 처리된 원본 활동에 연결된 미회수 XP와 Score를 회수합니다.
     *
     * @param usersId 사용자 식별자
     * @param sourceType 원본 활동 유형
     * @param sourceId 원본 활동 식별자
     * @param reason 회수 사유
     * @return 보상 회수 결과
     */
    RewardRevocationResult revokeRewards(
            String usersId,
            String sourceType,
            String sourceId,
            String reason
    );
}
