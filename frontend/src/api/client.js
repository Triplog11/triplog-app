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

/**
 * 백엔드 API 공통 fetch 래퍼.
 * 에러 응답 포맷 {status, message}를 ApiError로 변환한다.
 */
export async function request(path, { method = 'GET', body, token } = {}) {
  if (!BASE_URL) {
    throw new ApiError(0, 'API 서버 주소가 설정되지 않았어요. .env의 EXPO_PUBLIC_API_URL을 확인해 주세요.');
  }

  let response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      method,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
      ...(body !== undefined ? { body: JSON.stringify(body) } : {}),
    });
  } catch (error) {
    console.error(`API 요청 실패: ${method} ${path}`, error);
    throw new ApiError(0, NETWORK_ERROR_MESSAGE);
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
