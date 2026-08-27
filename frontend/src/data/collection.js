/**
 * 도감(수집) 표시 상수 — 카드 등급 팔레트와 서버 CardTier 매핑.
 */

/**
 * 카드 등급(희귀도) 팔레트 — 수집 게임의 등급 구분용 카테고리 색.
 * 지도 카테고리 팔레트와 같은 성격의 데이터 시각화 색상(브랜드 원액센트 규칙 예외).
 */
export const GRADE_CONFIG = {
  Common: { label: '일반', stars: 1, color: '#6B7280', soft: '#F3F4F6', border: '#D1D5DB', description: '어디서나 만날 수 있는 친숙한 장소' },
  Rare: { label: '희귀', stars: 2, color: '#2563EB', soft: '#DBEAFE', border: '#93C5FD', description: '발걸음을 멈추게 하는 특별한 장소' },
  Epic: { label: '영웅', stars: 3, color: '#7C3AED', soft: '#EDE9FE', border: '#C4B5FD', description: '소수만이 방문하는 숨겨진 명소' },
  Legendary: { label: '전설', stars: 4, color: '#D97706', soft: '#FEF3C7', border: '#FCD34D', description: '일생에 한 번은 꼭 가야 할 전설적인 장소' },
};

export const GRADE_ORDER = ['Common', 'Rare', 'Epic', 'Legendary'];

/** 서버 CardTier enum(COMMON/RARE/EPIC/LEGENDARY) → GRADE_CONFIG 키 */
const TIER_TO_GRADE = {
  COMMON: 'Common',
  RARE: 'Rare',
  EPIC: 'Epic',
  LEGENDARY: 'Legendary',
};

/**
 * 서버 cardTier를 GRADE_CONFIG 키로 변환한다. 알 수 없는 값이면 null.
 * @param {string|null|undefined} tier
 * @returns {'Common'|'Rare'|'Epic'|'Legendary'|null}
 */
export function tierToGrade(tier) {
  if (tier == null) return null;
  return TIER_TO_GRADE[String(tier).toUpperCase()] ?? null;
}

/** ISO 날짜 문자열을 'YYYY.MM.DD'로 (실패 시 원본) */
export function formatAcquiredDate(iso) {
  if (!iso) return null;
  return typeof iso === 'string' && iso.length >= 10 ? iso.slice(0, 10).replace(/-/g, '.') : iso;
}

/**
 * GET /landmarks/me 항목을 카드 UI 모델로 변환한다.
 * @param {{cardId, landmarkId, landmarkName, cardName, cardTier, cardUrl, acquiredAt}} item
 */
export function toCardModel(item) {
  return {
    id: item.cardId ?? item.landmarkId,
    landmarkId: item.landmarkId,
    name: item.cardName ?? item.landmarkName,
    landmarkName: item.landmarkName,
    grade: tierToGrade(item.cardTier),
    imageUrl: item.cardUrl ?? null,
    obtained: true,
    date: formatAcquiredDate(item.acquiredAt),
    region: null,
  };
}
