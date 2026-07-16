package triplog.backend.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.users.entity.LoginType;
import triplog.backend.users.entity.Users;

import java.util.Optional;

/**
 * 사용자(Users) 엔티티의 데이터 접근을 담당하는 JPA Repository입니다.
 * <p>
 * Spring Data JPA를 기반으로 CRUD 기능을 제공하며,
 * 메서드 이름 기반 쿼리, JPQL, Query Method 등을 통해 사용자 데이터를 조회하고 관리합니다.
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String> {

    /**
     * 이메일과 로그인 타입으로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 로그인 타입
     * @return 조회된 사용자
     */
    Optional<Users> findByEmailAndLoginType(String email, LoginType loginType);

    /**
     * 닉네임으로 사용자 존재 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 해당 닉네임을 사용하는 사용자가 존재하면 {@code true}
     */
    boolean existsByNickname(String nickname);

    /**
     * 이메일로 사용자 존재 여부를 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 해당 이메일을 사용하는 사용자가 존재하면 {@code true}
     */
    boolean existsByEmail(String email);
}
