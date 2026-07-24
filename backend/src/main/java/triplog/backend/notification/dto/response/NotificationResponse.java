package triplog.backend.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 알림 관련 응답 DTO를 그룹화하는 클래스입니다.
 */
@Schema(description = "알림 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

    /**
     * 알림 읽음 처리 결과를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "알림 읽음 처리 응답")
    public static class ReadResponse {

        @Schema(description = "읽음 처리된 알림 식별자", example = "101")
        private Long notificationId;

        @Schema(description = "알림 읽음 여부", example = "true")
        private Boolean isRead;

        @Schema(description = "알림을 읽은 일시", example = "2026-06-25T10:40:00")
        private LocalDateTime readAt;

        /**
         * 읽음 처리된 알림 정보로 응답 DTO를 생성합니다.
         *
         * @param notificationId 읽음 처리된 알림 식별자
         * @param readAt 알림을 읽은 일시
         * @return 알림 읽음 처리 응답 DTO
         */
        public static ReadResponse toDto(Long notificationId, LocalDateTime readAt) {
            return new ReadResponse(notificationId, true, readAt);
        }
    }
}
