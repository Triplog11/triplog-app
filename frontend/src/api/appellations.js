import { authedRequest } from './client';

/**
 * 내가 획득한 칭호 목록.
 * @returns {totalElements, items: [{appellationId, appellationName, representative}]}
 */
export function fetchAppellations() {
  return authedRequest('/appellations');
}

/**
 * 대표 칭호 설정.
 * @returns {appellationId, appellationName, representative}
 */
export function setRepresentativeAppellation(appellationId) {
  return authedRequest(`/appellations/${appellationId}/representative`, { method: 'PATCH' });
}
