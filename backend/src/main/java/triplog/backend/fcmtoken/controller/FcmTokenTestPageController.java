package triplog.backend.fcmtoken.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * FCM 푸시 토큰 테스트 페이지 요청을 처리하는 Controller입니다.
 */
@Controller
@RequestMapping("/fcm-tokens")
@Slf4j
public class FcmTokenTestPageController {

    /**
     * FCM 토큰 발급과 백엔드 등록을 확인하는 테스트 페이지를 반환합니다.
     *
     * @return FCM 푸시 토큰 테스트 페이지 템플릿
     */
    @GetMapping("/test")
    public String testPage() {
        log.info("FCM 푸시 토큰 테스트 페이지 요청 수신");
        return "fcmtoken/fcm-token-test";
    }
}
