const BASE_URL = process.env.EXPO_PUBLIC_API_URL;

export class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

const NETWORK_ERROR_MESSAGE = '일시적으로 연결이 불안정해요. 잠시 후 다시 시도해 주세요.';
const SERVER_ERROR_MESSAGE = '서버에 문제가 생겼어요. 잠시 후 다시 시도해 주세요.';
const REQUEST_TIMEOUT_MS = 15000;

let onUnauthorized = null;

/** 401 응답 시 호출될 핸들러 등록 (AuthContext가 세션 정리용으로 사용) */
export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler;
}

/**
 * 저장된 액세스 토큰을 붙여 호출하는 인증 API 래퍼.
 * (토큰 저장소를 늦게 로드해 client ↔ storage 순환 참조를 피한다)
 */
export async function authedRequest(path, options = {}) {
  const { getTokens } = require('../utils/tokenStorage');
  const tokens = await getTokens();
  if (!tokens) {
    throw new ApiError(401, '로그인이 필요합니다.');
  }
  return request(path, { ...options, token: tokens.accessToken });
}

/**
 * 백엔드 API 공통 fetch 래퍼.
 * 에러 응답 포맷 {status, message}를 ApiError로 변환한다.
 */
export async function request(path, { method = 'GET', body, token } = {}) {
  if (!BASE_URL) {
    throw new ApiError(0, 'API 서버 주소가 설정되지 않았어요. .env의 EXPO_PUBLIC_API_URL을 확인해 주세요.');
  }

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  let response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      method,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
      signal: controller.signal,
    });
  } catch (error) {
    console.error(`API 요청 실패: ${method} ${path}`, error);
    throw new ApiError(0, NETWORK_ERROR_MESSAGE);
  } finally {
    clearTimeout(timeoutId);
  }

  let data = null;
  try {
    data = await response.json();
  } catch (error) {
    data = null;
  }

  if (!response.ok) {
    if (response.status === 401 && onUnauthorized) {
      onUnauthorized();
    }
    const message = data?.message ?? SERVER_ERROR_MESSAGE;
    throw new ApiError(response.status, message);
  }

  return data;
}
