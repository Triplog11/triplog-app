/**
 * Cloudinary 이미지 URL 최적화 헬퍼.
 *
 * 서버가 내려주는 카드 이미지는 1254x1254 무손실 WebP(약 2MB) 원본이다.
 * Cloudinary는 `.../image/upload/{변환}/{public_id}` 형태로 변환 구간을 받으므로,
 * 표시 용도에 맞는 폭을 URL에 실어 서버 쪽에서 리샘플링된 이미지를 받는다.
 *
 * 변환을 붙일 수 없는 입력(Cloudinary가 아닌 URL, null, 빈 문자열, 이미 변환이 붙은 URL,
 * require()로 불러온 로컬 에셋)은 손대지 않고 그대로 돌려준다.
 */

/** `https://res.cloudinary.com/{cloud}/image/upload/` 와 그 뒤의 나머지 경로를 분리한다. */
const CLOUDINARY_UPLOAD_PATTERN = /^(https?:\/\/res\.cloudinary\.com\/[^/]+\/image\/upload\/)(.+)$/i;

/**
 * Cloudinary 변환 파라미터의 키 목록.
 * 변환 세그먼트는 `w_400,q_auto,f_auto`처럼 `키_값`을 쉼표로 이어 붙인 형태이므로,
 * 모든 토큰의 키가 이 목록에 있을 때만 변환 세그먼트로 판정한다.
 * (`my_folder` 같은 폴더명이나 `v1787467863` 같은 버전 세그먼트를 변환으로 오인하지 않기 위함)
 */
const TRANSFORM_KEYS = [
  'a', 'ar', 'b', 'bo', 'c', 'co', 'd', 'dpr', 'du', 'e', 'eo', 'f', 'fl',
  'g', 'h', 'if', 'l', 'o', 'pg', 'q', 'r', 'so', 't', 'u', 'w', 'x', 'y', 'z',
];

/**
 * 화면 용도별 변환 프리셋.
 *
 * 폭은 "실제 렌더링 폭(dp) x 기기 픽셀 밀도"를 넘어서는 가장 작은 값으로 골랐다.
 * 계산에 쓴 기준 화면은 폭 390dp(iPhone 14 급), 밀도는 최대 3배이다.
 *
 * - thumb(400): 마이페이지 최근 획득 카드 타일. 타일 폭이 약 96dp이므로 밀도 3배에서 288px.
 * - card(800): 도감 그리드 카드 썸네일. 타일 폭이 약 168dp라 밀도 3배에서 504px이고,
 *   768dp 태블릿에서는 48% 폭인 355dp가 밀도 2배에서 710px까지 커지므로 800으로 잡았다.
 * - hero(1080): 카드 상세 히어로와 이벤트 배너. 전체 폭(350~390dp)이 밀도 3배에서
 *   1050~1170px이며, 히어로는 어두운 오버레이가 덮이므로 1080에서 화질 손실이 보이지 않는다.
 *
 * 실측 용량(카드 원본 1254x1254 기준): 원본 2,054,084B / w_400 34,630B /
 * w_800 128,514B / w_1080 234,028B.
 */
export const IMAGE_PRESETS = {
  thumb: 'w_400,q_auto,f_auto',
  card: 'w_800,q_auto,f_auto',
  hero: 'w_1080,q_auto:good,f_auto',
};

const DEFAULT_PRESET = 'card';

/** 경로 첫 세그먼트가 Cloudinary 변환 구간인지 판정한다. */
function isTransformSegment(segment) {
  if (!segment || !segment.includes('_')) return false;
  return segment
    .split(',')
    .every((token) => TRANSFORM_KEYS.includes(token.split('_')[0]));
}

/**
 * Cloudinary URL에 용도별 변환 파라미터를 삽입한다.
 *
 * @param {string|number|object|null|undefined} source 원본 URL 또는 require() 에셋
 * @param {'thumb'|'card'|'hero'} [preset] 표시 용도 (알 수 없는 값이면 card)
 * @returns {string|number|object|null|undefined} 변환이 적용된 URL, 또는 손대지 않은 원본
 */
export function optimizeImageUrl(source, preset = DEFAULT_PRESET) {
  // require() 에셋(숫자·객체), null, undefined는 그대로 통과시킨다.
  if (typeof source !== 'string') return source;

  const match = CLOUDINARY_UPLOAD_PATTERN.exec(source.trim());
  if (!match) return source;

  const [, uploadBase, resourcePath] = match;
  // 이미 변환이 붙어 있으면 덧붙이지 않는다.
  if (isTransformSegment(resourcePath.split('/')[0])) return source;

  const transform = IMAGE_PRESETS[preset] ?? IMAGE_PRESETS[DEFAULT_PRESET];
  return `${uploadBase}${transform}/${resourcePath}`;
}

export default optimizeImageUrl;
