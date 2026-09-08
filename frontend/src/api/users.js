import { request, authedRequest, ApiError } from './client';

/**
 * 닉네임 중복 확인.
 * @returns {available: boolean, message: string}
 */
export function checkNickname(nickname) {
  return request('/users/nickname/check', {
    method: 'POST',
    body: { nickName: nickname },
  });
}

/**
 * 프로필 수정 — 보낸 필드만 갱신된다.
 * @param {{nickname?, addressSi?, addressDoGun?, addressGu?, profileUrl?}} changes
 * @returns {usersId, nickname, addressSi, addressDoGun, addressGu, profileUrl}
 */
export function updateProfile(changes) {
  return authedRequest('/users/profile', {
    method: 'PATCH',
    body: changes,
  });
}

/**
 * 이메일 중복 확인.
 * @returns {available: boolean, message: string}
 */
export function checkEmail(email) {
  return request('/users/email/check', {
    method: 'POST',
    body: { email },
  });
}

/**
 * 회원 탈퇴. 본인 확인을 위해 가입한 이메일과 현재 세션의 리프레시 토큰을 함께 보냅니다.
 * 성공하면 계정과 관련 데이터(방문 인증, 여행 기록, 수집한 카드와 뱃지)가 모두 삭제됩니다.
 * @param {string} email 탈퇴할 계정의 이메일
 * @returns {{deleted: boolean, email: string, deletedAt: string}}
 */
export async function withdrawAccount(email) {
  const { getTokens } = require('../utils/tokenStorage');
  const tokens = await getTokens();
  if (!tokens) {
    throw new ApiError(401, '로그인이 필요합니다.');
  }
  return authedRequest('/users/me', {
    method: 'DELETE',
    body: { email, refreshToken: tokens.refreshToken },
  });
}
