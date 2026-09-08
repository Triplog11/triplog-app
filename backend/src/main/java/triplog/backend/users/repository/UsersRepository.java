package triplog.backend.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * 사용자 프로필 정보를 수정합니다.
     * <p>
     * 요청에서 전달되지 않은 필드는 {@code null}로 들어오며 기존 값을 유지합니다.
     *
     * @param usersId 수정할 사용자 ID
     * @param nickname 변경할 닉네임
     * @param profileUrl 변경할 프로필 이미지 URL
     * @return 수정된 행 수
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Users u
            set u.nickname = coalesce(:nickname, u.nickname),
                u.profileUrl = coalesce(:profileUrl, u.profileUrl)
            where u.usersId = :usersId
            """)
    int updateProfile(
            @Param("usersId") String usersId,
            @Param("nickname") String nickname,
            @Param("profileUrl") String profileUrl
    );

    /**
     * 회원 탈퇴 대상 사용자의 통계 정보를 삭제합니다.
     *
     * @param usersId 탈퇴할 사용자 ID
     */
    @Modifying
    @Query(value = "delete from stats where users_id = :usersId", nativeQuery = true)
    void deleteStatsByUsersId(@Param("usersId") String usersId);

    /**
     * 회원 탈퇴 대상 사용자의 획득 배지 정보를 삭제합니다.
     *
     * @param usersId 탈퇴할 사용자 ID
     */
    @Modifying
    @Query(value = "delete from users_badge where users_id = :usersId", nativeQuery = true)
    void deleteBadgesByUsersId(@Param("usersId") String usersId);

    /**
     * 회원 탈퇴 대상 사용자의 지역 방문 로그를 삭제합니다.
     *
     * @param usersId 탈퇴할 사용자 ID
     */
    @Modifying
    @Query(value = "delete from region_visit_log where users_id = :usersId", nativeQuery = true)
    void deleteRegionVisitLogsByUsersId(@Param("usersId") String usersId);

    /**
     * 회원 탈퇴 대상 사용자의 랜드마크 방문 로그를 삭제합니다.
     *
     * @param usersId 탈퇴할 사용자 ID
     */
    @Modifying
    @Query(value = "delete from landmark_visit_log where users_id = :usersId", nativeQuery = true)
    void deleteLandmarkVisitLogsByUsersId(@Param("usersId") String usersId);

    /**
     * 회원 탈퇴 대상 사용자의 관광지 방문 로그를 삭제합니다.
     *
     * @param usersId 탈퇴할 사용자 ID
     */
    @Modifying
    @Query(value = "delete from attraction_visit_log where users_id = :usersId", nativeQuery = true)
    void deleteAttractionVisitLogsByUsersId(@Param("usersId") String usersId);
}
