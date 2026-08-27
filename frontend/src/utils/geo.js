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
