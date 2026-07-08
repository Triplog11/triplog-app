package triplog.backend.common.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Google OAuth API와 통신하는 Client입니다.
 * <p>
 * 프론트엔드가 Google 로그인 완료 후 전달받은 인가 코드(Authorization Code)를
 * 백엔드에 넘기면, 이 Client가 해당 코드를 Google 인증 서버로 전달하여
 * 로그인 처리에 필요한 토큰을 발급받습니다.
 * <p>
 * 현재 소셜 로그인 단계에서는 사용자의 이메일만 로그인 식별 정보로 사용합니다.
 * 닉네임, 프로필 이미지 등 추가 정보는 별도 추가 정보 API에서 처리합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleClient implements SocialApiClient {
}
