/**
 * 주변 랜드마크 목데이터 — 관광 API(backend feature/56) 연동 전 임시.
 * 좌표는 실제 위치를 넣어 두어, GPS 거리 계산은 실제로 동작하도록 한다.
 */
export const NEARBY_LANDMARKS = [
  { id: 101, name: '국립중앙과학관', region: '대전 유성구', grade: 'Rare', lat: 36.3757, lng: 127.3767, radiusM: 500, point: 80, xp: 40, visited: false },
  { id: 102, name: '대전시립미술관', region: '대전 서구', grade: 'Common', lat: 36.3684, lng: 127.3877, radiusM: 500, point: 50, xp: 25, visited: false },
  { id: 103, name: '한밭수목원', region: '대전 서구', grade: 'Rare', lat: 36.3676, lng: 127.3884, radiusM: 500, point: 70, xp: 35, visited: false },
  { id: 104, name: '엑스포과학공원', region: '대전 유성구', grade: 'Epic', lat: 36.3742, lng: 127.3863, radiusM: 500, point: 120, xp: 60, visited: false },
  { id: 105, name: '성심당 본점', region: '대전 중구', grade: 'Epic', lat: 36.3277, lng: 127.4278, radiusM: 300, point: 120, xp: 60, visited: false },
  { id: 106, name: '장태산 자연휴양림', region: '대전 서구', grade: 'Legendary', lat: 36.2286, lng: 127.3313, radiusM: 800, point: 200, xp: 100, visited: false },
];

const EARTH_RADIUS_M = 6371000;

/** 두 좌표 사이 거리(m) — Haversine */
export function distanceInMeters(from, to) {
  const toRad = (deg) => (deg * Math.PI) / 180;
  const dLat = toRad(to.lat - from.lat);
  const dLng = toRad(to.lng - from.lng);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(from.lat)) * Math.cos(toRad(to.lat)) * Math.sin(dLng / 2) ** 2;
  return Math.round(EARTH_RADIUS_M * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
}

export function formatDistance(meters) {
  if (meters == null) return '--m';
  if (meters < 1000) return `${meters}m`;
  return `${(meters / 1000).toFixed(1)}km`;
}
