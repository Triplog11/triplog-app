/**
 * 마이페이지 목데이터 — 해당 백엔드 API(랭킹/카드/여행기록/통계)가 아직 없어
 * 임시로 사용한다. API가 열리면 각 섹션별로 실데이터로 교체할 것.
 */

export const MOCK_RANK = {
  tierLabel: 'Gold Rank',
  monthlyRank: 23,
  totalScore: 8450,
};

export const MOCK_STATS = {
  visitedRegions: 7,
  certifiedLandmarks: 24,
  collectedCards: 7,
};

export const MOCK_XP_MAX = 900;

export const MOCK_RECENT_CARDS = [
  { id: 1, name: '경복궁', region: '종로구', grade: 'Epic' },
  { id: 2, name: '남산타워', region: '용산구', grade: 'Rare' },
  { id: 3, name: '한강공원', region: '서울 영등포구', grade: 'Common' },
];

export const MOCK_TRAVEL_LOG = {
  date: '2026.07.12',
  totalCount: 3,
  entries: [
    {
      id: 1,
      place: '국립중앙과학관',
      region: '대전 유성구',
      cardGrade: 'S',
      review: null,
      photoCount: 0,
    },
    {
      id: 2,
      place: '국립중앙과학관',
      region: '대전 유성구',
      cardGrade: null,
      review: '아이들이 시시해할까 걱정했는데 생각보다 체험할 게 많아서 오랜만에 추억을 쌓았어요.',
      photoCount: 3,
    },
    {
      id: 3,
      place: '국립중앙과학관',
      region: '대전 유성구',
      cardGrade: null,
      review: '아이들이 시시해할까 걱정했는데 생각보다 체험할 게 많아서 오랜만에 추억쌓고 왔습니다.',
      photoCount: 2,
    },
  ],
};
