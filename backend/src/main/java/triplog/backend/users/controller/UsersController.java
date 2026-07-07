package triplog.backend.users.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자(User)와 관련된 API 요청을 처리하는 Controller입니다.
 * <p>
 * 사용자 정보 조회 및 수정 등 사용자 도메인과 관련된 HTTP 요청을 처리합니다.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Users API", description = "유저 API")
@RequestMapping("/users")
@Slf4j
public class UsersController {
}
