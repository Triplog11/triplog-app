import { request } from './client';

/**
 * 소셜/자체 로그인.
 * @returns 기존 회원: {nickname, level, xp, tier, accessToken, refreshToken}
 *          신규 회원: {expiresIn, temporaryToken}
 */
export function oauthLogin({ provider, code, state, email, password }) {
  return request('/auth/oauth', {
    method: 'POST',
    body: { provider, code, state, email, password },
  });
}

/**
 * 소셜 신규 회원 추가정보 입력 (임시 토큰 인증).
 * @returns {nickname, level, xp, tier, accessToken, refreshToken}
 */
export function submitAdditionalInfo(temporaryToken, {
  nickname,
  profileUrl,
  addressSi,
  addressDoGun,
  addressGu,
  isNotification,
}) {
  return request('/auth/additional-info', {
    method: 'POST',
    token: temporaryToken,
    body: { nickname, profileUrl, addressSi, addressDoGun, addressGu, isNotification },
  });
}

/**
 * 로그아웃 — 서버의 Refresh Token을 무효화한다.
 */
export function logoutRequest(accessToken, refreshToken) {
  return request('/auth/logout', {
    method: 'POST',
    token: accessToken,
    body: { refreshToken },
  });
}

/**
 * 이메일 자체 회원가입. 성공 후 oauthLogin({provider: 'LOCAL', email, password})로 로그인한다.
 * @returns {isRegister: boolean}
 */
export function signup({
  nickname, profileUrl, addressSi, addressDoGun, addressGu, email, password, isNotification,
}) {
  return request('/auth/signup', {
    method: 'POST',
    body: {
      nickname, profileUrl, addressSi, addressDoGun, addressGu, email, password, isNotification,
    },
  });
}
