package triplog.backend.stats.service;

/**
 * 사용자 통계(Stats)와 관련된 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * 사용자 통계 정보 생성, 조회, 수정 및 점수 관리 등
 * Stats 도메인의 비즈니스 기능을 선언합니다.
 */
public interface StatsService {

    /**
     * 로그인 성공 응답에 포함할 사용자의 통계 정보를 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 사용자의 레벨, 경험치, 티어 정보
     */
    StatsLoginInfo getLoginStats(String usersId);

    /**
     * 신규 사용자의 초기 통계 정보를 생성합니다.
     *
     * @param usersId 통계를 생성할 사용자 ID
     * @param addressSi 시
     * @param addressDoGun 도/군
     * @param addressGu 구
     * @return 생성된 사용자의 초기 레벨, 경험치, 티어 정보
     */
    StatsLoginInfo createInitialStats(String usersId, String addressSi, String addressDoGun, String addressGu);
}
