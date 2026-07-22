/** 앱 스킴 딥링크 — 소셜 로그인 완료 후 브라우저가 돌아올 주소 */
export const APP_RETURN_URL = 'triplog://oauth';

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
      [decodeURIComponent(key)]: decodeURIComponent(value.replace(/\+/g, ' ')),
    };
  }, {});
}
