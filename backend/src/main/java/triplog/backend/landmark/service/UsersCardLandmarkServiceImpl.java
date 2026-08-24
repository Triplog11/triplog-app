package triplog.backend.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.landmark.dto.response.LandmarkResponse.ObtainedCardListResponse;
import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.UsersCardLandmark;
import triplog.backend.landmark.repository.UsersCardLandmarkRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * {@link UsersCardLandmarkService}의 기본 구현체입니다.
 * 사용자 카드 획득 관계의 저장소 접근을 전담합니다.
 */
@Service
@RequiredArgsConstructor
public class UsersCardLandmarkServiceImpl implements UsersCardLandmarkService {

    private final UsersCardLandmarkRepository usersCardLandmarkRepository;
    private final LandmarkService landmarkService;
    private final CardService cardService;

    /**
     * 홈 화면에 노출할 최근 획득 카드 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param limit 최대 조회 수
     * @return 최근 획득 카드 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<LandmarkHomeCardInfo> getRecentObtainedCardInfo(String usersId, int limit) {
        return usersCardLandmarkRepository
                .findByUsersIdOrderByUsersCardLandmarkVisitedAtDescUsersCardLandmarkIdDesc(
                        usersId,
                        PageRequest.of(0, limit)
                )
                .getContent().stream()
                .map(LandmarkHomeCardInfo::from)
                .toList();
    }

    /**
     * 사용자가 수집한 카드 수를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 수집 카드 수
     */
    @Override
    @Transactional(readOnly = true)
    public int countCollectedCards(String usersId) {
        return Math.toIntExact(usersCardLandmarkRepository.countByUsersId(usersId));
    }

    /**
     * 사용자가 획득한 카드 목록을 최신순으로 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param pageable 페이지 정보
     * @return 획득 카드 목록 응답
     */
    @Override
    @Transactional(readOnly = true)
    public ObtainedCardListResponse getObtainedCards(String usersId, Pageable pageable) {
        Page<UsersCardLandmark> obtainedCards = usersCardLandmarkRepository
                .findByUsersIdOrderByUsersCardLandmarkVisitedAtDescUsersCardLandmarkIdDesc(
                        usersId,
                        pageable
                );
        return ObtainedCardListResponse.toDto(obtainedCards);
    }

    /**
     * 사용자가 획득한 랜드마크 식별자 집합을 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @return 획득한 랜드마크 식별자 집합
     */
    @Override
    @Transactional(readOnly = true)
    public Set<Long> findAcquiredLandmarkIdsByUsersId(String usersId) {
        return usersCardLandmarkRepository.findAcquiredLandmarkIdsByUsersId(usersId);
    }

    /**
     * 사용자와 랜드마크에 해당하는 카드 획득 정보를 카드와 함께 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 카드 획득 정보, 없으면 빈 값
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UsersCardLandmark> findOptionalWithCard(String usersId, Long landmarkId) {
        return usersCardLandmarkRepository
                .findByUsersIdAndLandmarkLandmarkIdWithCard(usersId, landmarkId);
    }

    /**
     * 사용자 카드를 데이터베이스 유일 제약으로 중복 없이 획득 처리합니다.
     *
     * @param usersId 사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 이번 호출에서 새로 획득했으면 {@code true}
     */
    @Override
    @Transactional
    public boolean acquireCard(String usersId, Long landmarkId) {
        // 랜드마크와 카드의 존재를 각 소유 서비스에서 검증한 뒤 식별자만 저장합니다.
        landmarkService.findByIdWithContent(landmarkId);
        Card card = cardService.findByLandmarkId(landmarkId);
        return usersCardLandmarkRepository.insertIfAbsent(
                usersId,
                landmarkId,
                card.getCardId(),
                LocalDateTime.now()
        ) == 1;
    }
}
