package triplog.backend.fcmtoken.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.fcmtoken.repository.FcmTokenRepository;
import triplog.backend.users.repository.UsersRepository;

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
}
