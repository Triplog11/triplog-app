package triplog.backend.common.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.common.auth.entity.RefreshToken;

import java.util.Optional;

/**
 * Redis에 저장된 {@link RefreshToken} 데이터 접근을 담당하는 Repository입니다.
 * <p>
 * Refresh Token 저장, 조회, 삭제 기능을 Spring Data Redis 기반으로 제공합니다.
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    /**
     * Refresh Token 문자열로 저장된 토큰 정보를 조회합니다.
     *
     * @param refreshToken 클라이언트가 전달한 Refresh Token
     * @return 저장된 Refresh Token 엔티티
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
