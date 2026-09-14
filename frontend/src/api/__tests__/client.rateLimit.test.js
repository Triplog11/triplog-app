// client 모듈은 로드 시점에 BASE_URL 을 읽으므로 import 전에 주소를 넣어야 한다.
process.env.EXPO_PUBLIC_API_URL = 'https://test.triplog.local';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const { request, ApiError, resolveRetryDelay } = require('../client');

describe('resolveRetryDelay', () => {
  it('Retry-After 가 있으면 그 값을 초 단위로 따른다', () => {
    expect(resolveRetryDelay(2, 0)).toBe(2000);
    expect(resolveRetryDelay(3, 1)).toBe(3000);
  });

  it('Retry-After 가 지나치게 길면 5초로 자른다', () => {
    expect(resolveRetryDelay(60, 0)).toBe(5000);
  });

  it('Retry-After 가 없으면 재시도할수록 간격이 늘어난다', () => {
    expect(resolveRetryDelay(NaN, 0)).toBe(1200);
    expect(resolveRetryDelay(NaN, 1)).toBe(2400);
    expect(resolveRetryDelay(null, 2)).toBe(3600);
  });

  it('음수나 0은 무시하고 기본 간격을 쓴다', () => {
    expect(resolveRetryDelay(0, 0)).toBe(1200);
    expect(resolveRetryDelay(-5, 0)).toBe(1200);
  });
});

describe('429 자동 재시도', () => {
  const okResponse = (data) => ({
    ok: true,
    status: 200,
    json: async () => data,
    headers: { get: () => null },
  });

  const tooManyResponse = (retryAfter = null) => ({
    ok: false,
    status: 429,
    json: async () => ({ status: 429, message: '요청을 너무 많이 보냈습니다.' }),
    headers: { get: (key) => (key === 'Retry-After' ? retryAfter : null) },
  });

  // 재시도 대기(1.2초씩)를 실제로 기다리므로 넉넉한 제한을 둔다.
  // setTimeout 을 모킹하면 요청 타임아웃 타이머까지 즉시 발동해 요청이 취소된다.
  jest.setTimeout(20000);

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    delete global.fetch;
  });

  it('429 를 받으면 기다렸다 다시 시도해 성공시킨다', async () => {
    global.fetch
      .mockResolvedValueOnce(tooManyResponse('1'))
      .mockResolvedValueOnce(okResponse({ nickname: '테스터' }));

    await expect(request('/users/mypage')).resolves.toEqual({ nickname: '테스터' });
    expect(global.fetch).toHaveBeenCalledTimes(2);
  });

  it('계속 429 면 재시도를 멈추고 429 오류를 던진다', async () => {
    global.fetch.mockResolvedValue(tooManyResponse('1'));

    await expect(request('/users/mypage')).rejects.toMatchObject({
      name: 'ApiError',
      status: 429,
    });
    // 첫 요청 + 재시도 2회
    expect(global.fetch).toHaveBeenCalledTimes(3);
  });

  it('429 가 아닌 오류는 곧바로 던지고 재시도하지 않는다', async () => {
    global.fetch.mockResolvedValue({
      ok: false,
      status: 404,
      json: async () => ({ status: 404, message: '찾을 수 없습니다.' }),
      headers: { get: () => null },
    });

    await expect(request('/landmarks/99999')).rejects.toBeInstanceOf(ApiError);
    expect(global.fetch).toHaveBeenCalledTimes(1);
  });

  it('처음부터 성공하면 재시도하지 않는다', async () => {
    global.fetch.mockResolvedValue(okResponse({ ok: 1 }));

    await expect(request('/home')).resolves.toEqual({ ok: 1 });
    expect(global.fetch).toHaveBeenCalledTimes(1);
  });
});
