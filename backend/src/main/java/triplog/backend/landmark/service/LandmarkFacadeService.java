package triplog.backend.landmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.landmark.dto.response.LandmarkResponse.LandmarkDetailResponse;
import triplog.backend.landmark.entity.Card;
import triplog.backend.landmark.entity.Landmark;
import triplog.backend.landmark.entity.UsersCardLandmark;

/**
 * 랜드마크 상세 조회에 필요한 랜드마크·카드·사용자 획득 정보를 조합합니다.
 */
@Service
@RequiredArgsConstructor
public class LandmarkFacadeService {

    private final LandmarkService landmarkService;
    private final CardService cardService;
    private final UsersCardLandmarkService usersCardLandmarkService;

    /**
     * 로그인 사용자를 기준으로 랜드마크 상세 정보를 조회합니다.
     *
     * @param usersId 사용자 식별자
     * @param landmarkId 랜드마크 식별자
     * @return 랜드마크 상세 응답
     */
    @Transactional(readOnly = true)
    public LandmarkDetailResponse getLandmarkDetail(String usersId, Long landmarkId) {
        Landmark landmark = landmarkService.findByIdWithContent(landmarkId);
        Card card = cardService.findOptionalByLandmarkId(landmarkId).orElse(null);
        UsersCardLandmark acquiredCard = usersCardLandmarkService
                .findOptionalWithCard(usersId, landmarkId)
                .orElse(null);
        return LandmarkDetailResponse.toDto(landmark, card, acquiredCard);
    }
}
