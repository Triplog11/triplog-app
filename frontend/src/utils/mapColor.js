/**
 * 방문률을 지도 색으로 바꾸는 순수 함수 모음.
 * 색 스케일 정의는 theme.mapScale 한 곳뿐이며, 지도 도형과 범례 막대가 모두 이 파일을 거친다.
 */
import theme from '../theme/theme';

const STOPS = theme.mapScale;

/** 범례 그라데이션 막대에 그대로 넘기는 색 배열 (LinearGradient colors) */
export const SCALE_COLORS = STOPS.map((stop) => stop.color);

/** 범례 그라데이션 막대의 색 위치 배열 (LinearGradient locations) */
export const SCALE_LOCATIONS = STOPS.map((stop) => stop.at);

/** 방문률 0%에 해당하는 미방문 회색 */
export const UNVISITED_COLOR = STOPS[0].color;

function hexToRgb(hex) {
  const value = parseInt(hex.slice(1), 16);
  return { r: (value >> 16) & 255, g: (value >> 8) & 255, b: value & 255 };
}

function rgbToHex({ r, g, b }) {
  const channel = (n) => Math.round(n).toString(16).padStart(2, '0');
  return `#${channel(r)}${channel(g)}${channel(b)}`.toUpperCase();
}

/**
 * 서버가 방문률을 0~1 비율로 줄 때와 0~100 퍼센트로 줄 때가 모두 있어서 0~1로 맞춘다.
 * 1보다 큰 값만 퍼센트로 보므로 1은 100%로 해석하며, 숫자가 아니거나 음수면 0으로 본다.
 * @param {number|string|null|undefined} value
 * @returns {number} 0 이상 1 이하의 방문률
 */
export function normalizeRate(value) {
  const number = Number(value);
  if (!Number.isFinite(number) || number <= 0) return 0;
  return Math.min(1, number > 1 ? number / 100 : number);
}

/**
 * 방문률을 스케일 색(HEX 문자열)으로 변환한다.
 * @param {number|string|null|undefined} rate 0~1 비율 또는 0~100 퍼센트
 * @returns {string} 예: 0 → '#E8EAEC', 1 → '#368FFF'
 */
export function visitRateColor(rate) {
  const ratio = normalizeRate(rate);
  if (ratio <= 0) return UNVISITED_COLOR;

  const upperIndex = STOPS.findIndex((stop) => ratio <= stop.at);
  if (upperIndex <= 0) return STOPS[STOPS.length - 1].color;

  const lower = STOPS[upperIndex - 1];
  const upper = STOPS[upperIndex];
  const span = upper.at - lower.at;
  const t = span > 0 ? (ratio - lower.at) / span : 0;
  const from = hexToRgb(lower.color);
  const to = hexToRgb(upper.color);

  return rgbToHex({
    r: from.r + (to.r - from.r) * t,
    g: from.g + (to.g - from.g) * t,
    b: from.b + (to.b - from.b) * t,
  });
}

/**
 * 방문 수와 전체 수로 방문률 색을 구한다. 전체가 0이면 미방문 회색을 돌려준다.
 * @param {number} collected 방문한 시·군·구 수
 * @param {number} total 전체 시·군·구 수
 * @returns {string} HEX 색상 문자열
 */
export function countRatioColor(collected, total) {
  if (!Number.isFinite(total) || total <= 0) return UNVISITED_COLOR;
  return visitRateColor((Number(collected) || 0) / total);
}

/**
 * 시·군·구 목록의 completionRate 평균을 방문률로 삼는다.
 * 상세 지도는 도형과 시·군·구를 짝지을 식별자가 없어서 시·도 단위 평균만 색으로 표현한다.
 * @param {Array<{completionRate?: number, visited?: boolean}>} regions
 * @returns {number|null} 0~1 방문률, 계산할 수 없으면 null
 */
export function averageCompletionRate(regions) {
  if (!Array.isArray(regions) || regions.length === 0) return null;
  const sum = regions.reduce((acc, region) => {
    const rate = region?.completionRate;
    if (rate != null) return acc + normalizeRate(rate);
    return acc + (region?.visited ? 1 : 0);
  }, 0);
  return sum / regions.length;
}
