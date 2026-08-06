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
