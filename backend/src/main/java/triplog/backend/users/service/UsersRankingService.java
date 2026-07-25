package triplog.backend.users.service;

/**
 * 랭킹 구성에 필요한 사용자 요약 정보 조회 기능을 정의합니다.
 */
public interface UsersRankingService {

    /**
     * 사용자 ID로 랭킹 응답에 필요한 사용자 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 랭킹 응답에 필요한 사용자 요약 정보
     */
    UsersRankingInfo getRankingInfo(String usersId);
}
