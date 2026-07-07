package triplog.backend.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import triplog.backend.users.entity.Users;

/**
 * 사용자(Users) 엔티티의 데이터 접근을 담당하는 JPA Repository입니다.
 * <p>
 * Spring Data JPA를 기반으로 CRUD 기능을 제공하며,
 * 메서드 이름 기반 쿼리, JPQL, Query Method 등을 통해 사용자 데이터를 조회하고 관리합니다.
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
}
