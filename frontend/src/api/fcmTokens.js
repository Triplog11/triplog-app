import { authedRequest } from './client';

/**
 * FCM 푸시 토큰 등록.
 * @returns {isRegistered: boolean} 이미 등록된 토큰이면 409
 */
export function registerFcmToken({ token, deviceType, deviceName }) {
  return authedRequest('/fcm-tokens', {
    method: 'POST',
    body: { token, deviceType, deviceName },
  });
}

/**
 * FCM 푸시 토큰 삭제 (로그아웃 시).
 * @returns {isRegistered: boolean} 성공 시 false
 */
export function deleteFcmToken(token) {
  return authedRequest('/fcm-tokens', {
    method: 'DELETE',
    body: { token },
  });
}
