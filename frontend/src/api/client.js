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
const SESSION_EXPIRED_MESSAGE = '세션이 만료되었어요. 다시 로그인해 주세요.';
const REQUEST_TIMEOUT_MS = 15000;

let onUnauthorized = null;

/** 401 응답 시(재발급도 실패) 호출될 핸들러 등록 (AuthContext가 세션 정리용으로 사용) */
export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler;
}

/**
 * 동시 재발급 폭주 방지 — 진행 중인 재발급 프라미스를 공유한다.
 * 여러 요청이 동시에 401을 받아도 재발급은 한 번만 수행된다.
 */
let reissuePromise = null;

async function reissueAccessToken() {
  if (!reissuePromise) {
    reissuePromise = (async () => {
      const { getTokens, saveTokens } = require('../utils/tokenStorage');
      const tokens = await getTokens();
      if (!tokens) {
        throw new ApiError(401, SESSION_EXPIRED_MESSAGE);
      }
      // 재발급은 무인증 엔드포인트 — Authorization 헤더 없이 refreshToken만 보낸다
      const data = await request('/auth/reissue', {
        method: 'POST',
        body: { refreshToken: tokens.refreshToken },
      });
      await saveTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken });
      return data.accessToken;
    })();
  }
  try {
    return await reissuePromise;
  } finally {
    reissuePromise = null;
  }
}

/**
 * 저장된 액세스 토큰을 붙여 호출하는 인증 API 래퍼.
 * 401을 받으면 refreshToken으로 한 번 재발급 후 재시도하고,
 * 재발급도 실패하면 onUnauthorized(로그아웃)를 호출한다.
 * (토큰 저장소를 늦게 로드해 client ↔ storage 순환 참조를 피한다)
 */
export async function authedRequest(path, options = {}) {
  const { getTokens } = require('../utils/tokenStorage');
  const tokens = await getTokens();
  if (!tokens) {
    throw new ApiError(401, '로그인이 필요합니다.');
  }

  const attempt = (accessToken) => request(path, { ...options, token: accessToken });

  try {
    return await attempt(tokens.accessToken);
  } catch (error) {
    if (!(error instanceof ApiError) || error.status !== 401) {
      throw error;
    }
    // 액세스 토큰 만료 추정 — 재발급 후 1회 재시도
    try {
      const newAccessToken = await reissueAccessToken();
      return await attempt(newAccessToken);
    } catch (reissueError) {
      if (onUnauthorized) onUnauthorized();
      throw reissueError instanceof ApiError
        ? reissueError
        : new ApiError(401, SESSION_EXPIRED_MESSAGE);
    }
  }
}

/**
 * 인증이 필요한 multipart/form-data 요청 (이미지 업로드, 방문 인증 등).
 * FormData는 fetch가 boundary를 직접 세팅하도록 Content-Type을 넘기지 않는다.
 */
export function authedMultipartRequest(path, formData, { method = 'POST', headers } = {}) {
  return authedRequest(path, { method, formData, headers });
}

/**
 * 백엔드 API 공통 fetch 래퍼.
 * 에러 응답 포맷 {status, message}를 ApiError로 변환한다.
 * body는 JSON, formData는 multipart로 전송된다(둘 중 하나만 사용).
 */
export async function request(path, {
  method = 'GET', body, token, formData, headers: extraHeaders,
} = {}) {
  if (!BASE_URL) {
    throw new ApiError(0, 'API 서버 주소가 설정되지 않았어요. .env의 EXPO_PUBLIC_API_URL을 확인해 주세요.');
  }

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  const headers = {
    ...(extraHeaders ?? {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
  let fetchBody;
  if (formData !== undefined) {
    fetchBody = formData; // Content-Type은 fetch가 boundary와 함께 자동 설정
  } else if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
    fetchBody = JSON.stringify(body);
  }

  let response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      ...(fetchBody !== undefined ? { body: fetchBody } : {}),
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
    const message = data?.message ?? SERVER_ERROR_MESSAGE;
    throw new ApiError(response.status, message);
  }

  return data;
}
