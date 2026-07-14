package triplog.backend.badge.service;

import org.springframework.data.domain.Pageable;
import triplog.backend.badge.dto.response.BadgeListResult;
import triplog.backend.badge.dto.response.BadgeResponse;

/**
 * 배지 목록 및 상세 조회 기능의 계약을 정의합니다.
 */
public interface BadgeService {

    /**
     * 로그인 사용자의 획득 상태를 포함한 배지 목록을 조회합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param badgeType 필터링할 배지 타입
     * @param isAcquired 필터링할 획득 여부
     * @param pageable 페이지 정보
     * @return 조회 조건에 따른 전체 또는 획득 배지 목록 응답
     */
    BadgeListResult getBadges(String usersId, String badgeType, Boolean isAcquired, Pageable pageable);

    /**
     * 로그인 사용자의 획득 상태를 포함한 배지 상세 정보를 조회합니다.
     *
     * @param usersId 로그인 사용자 ID
     * @param badgeId 조회할 배지 ID
     * @return 배지 상세 응답
     * @throws triplog.backend.badge.exception.BadgeException 배지가 존재하지 않는 경우
     */
    BadgeResponse.BadgeDetailResponse getBadgeDetail(String usersId, Long badgeId);
}
