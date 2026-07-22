import * as WebBrowser from 'expo-web-browser';
import { APP_RETURN_URL, parseRedirectParams } from './redirect';

const GOOGLE_AUTHORIZE_URL = 'https://accounts.google.com/o/oauth2/v2/auth';
const CLIENT_ID = process.env.EXPO_PUBLIC_GOOGLE_CLIENT_ID;
const REDIRECT_URI = process.env.EXPO_PUBLIC_GOOGLE_REDIRECT_URI;

/**
 * 구글 로그인 인가 코드를 획득한다.
 * redirect_uri는 백엔드 env의 GOOGLE_REDIRECT_URI와 반드시 동일해야 하며
 * (백엔드가 같은 값으로 토큰 교환), 해당 콜백이 앱 스킴(triplog://oauth)으로
 * code를 되돌려주는 것을 전제로 한다.
 *
 * @returns {{provider: 'GOOGLE', code: string} | null} 사용자가 취소하면 null
 */
export async function getGoogleAuthCode() {
  if (!CLIENT_ID || !REDIRECT_URI) {
    throw new Error('구글 로그인 설정이 없어요. .env의 EXPO_PUBLIC_GOOGLE_* 값을 확인해 주세요.');
  }

  const authorizeUrl =
    `${GOOGLE_AUTHORIZE_URL}?response_type=code` +
    `&client_id=${encodeURIComponent(CLIENT_ID)}` +
    `&redirect_uri=${encodeURIComponent(REDIRECT_URI)}` +
    `&scope=${encodeURIComponent('openid email profile')}` +
    '&prompt=select_account';

  const result = await WebBrowser.openAuthSessionAsync(authorizeUrl, APP_RETURN_URL);

  if (result.type !== 'success') {
    return null; // 사용자가 브라우저를 닫음
  }

  const params = parseRedirectParams(result.url);
  if (params.error) {
    throw new Error('구글 로그인에 실패했어요. 다시 시도해 주세요.');
  }
  if (!params.code) {
    throw new Error('구글 인가 코드를 받지 못했어요. 다시 시도해 주세요.');
  }

  return { provider: 'GOOGLE', code: params.code };
}
