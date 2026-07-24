package triplog.backend.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.notification.service.NotificationService;

/**
 * 알림 관련 API 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(name = "Notification API", description = "알림 API")
public class NotificationController {

    /**
     * 알림 비즈니스 로직을 처리하는 서비스입니다.
     */
    private final NotificationService notificationService;
}
