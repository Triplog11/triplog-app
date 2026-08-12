package triplog.backend.mission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import triplog.backend.mission.entity.UsersMission;

import java.util.List;

/**
 * 사용자 미션 완료 정보의 데이터 접근을 담당하는 JPA Repository입니다.
 */
@Repository
public interface UsersMissionRepository extends JpaRepository<UsersMission, Long> {

    /**
     * 사용자 ID와 미션 ID 목록으로 완료된 미션 정보를 조회합니다.
     *
     * @param usersId 사용자 ID
     * @param missionIds 미션 ID 목록
     * @return 해당 사용자가 완료한 미션 목록
     */
    List<UsersMission> findByUsersUsersIdAndMissionMissionIdIn(String usersId, List<Long> missionIds);

    /**
     * 사용자 미션 완료 기록을 중복 없이 저장합니다.
     *
     * @param usersId   사용자 식별자
     * @param missionId 미션 식별자
     * @return 새로 저장됐으면 1, 이미 존재하면 0
     */
    @Modifying
    @Query(value = """
            INSERT IGNORE INTO users_mission (users_id, mission_id, users_mission_created_at)
            VALUES (:usersId, :missionId, CURRENT_TIMESTAMP)
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("usersId") String usersId,
            @Param("missionId") Long missionId
    );
}
