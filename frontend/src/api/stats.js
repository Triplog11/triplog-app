import { authedRequest } from './client';

/** 랭킹 타입 */
export const RANKING_TYPE = {
  TOTAL: 'TOTAL',
  MONTHLY: 'MONTHLY',
  QUARTER: 'QUARTER',
};

/**
 * 내 랭킹 정보 조회.
 * @returns {nickname, profileUrl, totalRank, monthlyRank, quarterRank,
 *           overallScore, monthScore, quarterScore, level, tier, nextTier, requiredScore}
 */
export function fetchMyRanking() {
  return authedRequest('/stats/rankings/me');
}

/**
 * 전체 랭킹 목록 조회.
 * @param {{rankingType?: string, page?: number, size?: number}} opts
 * @returns {rankingType, page, size, totalElements, totalPages,
 *           rankings: [{rank, usersId, nickname, profileUrl, score, level, tier}]}
 */
export function fetchRankings({ rankingType = RANKING_TYPE.TOTAL, page = 0, size = 10 } = {}) {
  const params = new URLSearchParams({
    rankingType,
    page: String(page),
    size: String(size),
  });
  return authedRequest(`/stats/rankings?${params.toString()}`);
}

/**
 * 내 스탯 정보 조회 (레벨/경험치/티어).
 * @returns {level, xp, currentTier, overallScore, monthScore,
 *           nextLevel, requiredXp, remainingXp, nextTier, requiredScore}
 */
export function fetchMyStats() {
  return authedRequest('/stats/me');
}
