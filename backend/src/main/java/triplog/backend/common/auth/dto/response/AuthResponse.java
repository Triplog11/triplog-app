package triplog.backend.common.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증(Auth) API 응답 DTO를 그룹화하는 클래스입니다.
 * <p>
 * 로그인 성공, 회원가입 분기, 토큰 재발급 등 인증 흐름에서 클라이언트에 반환하는
 * Response DTO를 내부 정적 클래스로 정의합니다.
 */
@Schema(description = "인증 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {

    /**
     * Google 토큰 발급 API 응답 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    public static class GoogleTokenResponse {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("id_token")
        private String idToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("token_type")
        private String tokenType;
    }

    /**
     * 기존 회원 로그인 성공 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "로그인 성공 응답")
    public static class LoginSuccessResponse {

        @Schema(description = "유저 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
        private String usersId;

        @Schema(description = "닉네임", example = "여행자")
        private String nickname;

        @Schema(description = "초기 레벨", example = "1")
        private Integer level;

        @Schema(description = "초기 경험치", example = "0")
        private Integer xp;

        @Schema(description = "초기 티어", example = "BRONZE")
        private String tier;

        @Schema(description = "서비스 접근 토큰", example = "access-token")
        private String accessToken;

        @Schema(description = "서비스 갱신 토큰", example = "refresh-token")
        private String refreshToken;

        /**
         * 로그인 성공 응답 DTO를 생성합니다.
         *
         * @param usersId 유저 식별자
         * @param nickname 닉네임
         * @param level 초기 레벨
         * @param xp 초기 경험치
         * @param tier 초기 티어
         * @param accessToken 서비스 접근 토큰
         * @param refreshToken 서비스 갱신 토큰
         * @return 로그인 성공 응답 DTO
         */
        public static LoginSuccessResponse toDto(String usersId, String nickname, Integer level, Integer xp,
                                                 String tier, String accessToken, String refreshToken) {
            return new LoginSuccessResponse(usersId, nickname, level, xp, tier, accessToken, refreshToken);
        }
    }

    /**
     * 추가 정보 입력이 필요한 사용자에게 반환하는 임시 토큰 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "추가 정보 입력용 임시 토큰 응답")
    public static class TemporaryTokenResponse {

        @Schema(description = "토큰 만료시간", example = "300")
        private Long expiresIn;

        @Schema(description = "임시 토큰", example = "temporary-token")
        private String temporaryToken;

        /**
         * 임시 토큰 응답 DTO를 생성합니다.
         *
         * @param expiresIn 토큰 만료시간
         * @param temporaryToken 임시 토큰
         * @return 임시 토큰 응답 DTO
         */
        public static TemporaryTokenResponse toDto(Long expiresIn, String temporaryToken) {
            return new TemporaryTokenResponse(expiresIn, temporaryToken);
        }
    }
}
