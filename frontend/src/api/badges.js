import { authedRequest } from './client';

/**
 * 뱃지 목록 조회 (페이징/필터).
 * isAcquired=true면 슬림 응답({badgeId, badgeName, badgeUrl, representative}),
 * 그 외에는 상세 응답({..., badgeType, badgeTarget, badgeValue, acquired})이 온다.
 * @returns {page, size, totalElements, totalPages, items: []}
 */
export function fetchBadges({ badgeType, isAcquired, page = 0, size = 30 } = {}) {
  const params = new URLSearchParams();
  params.append('page', String(page));
  params.append('size', String(size));
  if (badgeType != null) params.append('badgeType', badgeType);
  if (isAcquired != null) params.append('isAcquired', String(isAcquired));
  return authedRequest(`/badges?${params.toString()}`);
}

/**
 * 뱃지 상세 조회.
 * @returns {badgeId, badgeName, badgeUrl, badgeGroup, badgeType, badgeTarget,
 *           badgeOperator, badgeValue, acquired, representative}
 */
export function fetchBadgeDetail(badgeId) {
  return authedRequest(`/badges/${badgeId}`);
}
