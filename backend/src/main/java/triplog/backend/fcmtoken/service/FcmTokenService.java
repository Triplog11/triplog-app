package triplog.backend.fcmtoken.service;

import triplog.backend.fcmtoken.dto.response.FcmTokenResponse.RegisterResponse;
import triplog.backend.fcmtoken.dto.response.FcmTokenResponse.DeleteResponse;

/**
 * FCM 푸시 토큰 도메인의 비즈니스 로직을 정의하는 Service 인터페이스입니다.
 * <p>
 * FCM 토큰 등록, 조회, 수정, 삭제와 관련된 기능을 선언합니다.
 */
public interface FcmTokenService {

    /**
     * 로그인한 사용자의 FCM 푸시 토큰과 디바이스 정보를 등록합니다.
     *
     * @param usersId 토큰을 등록할 사용자 ID
     * @param token FCM에서 발급한 디바이스 토큰
     * @param deviceType 디바이스 운영체제 유형
     * @param deviceName 디바이스 이름
     * @return FCM 푸시 토큰 등록 결과
     */
    RegisterResponse register(String usersId, String token, String deviceType, String deviceName);

    /**
     * 로그인한 사용자의 FCM 푸시 토큰을 삭제합니다.
     *
     * @param usersId 토큰을 삭제할 사용자 ID
     * @param token 삭제할 FCM 토큰
     * @return FCM 푸시 토큰 삭제 결과
     */
    DeleteResponse delete(String usersId, String token);
}
