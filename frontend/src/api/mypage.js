import { authedRequest } from './client';

/**
 * 마이페이지 요약 — 프로필/레벨/티어/통계/대표 칭호·뱃지를 한 번에 조회.
 * @returns {nickname, profileUrl, level, xp, tier, overallScore, monthScore,
 *           totalCertificationCount, visitedRegionCount, acquiredBadgeCount, collectedCardCount,
 *           representativeAppellation: {appellationId, appellationName} | null,
 *           representativeBadge: {badgeId, badgeName, badgeUrl} | null}
 */
export function fetchMyPage() {
  return authedRequest('/users/mypage');
}

/** 활동 내역 타입 */
export const ACTIVITY_TYPE = {
  ATTRACTION: 'ATTRACTION',
  LANDMARK: 'LANDMARK',
  REGION: 'REGION',
  CARD: 'CARD',
  BADGE: 'BADGE',
  TITLE: 'TITLE',
  LEVEL: 'LEVEL',
  RANK: 'RANK',
  MISSION: 'MISSION',
};

/**
 * 내 활동 내역(보상 로그) — 최신순 페이징.
 * @returns {page, size, totalElements, totalPages,
 *           activities: [{activityId, activityType, title, content, score, xp, createdAt}]}
 */
export function fetchActivityHistory({ page = 0, size = 20 } = {}) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  return authedRequest(`/mypage/activityhistory?${params.toString()}`);
}
