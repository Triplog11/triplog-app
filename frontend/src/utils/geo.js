import { filterProvinceRegions } from './provinces';

/**
 * 역지오코딩 결과 → TripLog 표준 시·도 이름 매핑 (KR/EN 토큰 모두 대응)
 */
const REGION_TOKENS = [
  { canonical: '서울특별시', tokens: ['서울', 'seoul'] },
  { canonical: '부산광역시', tokens: ['부산', 'busan'] },
  { canonical: '대구광역시', tokens: ['대구', 'daegu'] },
  { canonical: '인천광역시', tokens: ['인천', 'incheon'] },
  { canonical: '광주광역시', tokens: ['광주', 'gwangju'] },
  { canonical: '대전광역시', tokens: ['대전', 'daejeon'] },
  { canonical: '울산광역시', tokens: ['울산', 'ulsan'] },
  { canonical: '세종특별자치시', tokens: ['세종', 'sejong'] },
  { canonical: '경기도', tokens: ['경기', 'gyeonggi'] },
  { canonical: '강원특별자치도', tokens: ['강원', 'gangwon'] },
  { canonical: '충청북도', tokens: ['충청북', '충북', 'chungcheongbuk', 'north chungcheong'] },
  { canonical: '충청남도', tokens: ['충청남', '충남', 'chungcheongnam', 'south chungcheong'] },
  { canonical: '전라북도', tokens: ['전라북', '전북', 'jeollabuk', 'jeonbuk', 'north jeolla'] },
  { canonical: '전라남도', tokens: ['전라남', '전남', 'jeollanam', 'south jeolla'] },
  { canonical: '경상북도', tokens: ['경상북', '경북', 'gyeongsangbuk', 'north gyeongsang'] },
  { canonical: '경상남도', tokens: ['경상남', '경남', 'gyeongsangnam', 'south gyeongsang'] },
  { canonical: '제주특별자치도', tokens: ['제주', 'jeju'] },
];

/**
 * expo-location reverseGeocodeAsync 결과에서 표준 시·도 이름 추출.
 * @param place { region?, city?, subregion?, district? }
 * @returns canonical name 또는 null
 */
/**
 * 위치 칩용 상세 라벨 — "대전 유성구", "수원시 팔달구"처럼 시·도 + 구/동 단위까지 표시.
 */
export function formatPlaceLabel(place, canonicalRegion) {
  if (!place) return canonicalRegion || null;

  const shorten = (name) =>
    (name || '')
      .replace(/(특별자치시|특별자치도|특별시|광역시)$/, '')
      .trim();

  const regionPart = shorten(place.region) || shorten(canonicalRegion);
  const detailCandidates = [place.district, place.city, place.subregion, place.street]
    .map((s) => (s || '').trim())
    .filter((s) => s && s !== place.region && shorten(s) !== regionPart);

  const detail = detailCandidates.slice(0, 2).join(' ');
  const label = [regionPart, detail].filter(Boolean).join(' ');
  return label || canonicalRegion || null;
}

export function resolveRegionName(place) {
  if (!place) return null;
  const haystack = [place.region, place.city, place.subregion, place.district]
    .filter(Boolean)
    .join(' ')
    .toLowerCase();
  if (!haystack) return null;

  const match = REGION_TOKENS.find((entry) =>
    entry.tokens.some((token) => haystack.includes(token))
  );
  return match ? match.canonical : null;
}

const EARTH_RADIUS_M = 6371000;

/**
 * 두 좌표 사이 거리(m) — Haversine.
 * @param from {lat, lng}
 * @param to {lat, lng}
 */
export function distanceInMeters(from, to) {
  const toRad = (deg) => (deg * Math.PI) / 180;
  const dLat = toRad(to.lat - from.lat);
  const dLng = toRad(to.lng - from.lng);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(from.lat)) * Math.cos(toRad(to.lat)) * Math.sin(dLng / 2) ** 2;
  return Math.round(EARTH_RADIUS_M * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
}

/** 거리 표시 — 좌표를 모르면 `--m` (DESIGN.md §14) */
export function formatDistance(meters) {
  if (meters == null) return '--m';
  if (meters < 1000) return `${meters}m`;
  return `${(meters / 1000).toFixed(1)}km`;
}

/** 랜드마크 DTO에서 좌표 추출 — 백엔드가 아직 위경도를 내려주지 않아 대부분 null */
export function getLandmarkCoords(landmark) {
  const lat = landmark?.latitude ?? landmark?.lat;
  const lng = landmark?.longitude ?? landmark?.lng;
  if (typeof lat !== 'number' || typeof lng !== 'number') return null;
  return { lat, lng };
}

/* ── 현재 위치 → 시·군·구 매칭 ─────────────────────────────── */

const PROVINCE_SUFFIX = /(특별자치시|특별자치도|특별시|광역시|도)$/;
const DISTRICT_SUFFIX = /(시|군|구)$/;

/** 행정구역 이름 정규화 — 공백을 없애고 소문자로 통일한다(영문 표기도 같은 규칙). */
function normalizeAdminName(name) {
  return String(name ?? '').replace(/\s+/g, '').toLowerCase();
}

/** 붙여 쓴 지역 이름을 시·군·구 단위로 나눈다. '성남시분당구' → ['성남시', '분당구'] */
function splitAdminTokens(normalized) {
  return normalized.match(/.+?[시군구]/g) ?? [normalized];
}

