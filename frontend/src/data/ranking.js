/**
 * 랭킹 목데이터 — 랭킹 API가 아직 없어 임시 사용.
 * 백엔드 Stats 도메인에 엔드포인트가 생기면 교체할 것.
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

export const SEASON = {
  name: '2026 여름 시즌',
  remainingDays: 12,
};

export const RANKINGS = [
  { rank: 1, nickname: '배낭여행러', level: 12, levelName: '전국 여행왕', tier: 'Grandmaster', score: 28500, cards: 48, regions: 17 },
  { rank: 2, nickname: '사진찍는민지', level: 10, levelName: '광역 탐험가', tier: 'Master', score: 22100, cards: 35, regions: 12 },
  { rank: 3, nickname: '주말여행러', level: 9, levelName: '광역 탐험가', tier: 'Diamond', score: 18750, cards: 28, regions: 10 },
  { rank: 4, nickname: '트립마스터', level: 8, levelName: '도시 탐험가', tier: 'Platinum', score: 14200, cards: 22, regions: 8 },
  { rank: 5, nickname: '여행하는수진', level: 7, levelName: '도시 탐험가', tier: 'Gold', score: 11800, cards: 19, regions: 7 },
  { rank: 6, nickname: '걷는여행자', level: 6, levelName: '로컬 여행자', tier: 'Gold', score: 10200, cards: 17, regions: 6 },
  { rank: 7, nickname: '대전탐험대', level: 6, levelName: '로컬 여행자', tier: 'Silver', score: 9100, cards: 14, regions: 5 },
  { rank: 8, nickname: '주말산책러', level: 5, levelName: '로컬 여행자', tier: 'Silver', score: 8800, cards: 12, regions: 5 },
];

/** 내 랭킹 — 로그인 유저 정보(nickname/level/tier)로 덮어써서 사용한다 */
export const MY_RANK = {
  rank: 23,
  score: 8450,
  cards: 15,
  regions: 7,
  levelName: '로컬 여행자',
};
