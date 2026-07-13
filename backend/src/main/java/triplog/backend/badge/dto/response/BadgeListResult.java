package triplog.backend.badge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 조회 조건에 따라 반환되는 배지 목록 응답의 공통 타입입니다.
 */
@Schema(description = "배지 목록 응답 공통 타입", oneOf = {
        BadgeResponse.BadgeListResponse.class,
        BadgeResponse.BadgeListAcquiredResponse.class
})
public sealed interface BadgeListResult
        permits BadgeResponse.BadgeListResponse, BadgeResponse.BadgeListAcquiredResponse {
}
