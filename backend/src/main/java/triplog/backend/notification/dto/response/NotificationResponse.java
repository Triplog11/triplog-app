package triplog.backend.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import triplog.backend.notification.entity.Notification;
import triplog.backend.notification.entity.NotificationPolicy;
import triplog.backend.notification.dto.request.NotificationRequest.SettingsUpdateRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 알림 관련 응답 DTO를 그룹화하는 클래스입니다.
 */
@Schema(description = "알림 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

    /**
     * 알림 정책의 활성화 상태를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "알림 설정 조회 응답")
    public static class SettingsResponse {

        @Schema(description = "레벨 알림 여부", example = "true")
        private Boolean isLevelUp;

        @Schema(description = "랭킹 알림 여부", example = "true")
        private Boolean isRankUp;

        @Schema(description = "배지 획득 알림 여부", example = "false")
        private Boolean isBadgeAcquired;

        @Schema(description = "카드 획득 알림 여부", example = "true")
        private Boolean isCardAcquired;

        @Schema(description = "지역 방문 완료 알림 여부", example = "true")
        private Boolean isRegionCompleted;

        @Schema(description = "랜드마크 방문 인증 알림 여부", example = "false")
        private Boolean isLandmarkVerified;

        @Schema(description = "주간 미션 완료 알림 여부", example = "true")
        private Boolean isWeeklyMissonCompleted;

        /**
         * 알림 정책 목록의 활성화 상태를 알림 설정 응답으로 변환합니다.
         *
         * @param policies 알림 정책 목록
         * @return 알림 설정 조회 응답
         */
        public static SettingsResponse toDto(List<NotificationPolicy> policies) {
            Map<String, NotificationPolicy> policyMap = policies.stream()
                    .collect(Collectors.toMap(
                            NotificationPolicy::getNotificationType,
                            Function.identity()
                    ));
            return new SettingsResponse(
                    policyMap.get("LEVEL_UP").isActive(),
                    policyMap.get("RANK_UP").isActive(),
                    policyMap.get("BADGE_ACQUIRED").isActive(),
                    policyMap.get("CARD_ACQUIRED").isActive(),
                    policyMap.get("REGION_CONQUERED").isActive(),
                    policyMap.get("VISIT_VERIFIED").isActive(),
                    policyMap.get("WEEKLY_MISSION_COMPLETED").isActive()
            );
        }

        /**
         * 알림 설정 수정 요청을 수정 결과 응답으로 변환합니다.
         *
         * @param request 알림 설정 수정 요청
         * @return 알림 설정 수정 응답
         */
        public static SettingsResponse toDto(SettingsUpdateRequest request) {
            return new SettingsResponse(
                    request.getIsLevelUp(),
                    request.getIsRankUp(),
                    request.getIsBadgeAcquired(),
                    request.getIsCardAcquired(),
                    request.getIsRegionCompleted(),
                    request.getIsLandmarkVerified(),
                    request.getIsWeeklyMissonCompleted()
            );
        }
    }

    /**
     * 페이징된 알림 목록을 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "알림 목록 조회 응답")
    public static class ListResponse {

        @Schema(description = "현재 페이지 번호", example = "0")
        private int page;

        @Schema(description = "페이지 크기", example = "10")
        private int size;

        @Schema(description = "전체 알림 수", example = "25")
        private long totalElements;

        @Schema(description = "전체 페이지 수", example = "3")
        private int totalPages;

        @Schema(description = "알림 목록")
        private List<NotificationItem> notifications;

        /**
         * 페이징된 알림 엔티티 조회 결과를 목록 응답 DTO로 변환합니다.
         *
         * @param result 페이징된 알림 조회 결과
         * @return 알림 목록 조회 응답
         */
        public static ListResponse toDto(Page<Notification> result) {
            List<NotificationItem> notifications = result.getContent().stream()
                    .map(NotificationItem::toDto)
                    .toList();
            return new ListResponse(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    notifications
            );
        }
    }

    /**
     * 알림 목록의 개별 알림 정보를 반환하는 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "알림 목록 항목")
    public static class NotificationItem {

        @Schema(description = "알림 ID", example = "101")
        private Long notificationId;

        @Schema(description = "알림 제목", example = "미션 완료")
        private String title;

        @Schema(description = "알림 내용", example = "일일 미션을 완료했습니다.")
        private String content;

        @Schema(description = "알림 타입", example = "MISSION")
        private String notificationType;

        @Schema(description = "읽음 여부", example = "false")
        private Boolean isRead;

        @Schema(description = "생성 일시", example = "2026-06-25T10:30:00")
        private LocalDateTime createdAt;

        /**
         * 알림 엔티티를 목록 항목 DTO로 변환합니다.
         *
         * @param notification 변환할 알림 엔티티
         * @return 알림 목록 항목
         */
        public static NotificationItem toDto(Notification notification) {
            return new NotificationItem(
                    notification.getNotificationId(),
                    notification.getNotificationTitle(),
                    notification.getNotificationContent(),
                    notification.getNotificationType(),
                    notification.isRead(),
                    notification.getNotificationCreatedAt()
            );
        }
    }

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
