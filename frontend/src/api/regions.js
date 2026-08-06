import { authedRequest } from './client';

/**
 * 지역(시·군·구) 목록 조회 (페이징).
 * @returns {page, size, totalElements, totalPages,
 *           regions: [{regionId, regionName, regionOverview, legalRegionCode, legalDistrictCode, visited}]}
 */
export function fetchRegions({ page = 0, size = 10 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return authedRequest(`/regions?${params.toString()}`);
}

/**
 * 전국 지도 현황 조회.
 * @returns {totalRegionCount, completedRegionCount, visitedRegionCount, overallCompletionRate,
 *           regions: [{regionId, regionName, legalRegionCode, legalDistrictCode, visited, completed, completionRate}]}
 */
export function fetchNationwideMap() {
  return authedRequest('/regions/nationwide/map');
}

/**
 * 광역(시·도) 지도 현황 조회.
 * @param {string} provinceCode 2자리 법정 시·도 코드 (예: '41')
 * @returns {totalRegionCount, completedRegionCount, visitedRegionCount, provinceCompletionRate, regions: [...]}
 */
export function fetchProvinceMap(provinceCode) {
  return authedRequest(`/regions/provinces/${provinceCode}/map`);
}

/**
 * 지역 상세 조회.
 * @returns {regionId, regionName, regionOverview, legalRegionCode, legalDistrictCode, visited, visitedCount,
 *           landmarks: {items: [{landmarkId, landmarkName, contentId, legalRegionCode, legalDistrictCode, acquired}]}}
 */
export function fetchRegionDetail(regionId) {
  return authedRequest(`/regions/${regionId}`);
}
