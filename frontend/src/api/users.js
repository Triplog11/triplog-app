import { request } from './client';

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
