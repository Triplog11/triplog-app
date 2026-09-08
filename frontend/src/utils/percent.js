/**
 * 달성률 표시 유틸.
 *
 * 서버는 completionRate 를 0~1 비율로 줄 때도 있고 0~100 퍼센트로 줄 때도 있어
 * 양쪽을 모두 받아 정규화한다. 표시할 때는 반올림해서 0이 되는 값을 그대로 '0%'
 * 로 쓰지 않는다. 전국 269개 시·군·구 가운데 한 곳을 방문하면 0.37% 인데 이를
 * 0% 로 보여주면 첫 인증을 마친 사용자가 아무것도 달성하지 못한 것으로 읽힌다.
 */

/** 0~1 비율과 0~100 퍼센트를 모두 0~100 범위의 수로 맞춘다. */
export function normalizePercent(rate) {
  if (typeof rate !== 'number' || Number.isNaN(rate) || rate < 0) return 0;
  return rate <= 1 ? rate * 100 : rate;
}

/**
 * 진행 바 너비처럼 계산에 쓸 정수 백분율.
 * @returns {number} 0 이상 100 이하
 */
export function toPercentValue(rate) {
  return Math.min(100, Math.round(normalizePercent(rate)));
}

/**
 * 화면에 보여줄 백분율 문자열. 값이 없으면 '--' 를 돌려준다.
 * 0 보다 크지만 반올림하면 0 이 되는 구간만 소수점 한 자리로 보여 준다.
 * @returns {string} 예: '--', '0', '0.4', '37', '100'
 */
export function formatPercent(rate) {
  if (rate == null) return '--';
  const value = normalizePercent(rate);
  if (value > 0 && value < 1) return value.toFixed(1);
  return String(Math.min(100, Math.round(value)));
}
