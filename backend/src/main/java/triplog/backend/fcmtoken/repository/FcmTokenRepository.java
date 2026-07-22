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
}
