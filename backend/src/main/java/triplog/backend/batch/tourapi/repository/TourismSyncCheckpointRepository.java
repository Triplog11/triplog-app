package triplog.backend.batch.tourapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.batch.tourapi.entity.TourismSyncCheckpoint;
import triplog.backend.batch.tourapi.entity.TourismSyncType;

import java.util.Optional;

/**
 * 동기화 유형별 성공 체크포인트를 저장하고 조회합니다.
 */
public interface TourismSyncCheckpointRepository extends JpaRepository<TourismSyncCheckpoint, Long> {

    /**
     * 동기화 유형의 체크포인트를 조회합니다.
     *
     * @param syncType 동기화 작업 유형
     * @return 저장된 체크포인트 또는 빈 값
     */
    Optional<TourismSyncCheckpoint> findBySyncType(TourismSyncType syncType);
}
