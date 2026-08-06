import { authedRequest } from './client';

/** 미션 주기 타입 */
export const MISSION_TYPE = {
  WEEKLY: 'WEEKLY',
  DAILY: 'DAILY',
};

/**
 * 내 미션 진행 현황 조회 (완료 여부 포함).
 * @param {{missionType?: string}} opts 생략 시 전체
 * @returns {missions: [{missionId, missionName, missionType, missionTarget, missionCondition,
 *           weekStart, weekEnd, rewardScore, rewardXp, completed, completedAt}]}
 */
export function fetchMyMissions({ missionType } = {}) {
  const params = new URLSearchParams();
  if (missionType != null) params.append('missionType', missionType);
  const query = params.toString();
  return authedRequest(`/missions/me${query ? `?${query}` : ''}`);
}

/**
 * 전체 미션 목록 조회 (완료 정보 없음).
 * @returns {missions: [{missionId, missionName, missionType, missionTarget, missionCondition,
 *           weekStart, weekEnd, rewardScore, rewardXp}]}
 */
export function fetchMissions({ missionType } = {}) {
  const params = new URLSearchParams();
  if (missionType != null) params.append('missionType', missionType);
  const query = params.toString();
  return authedRequest(`/missions${query ? `?${query}` : ''}`);
}
