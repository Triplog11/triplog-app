package triplog.backend.fcmtoken.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.fcmtoken.entity.FcmToken;

/**
 * FCM 푸시 토큰 엔티티의 데이터 접근을 담당하는 JPA Repository입니다.
 * <p>
 * Spring Data JPA를 기반으로 FCM 토큰 정보를 조회하고 관리합니다.
 */
@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    /**
     * 동일한 FCM 토큰이 이미 저장되어 있는지 확인합니다.
     *
     * @param token 확인할 FCM 토큰
     * @return 토큰이 존재하면 {@code true}
     */
    boolean existsByToken(String token);
}
