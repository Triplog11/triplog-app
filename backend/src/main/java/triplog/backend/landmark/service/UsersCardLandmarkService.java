package triplog.backend.landmark.service;

import org.springframework.data.domain.Pageable;
import triplog.backend.landmark.dto.response.LandmarkResponse.ObtainedCardListResponse;
import triplog.backend.landmark.entity.UsersCardLandmark;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 사용자 랜드마크 카드의 획득과 조회 기능을 정의합니다.
 */
public interface UsersCardLandmarkService {

    /** 홈 화면에 노출할 최근 획득 카드 정보를 조회합니다. */
    List<LandmarkHomeCardInfo> getRecentObtainedCardInfo(String usersId, int limit);

    /** 사용자가 수집한 카드 수를 조회합니다. */
    int countCollectedCards(String usersId);

    /** 사용자가 획득한 카드 목록을 최신순으로 조회합니다. */
    ObtainedCardListResponse getObtainedCards(String usersId, Pageable pageable);

    /** 사용자가 획득한 랜드마크 식별자 집합을 조회합니다. */
    Set<Long> findAcquiredLandmarkIdsByUsersId(String usersId);

    /** 사용자와 랜드마크에 해당하는 카드 획득 정보를 조회합니다. */
    Optional<UsersCardLandmark> findOptionalWithCard(String usersId, Long landmarkId);

    /** 카드를 중복 없이 최초 1회 획득 처리합니다. */
    boolean acquireCard(String usersId, Long landmarkId);
}
