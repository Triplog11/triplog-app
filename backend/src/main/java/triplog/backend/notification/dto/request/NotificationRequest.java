package triplog.backend.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 관련 요청 DTO를 그룹화하는 클래스입니다.
 */
@Schema(description = "알림 관련 요청 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationRequest {

    /**
     * 알림 정책별 활성화 상태를 수정하는 요청 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "알림 설정 수정 요청")
    public static class SettingsUpdateRequest {

        @NotNull(message = "레벨 알림 여부는 필수입니다.")
        @Schema(description = "레벨 알림 여부", example = "true")
        private Boolean isLevelUp;

        @NotNull(message = "랭킹 알림 여부는 필수입니다.")
        @Schema(description = "랭킹 알림 여부", example = "true")
        private Boolean isRankUp;

        @NotNull(message = "배지 획득 알림 여부는 필수입니다.")
        @Schema(description = "배지 획득 알림 여부", example = "false")
        private Boolean isBadgeAcquired;

        @NotNull(message = "카드 획득 알림 여부는 필수입니다.")
        @Schema(description = "카드 획득 알림 여부", example = "true")
        private Boolean isCardAcquired;

        @NotNull(message = "지역 방문 완료 알림 여부는 필수입니다.")
        @Schema(description = "지역 방문 완료 알림 여부", example = "true")
        private Boolean isRegionCompleted;

        @NotNull(message = "랜드마크 방문 인증 알림 여부는 필수입니다.")
        @Schema(description = "랜드마크 방문 인증 알림 여부", example = "false")
        private Boolean isLandmarkVerified;

        @NotNull(message = "주간 미션 완료 알림 여부는 필수입니다.")
        @Schema(description = "주간 미션 완료 알림 여부", example = "true")
        private Boolean isWeeklyMissonCompleted;
    }
}
