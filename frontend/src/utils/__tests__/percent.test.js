import { formatPercent, normalizePercent, toPercentValue } from '../percent';

describe('normalizePercent', () => {
  it('0~1 비율을 0~100 범위로 바꾼다', () => {
    expect(normalizePercent(0.37)).toBeCloseTo(37);
    expect(normalizePercent(1)).toBe(100);
  });

  it('이미 0~100 퍼센트로 온 값은 그대로 둔다', () => {
    expect(normalizePercent(37)).toBe(37);
    expect(normalizePercent(100)).toBe(100);
  });

  it('숫자가 아니거나 음수면 0으로 본다', () => {
    expect(normalizePercent(null)).toBe(0);
    expect(normalizePercent(undefined)).toBe(0);
    expect(normalizePercent(NaN)).toBe(0);
    expect(normalizePercent(-5)).toBe(0);
    expect(normalizePercent('37')).toBe(0);
  });
});

describe('formatPercent', () => {
  it('값이 없으면 두 줄표를 보여 준다', () => {
    expect(formatPercent(null)).toBe('--');
    expect(formatPercent(undefined)).toBe('--');
  });

  it('전국 269곳 중 1곳을 방문해도 0으로 뭉개지 않는다', () => {
    // 1 / 269 = 0.37% — 반올림하면 0이 되어 사용자가 아무것도 못 한 것으로 읽힌다
    expect(formatPercent(1 / 269)).toBe('0.4');
  });

  it('1% 미만 구간만 소수점 한 자리로 보여 준다', () => {
    expect(formatPercent(0.004)).toBe('0.4');
    expect(formatPercent(0.009)).toBe('0.9');
    expect(formatPercent(0.012)).toBe('1');
  });

  it('아무 곳도 방문하지 않았으면 0을 보여 준다', () => {
    expect(formatPercent(0)).toBe('0');
  });

  it('1% 이상은 정수로 반올림한다', () => {
    expect(formatPercent(0.374)).toBe('37');
    expect(formatPercent(0.376)).toBe('38');
    expect(formatPercent(37.4)).toBe('37');
  });

  it('1을 넘는 값은 이미 퍼센트로 온 것으로 보고 100을 넘지 않는다', () => {
    // 0~1 비율이라면 1.2 가 나올 수 없으므로 1.2% 로 읽는다
    expect(formatPercent(1.2)).toBe('1');
    expect(formatPercent(120)).toBe('100');
  });
});

describe('toPercentValue', () => {
  it('진행 바 너비로 쓸 정수를 돌려준다', () => {
    expect(toPercentValue(0.37)).toBe(37);
    expect(toPercentValue(37)).toBe(37);
    expect(toPercentValue(null)).toBe(0);
  });

  it('너비가 100을 넘지 않는다', () => {
    expect(toPercentValue(1.5)).toBe(2);
    expect(toPercentValue(150)).toBe(100);
  });
});
