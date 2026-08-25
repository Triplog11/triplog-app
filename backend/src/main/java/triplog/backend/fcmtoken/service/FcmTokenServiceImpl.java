package triplog.backend.fcmtoken.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.fcmtoken.dto.response.FcmTokenResponse.RegisterResponse;
import triplog.backend.fcmtoken.dto.response.FcmTokenResponse.DeleteResponse;
import triplog.backend.fcmtoken.entity.FcmToken;
import triplog.backend.fcmtoken.exception.FcmTokenException;
import triplog.backend.fcmtoken.repository.FcmTokenRepository;
import triplog.backend.users.entity.Users;
import triplog.backend.users.service.UsersService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static triplog.backend.fcmtoken.exception.FcmTokenErrorCode.FCM_TOKEN_ALREADY_REGISTERED;
import static triplog.backend.fcmtoken.exception.FcmTokenErrorCode.FCM_TOKEN_NOT_FOUND;

/**
 * {@link FcmTokenService}의 구현 클래스입니다.
 * <p>
 * Repository를 통해 FCM 푸시 토큰 데이터를 조회하고 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UsersService usersService;
    private final ObjectProvider<FirebaseMessaging> firebaseMessagingProvider;

    /**
     * Firebase가 활성화된 경우 사용자의 모든 등록 토큰으로 푸시 알림을 전송합니다.
     *
     * @param usersId 수신 사용자 식별자
     * @param title 푸시 알림 제목
     * @param content 푸시 알림 내용
     * @param data 앱 화면 이동 등에 사용할 추가 데이터
     */
    @Override
    public void sendPush(
            String usersId,
            String title,
            String content,
            Map<String, Object> data
    ) {
        FirebaseMessaging firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
        if (firebaseMessaging == null) {
            return;
        }

        List<String> tokens = fcmTokenRepository.findAllByUsersUsersId(usersId).stream()
                .map(FcmToken::getToken)
                .toList();
        if (tokens.isEmpty()) {
            return;
        }

        Map<String, String> messageData = data.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> String.valueOf(entry.getValue())
                ));
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder().setTitle(title).setBody(content).build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setChannelId("triplog_default")
                                .build())
                        .build())
                .putAllData(messageData)
                .addAllTokens(tokens)
                .build();
        firebaseMessaging.sendEachForMulticastAsync(message);
    }

    /**
     * 로그인한 사용자의 FCM 푸시 토큰과 디바이스 정보를 등록합니다.
     *
     * @param usersId 토큰을 등록할 사용자 ID
     * @param token FCM에서 발급한 디바이스 토큰
     * @param deviceType 디바이스 운영체제 유형
     * @param deviceName 디바이스 이름
     * @return FCM 푸시 토큰 등록 결과
     * @throws FcmTokenException 토큰이 이미 등록된 경우
     */
    @Override
    @Transactional
    public RegisterResponse register(String usersId, String token, String deviceType, String deviceName) {

        Users users = usersService.findById(usersId);
        if (fcmTokenRepository.existsByToken(token)) {
            throw new FcmTokenException(FCM_TOKEN_ALREADY_REGISTERED);
        }
        fcmTokenRepository.save(new FcmToken(users, token, deviceType, deviceName));
        return RegisterResponse.toDto(true);
    }

    /**
     * 로그인한 사용자가 등록한 FCM 푸시 토큰을 삭제합니다.
     *
     * @param usersId 토큰을 삭제할 사용자 ID
     * @param token 삭제할 FCM 토큰
     * @return FCM 푸시 토큰 삭제 결과
     * @throws FcmTokenException 사용자에게 등록된 토큰을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public DeleteResponse delete(String usersId, String token) {
        FcmToken fcmToken = fcmTokenRepository.findByUsersUsersIdAndToken(usersId, token)
                .orElseThrow(() -> new FcmTokenException(FCM_TOKEN_NOT_FOUND));
        fcmTokenRepository.delete(fcmToken);
        return DeleteResponse.toDto(false);
    }
}
