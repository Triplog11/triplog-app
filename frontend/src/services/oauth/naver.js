import * as WebBrowser from 'expo-web-browser';
import * as Crypto from 'expo-crypto';
import { APP_RETURN_URL, parseRedirectParams } from './redirect';

const NAVER_AUTHORIZE_URL = 'https://nid.naver.com/oauth2.0/authorize';
const CLIENT_ID = process.env.EXPO_PUBLIC_NAVER_CLIENT_ID;
const REDIRECT_URI = process.env.EXPO_PUBLIC_NAVER_REDIRECT_URI;

function createState() {
  const bytes = Crypto.getRandomBytes(16);
  return Array.from(bytes, (b) => b.toString(16).padStart(2, '0')).join('');
}

/**
 * 네이버 로그인 인가 코드를 획득한다.
 * 브라우저에서 네이버 인증 후, 백엔드 콜백이 앱 스킴(triplog://oauth)으로
 * code/state를 되돌려주는 것을 전제로 한다.
 *
 * @returns {{provider: 'NAVER', code: string, state: string} | null} 사용자가 취소하면 null
 */
export async function getNaverAuthCode() {
  if (!CLIENT_ID || !REDIRECT_URI) {
    throw new Error('네이버 로그인 설정이 없어요. .env의 EXPO_PUBLIC_NAVER_* 값을 확인해 주세요.');
  }

  const state = createState();
  const authorizeUrl =
    `${NAVER_AUTHORIZE_URL}?response_type=code` +
    `&client_id=${encodeURIComponent(CLIENT_ID)}` +
    `&redirect_uri=${encodeURIComponent(REDIRECT_URI)}` +
    `&state=${encodeURIComponent(state)}`;

  const result = await WebBrowser.openAuthSessionAsync(authorizeUrl, APP_RETURN_URL);

  if (result.type !== 'success') {
    return null; // 사용자가 브라우저를 닫음
  }

  const params = parseRedirectParams(result.url);
  if (params.error) {
    throw new Error('네이버 로그인에 실패했어요. 다시 시도해 주세요.');
  }
  if (!params.code) {
    throw new Error('네이버 인가 코드를 받지 못했어요. 다시 시도해 주세요.');
  }
  if (params.state !== state) {
    throw new Error('네이버 로그인 검증에 실패했어요. 다시 시도해 주세요.');
  }

  return { provider: 'NAVER', code: params.code, state: params.state };
}
