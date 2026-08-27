import { request, authedRequest } from './client';

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
