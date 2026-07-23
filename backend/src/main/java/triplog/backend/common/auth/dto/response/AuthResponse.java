package triplog.backend.common.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "인증 관련 응답 DTO 그룹")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {

    /**
     * 로그인 API 응답 공통 타입입니다.
     */
    public interface LoginResponse {
    }

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
     * Naver 토큰 발급 API 응답 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    public static class NaverTokenResponse {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("token_type")
        private String tokenType;
    }

    /**
     * Naver 사용자 정보 API 응답 DTO입니다.
     */
    @Getter
    @NoArgsConstructor
    public static class NaverUserInfoResponse {

        private String resultcode;

        private String message;

        private Response response;

        @Getter
        @NoArgsConstructor
        public static class Response {

            private String email;
        }
    }

    /**
     * 기존 회원 로그인 성공 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "로그인 성공 응답")
    public static class LoginSuccessResponse implements LoginResponse {

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

        public static LoginSuccessResponse toDto(
                String nickname,
                Integer level,
                Integer xp,
                String tier,
                String accessToken,
                String refreshToken
        ) {
            return new LoginSuccessResponse(nickname, level, xp, tier, accessToken, refreshToken);
        }
    }

    /**
     * 추가 정보 입력이 필요한 사용자에게 반환하는 임시 토큰 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "추가 정보 입력용 임시 토큰 응답")
    public static class TemporaryTokenResponse implements LoginResponse {

        @Schema(description = "토큰 만료시간", example = "300")
        private Long expiresIn;

        @Schema(description = "임시 토큰", example = "temporary-token")
        private String temporaryToken;

        public static TemporaryTokenResponse toDto(Long expiresIn, String temporaryToken) {
            return new TemporaryTokenResponse(expiresIn, temporaryToken);
        }
    }

    /**
     * 로컬 회원가입 완료 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "로컬 회원가입 완료 응답")
    public static class SignupResponse {

        @Schema(description = "회원가입 완료 여부", example = "true")
        private Boolean isRegister;

        /**
         * 회원가입 완료 여부를 기반으로 응답 DTO를 생성합니다.
         *
         * @param isRegister 회원가입 완료 여부
         * @return 로컬 회원가입 완료 응답 DTO
         */
        public static SignupResponse toDto(Boolean isRegister) {
            return new SignupResponse(isRegister);
        }
    }

    /**
     * 로그아웃 완료 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "로그아웃 완료 응답")
    public static class LogoutResponse {

        @Schema(description = "로그아웃 처리 여부", example = "true")
        private Boolean isLogOut;

        /**
         * 로그아웃 처리 여부를 기반으로 응답 DTO를 생성합니다.
         *
         * @param isLogOut 로그아웃 처리 여부
         * @return 로그아웃 완료 응답 DTO
         */
        public static LogoutResponse toDto(Boolean isLogOut) {
            return new LogoutResponse(isLogOut);
        }
    }

    /**
     * Access Token과 Refresh Token 재발급 응답 DTO입니다.
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "토큰 재발급 응답")
    public static class TokenReissueResponse {

        @Schema(description = "새로 발급된 서비스 접근 토큰", example = "new-access-token")
        private String accessToken;

        @Schema(description = "새로 발급된 서비스 갱신 토큰", example = "new-refresh-token")
        private String refreshToken;

        public static TokenReissueResponse toDto(
                String accessToken,
                String refreshToken
        ) {
            return new TokenReissueResponse(accessToken, refreshToken);
        }
    }
}
