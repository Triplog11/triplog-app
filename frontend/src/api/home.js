import { authedRequest } from './client';

/**
 * 홈 요약 정보 — 레벨/랭크/미션/최근 카드/최근 방문 지역을 한 번에 조회.
 * @returns {
 *   levelInformation: [{level, nickname, xp, levelPolicy}],
 *   rankInformation: {currentTier, monthScore, overallScore},
 *   missionInformation: [{missionId, missionName, missionType, missionTarget, missionOperator,
 *                         missionValue, missionFilter, missionWeekStart, missionWeekEnd,
 *                         missionScore, missionXp}],
 *   cardInformation: [{landmarkId, landmarkName, landmarkZipcode, cardTier, cardName, cardUrl}],
 *   regionInformation: [{regionId, regionName, regionOverview, regionZipcode, visitedAt, visitedCount}]
 * }
 */
export function fetchHome() {
  return authedRequest('/home');
}
