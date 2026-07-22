package triplog.backend.batch.tourapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import triplog.backend.batch.tourapi.entity.TourismSyncFailure;
import triplog.backend.batch.tourapi.entity.TourismSyncFailureStatus;
import triplog.backend.batch.tourapi.entity.TourismSyncType;

import java.util.List;
import java.util.Optional;

/**
 * TourismSyncFailure 영속성 처리를 담당하는 Repository입니다.
 */
public interface TourismSyncFailureRepository extends JpaRepository<TourismSyncFailure, Long> {

    /**
     * 작업 유형과 TourAPI contentId로 실패 이력을 조회합니다.
     *
     * @param syncType 동기화 작업 유형
     * @param externalContentId TourAPI contentId
     * @return 일치하는 실패 이력
     */
    Optional<TourismSyncFailure> findBySyncTypeAndExternalContentId(
            TourismSyncType syncType,
            String externalContentId
    );

    /**
     * 지정 상태의 실패 이력을 조회합니다.
     *
     * @param status 실패 이력 상태
     * @return 상태와 일치하는 실패 이력 목록
     */
    List<TourismSyncFailure> findAllBySyncFailureStatus(TourismSyncFailureStatus status);
}
