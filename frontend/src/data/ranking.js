/**
 * 랭킹 티어 메타데이터.
 * 랭킹 목록/내 순위는 /stats/rankings API에서 받는다 (목데이터 제거됨).
 */

/** 티어 색상 — 등급 시각화용 카테고리 색 (도감 등급 팔레트와 같은 성격) */
export const TIER_CONFIG = {
  Bronze: { label: '브론즈', color: '#B87333', soft: '#F7EDE3' },
  Silver: { label: '실버', color: '#7C8B9A', soft: '#EEF1F4' },
  Gold: { label: '골드', color: '#E8B84A', soft: '#FDF4DE' },
  Platinum: { label: '플래티넘', color: '#22B8CF', soft: '#E1F6F9' },
  Diamond: { label: '다이아', color: '#3B82F6', soft: '#E4EEFE' },
  Master: { label: '마스터', color: '#7C3AED', soft: '#EDE9FE' },
  Grandmaster: { label: '그랜드마스터', color: '#F76707', soft: '#FEEBDC' },
};

/** 백엔드 tier 문자열(대소문자 무관)을 TIER_CONFIG 항목으로 정규화 */
export function tierOf(tier) {
  if (!tier) return TIER_CONFIG.Bronze;
  const key = tier.charAt(0).toUpperCase() + tier.slice(1).toLowerCase();
  return TIER_CONFIG[key] ?? TIER_CONFIG.Bronze;
}
