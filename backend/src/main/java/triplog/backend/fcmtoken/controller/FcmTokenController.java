package triplog.backend.fcmtoken.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import triplog.backend.fcmtoken.service.FcmTokenService;

/**
 * FCM 푸시 토큰과 관련된 API 요청을 처리하는 Controller입니다.
 * <p>
 * 사용자 디바이스의 FCM 토큰 등록, 조회, 수정, 삭제와 관련된 HTTP 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "FCM Token API", description = "FCM 푸시 토큰 API")
@RequestMapping("/fcm-tokens")
@Slf4j
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;
}
