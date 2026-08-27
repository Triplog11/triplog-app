import { authedRequest } from './client';

/**
 * 랜드마크 상세 조회.
 * @returns {landmarkId, landmarkName, regionId, regionName, contentId,
 *           legalRegionCode, legalDistrictCode, acquired, acquiredAt, visitCount}
 *          (acquiredAt/visitCount는 미획득 시 null)
 */
export function fetchLandmarkDetail(landmarkId) {
  return authedRequest(`/landmarks/${landmarkId}`);
}

/**
 * 내가 획득한 랜드마크 카드 목록 — 최신 획득순 페이징.
 * @returns {page, size, totalElements, totalPages,
 *           items: [{cardId, landmarkId, landmarkName, cardName, cardTier, cardUrl, acquiredAt}]}
 */
export function fetchMyCards({ page = 0, size = 20 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return authedRequest(`/landmarks/me?${params.toString()}`);
}
