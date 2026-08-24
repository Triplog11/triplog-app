package triplog.backend.appellation.service;

import triplog.backend.appellation.entity.UsersAppellation;

/**
 * 다른 도메인에서 사용할 대표 칭호 조회 결과입니다.
 *
 * @param appellationId 대표 칭호 식별자
 * @param appellationName 대표 칭호 이름
 */
public record RepresentativeAppellationInfo(
        Long appellationId,
        String appellationName
) {

    /**
     * 사용자 칭호 엔티티를 대표 칭호 조회 결과로 변환합니다.
     *
     * @param usersAppellation 대표 사용자 칭호
     * @return 대표 칭호 조회 결과
     */
    public static RepresentativeAppellationInfo from(
            UsersAppellation usersAppellation
    ) {
        return new RepresentativeAppellationInfo(
                usersAppellation.getAppellation().getAppellationId(),
                usersAppellation.getAppellation().getAppellationName()
        );
    }
}