/**
 * 지역 이름에서 비교용 키를 만든다.
 * 앞에 붙은 시·도 이름은 떼어 내어, '대전광역시 유성구'와 '유성구'가 같은 값으로 비교되게 한다.
 */
function regionMatchKeys(regionName) {
  const raw = String(regionName ?? '').trim();
  const words = raw.split(/\s+/).filter(Boolean);
  const hasProvincePrefix = words.length > 1 && PROVINCE_SUFFIX.test(words[0]);
  const core = normalizeAdminName((hasProvincePrefix ? words.slice(1) : words).join(''));
  return { full: normalizeAdminName(raw), core, tokens: splitAdminTokens(core) };
}

/**
 * 후보 이름과 지역 이름의 일치 정도.
 * 3 = 이름이 그대로 일치, 2 = 접미사(시·군·구)를 뗀 형태가 일치, 0 = 불일치.
 */
function regionMatchTier(candidate, keys) {
  if (candidate.value === keys.full || candidate.value === keys.core) return 3;
  if (keys.tokens.includes(candidate.value)) return 3;
  if (candidate.stem.length < 2) return 0;
  const stemMatched = keys.tokens.some((token) => token.replace(DISTRICT_SUFFIX, '') === candidate.stem);
  return stemMatched ? 2 : 0;
}

/** 후보 이름 하나로 지역을 찾는다. 같은 순위의 지역이 둘 이상이면 확정하지 않고 null을 돌려준다. */
function findRegionByName(candidateName, regions) {
  const value = normalizeAdminName(candidateName);
  if (value.length < 2) return null;
  const candidate = { value, stem: value.replace(DISTRICT_SUFFIX, '') };

  const best = regions.reduce(
    (acc, region) => {
      const tier = regionMatchTier(candidate, regionMatchKeys(region.regionName));
      if (tier === 0 || tier < acc.tier) return acc;
      if (tier > acc.tier) return { tier, matches: [region] };
      return { tier: acc.tier, matches: [...acc.matches, region] };
    },
    { tier: 0, matches: [] },
  );
  return best.matches.length === 1 ? best.matches[0] : null;
}

/**
 * 역지오코딩 결과를 전국 지역 목록의 시·군·구와 맞춘다.
 *
 * 표기가 서로 달라도 찾을 수 있도록 다음 순서로 확인한다.
 *  1) resolveRegionName으로 시·도를 먼저 정하고, 그 시·도의 지역만 후보로 남긴다(같은 이름의 '중구' 구분).
 *  2) 상세 주소 필드(district → subregion → city → name) 순서로 이름을 대조한다.
 *  3) 이름이 그대로 일치하지 않으면 접미사(시·군·구)를 뗀 형태로 다시 대조한다.
 * 같은 순위로 여러 지역이 걸리면 그 후보는 건너뛰어, 엉뚱한 지역으로 인증되지 않게 한다.
 * 기기 언어가 영문이면 시·군·구 이름을 대조할 수 없으므로 null을 돌려주고, 호출부는 지역 직접 선택으로 안내한다.
 *
 * @param place expo-location reverseGeocodeAsync 결과 한 건
 * @param regions fetchNationwideMap()이 돌려준 regions 배열
 * @returns 매칭된 지역 객체 또는 null
 */
export function matchRegionByPlace(place, regions) {
  const list = Array.isArray(regions) ? regions : [];
  if (!place || list.length === 0) return null;

  const province = resolveRegionName(place);
  const scoped = province ? filterProvinceRegions(list, province) : [];
  const pool = scoped.length > 0 ? scoped : list;

  const candidates = [place.district, place.subregion, place.city, place.name];
  const matched = candidates.reduce(
    (found, candidate) => found ?? findRegionByName(candidate, pool),
    null,
  );
  if (matched) return matched;

  // 시·군·구 이름을 찾지 못해도 해당 시·도에 지역이 하나뿐이면(세종특별자치시) 그 지역으로 확정한다
  return province && pool.length === 1 ? pool[0] : null;
}

/**
 * 인증 후보 랜드마크를 정렬한다 — 인증 플로우의 유일한 정렬 지점.
 *
 * 백엔드가 랜드마크 좌표를 내려주지 않는 동안에는 이름순으로 정렬하고,
 * 좌표(latitude/longitude)가 내려오기 시작하면 아래 계산이 그대로 거리순 정렬로 동작한다.
 * 확장 지점: 반경 판정(예: 100m 이내만 노출)은 이 함수가 붙여 준 distanceM을 호출부에서 걸러 쓰면 된다.
 *
 * @param landmarks 랜드마크 배열 (원본은 변경하지 않는다)
 * @param origin 현재 위치 {lat, lng} — 없으면 거리 계산을 건너뛴다
 * @returns distanceM이 채워진 새 배열
 */
export function sortLandmarksByProximity(landmarks, origin) {
  const measured = (landmarks ?? []).map((landmark) => {
    const coords = getLandmarkCoords(landmark);
    return { ...landmark, distanceM: origin && coords ? distanceInMeters(origin, coords) : null };
  });
  return measured.sort(compareByProximity);
}

/** 거리를 아는 랜드마크가 앞, 나머지는 이름순 */
function compareByProximity(a, b) {
  if (a.distanceM != null && b.distanceM != null) return a.distanceM - b.distanceM;
  if (a.distanceM != null) return -1;
  if (b.distanceM != null) return 1;
  return String(a.landmarkName ?? '').localeCompare(String(b.landmarkName ?? ''), 'ko');
}
