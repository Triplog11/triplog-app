import * as Crypto from 'expo-crypto';

/** 앱 스킴 딥링크 — 소셜 로그인 완료 후 브라우저가 돌아올 주소 */
export const APP_RETURN_URL = 'triplog://oauth';

/**
 * 백엔드 콜백은 state가 'app:'으로 시작할 때만 딥링크로 302 리다이렉트하고,
 * 그 외에는 로그인 테스트 페이지를 반환한다 (AuthTestPageController#isAppLogin)
 */
const APP_STATE_PREFIX = 'app:';

/** CSRF 방지용 랜덤 state 생성 (CSPRNG) — 'app:' 접두사로 앱 로그인 요청임을 표시 */
export function createOAuthState() {
  const bytes = Crypto.getRandomBytes(16);
  const random = Array.from(bytes, (b) => b.toString(16).padStart(2, '0')).join('');
  return `${APP_STATE_PREFIX}${random}`;
}

function safeDecode(value) {
  try {
    return decodeURIComponent(value.replace(/\+/g, '%20'));
  } catch (error) {
    return value;
  }
}

/**
 * 리다이렉트 URL의 쿼리 파라미터를 파싱한다.
 * (RN 환경의 URL 구현 편차를 피하기 위한 수동 파싱)
 */
export function parseRedirectParams(url) {
  const queryString = url.split('#')[0].split('?')[1];
  if (!queryString) {
    return {};
  }
  return queryString.split('&').reduce((params, pair) => {
    const [key, value = ''] = pair.split('=');
    if (!key) {
      return params;
    }
    return {
      ...params,
      [safeDecode(key)]: safeDecode(value),
    };
  }, {});
}
