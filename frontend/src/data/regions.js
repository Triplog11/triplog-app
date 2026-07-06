/**
 * 대한민국 17개 시·도 더미 데이터 — 프로토타입 js/regions.js, js/region-detail.js 포팅.
 * 백엔드 연동 전까지 홈 지도/지역 상세의 데이터 소스.
 */
const IMG = {
  gyeongbok: 'https://images.unsplash.com/photo-1538485049741-fc4ae4a8b63e?w=400&h=400&fit=crop',
  haewoondae: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=400&h=400&fit=crop',
  gwangan: 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=400&h=400&fit=crop',
  namsan: 'https://images.unsplash.com/photo-1517154428173-894dca833cd7?w=400&h=400&fit=crop',
};

export const REGIONS = [
  { name: '부산광역시', shortName: '부산', collected: 2, total: 4, groups: ['metro-city'] },
  { name: '서울특별시', shortName: '서울', collected: 12, total: 45, groups: ['metro'] },
  { name: '대전광역시', shortName: '대전', collected: 4, total: 8, groups: ['metro-city'] },
  { name: '경기도', shortName: '경기', collected: 143, total: 1000, groups: ['metro'] },
  { name: '강원특별자치도', shortName: '강원', collected: 18, total: 120, groups: ['other'] },
  { name: '전라남도', shortName: '전라남', collected: 14, total: 100, groups: ['other'] },
  { name: '인천광역시', shortName: '인천', collected: 7, total: 28, groups: ['metro', 'metro-city'] },
  { name: '광주광역시', shortName: '광주', collected: 3, total: 18, groups: ['metro-city'] },
  { name: '대구광역시', shortName: '대구', collected: 2, total: 24, groups: ['metro-city'] },
  { name: '울산광역시', shortName: '울산', collected: 1, total: 16, groups: ['metro-city'] },
  { name: '세종특별자치시', shortName: '세종', collected: 2, total: 10, groups: ['other'] },
  { name: '충청북도', shortName: '충청북', collected: 9, total: 80, groups: ['other'] },
  { name: '충청남도', shortName: '충청남', collected: 11, total: 90, groups: ['other'] },
  { name: '전라북도', shortName: '전라북', collected: 6, total: 70, groups: ['other'] },
  { name: '경상북도', shortName: '경상북', collected: 10, total: 110, groups: ['other'] },
  { name: '경상남도', shortName: '경상남', collected: 8, total: 95, groups: ['other'] },
  { name: '제주특별자치도', shortName: '제주', collected: 5, total: 40, groups: ['other'] },
];

const REGION_DETAIL = {
  부산광역시: {
    area: '769.94km²',
    population: '약 334만 명',
    districts: '15개 구',
    summary:
      '대한민국 제2의 도시이자 동남권 거점 항구 도시예요. 해운대·광안리·자갈치 등 바다와 도시가 어우러진 매력적인 관광지입니다.',
    homepage: 'https://www.busan.go.kr',
    meta: '769.94km² | 약 334만 명 | 15개 구',
    date: '2026년 3월 24일',
    landmarks: [
      { name: '해운대', addr: '부산광역시 해운대구 우동', img: IMG.haewoondae, visited: true },
      { name: '광안리', addr: '부산광역시 수영구 광안동', img: IMG.gwangan, visited: true },
      { name: '???', addr: '부산광역시 ...', visited: false },
      { name: '???', addr: '부산광역시 ...', visited: false },
    ],
  },
  서울특별시: {
    area: '605.21km²',
    population: '약 940만 명',
    districts: '25개 구',
    summary:
      '대한민국의 수도이자 정치·경제·문화의 중심지예요. 궁궐·한강·전통과 현대가 공존하는 도시입니다.',
    homepage: 'https://www.seoul.go.kr',
    meta: '605.21km² | 약 940만 명 | 25개 구',
    date: '2026년 3월 24일',
    landmarks: [
      { name: '경복궁', addr: '서울특별시 종로구 사직로', img: IMG.gyeongbok, visited: true },
      { name: '남산타워', addr: '서울특별시 용산구 남산공원길', img: IMG.namsan, visited: true },
      { name: '???', addr: '서울특별시 ...', visited: false },
      { name: '???', addr: '서울특별시 ...', visited: false },
      { name: '???', addr: '서울특별시 ...', visited: false },
      { name: '???', addr: '서울특별시 ...', visited: false },
    ],
  },
  경기도: {
    area: '10,184km²',
    population: '약 1,350만 명',
    districts: '31개 시군',
    summary:
      '수도권을 둘러싼 최대 인구·면적의 도예요. 수원화성·한강·신도시와 자연이 함께하는 다채로운 탐험지입니다.',
    homepage: 'https://www.gg.go.kr',
    meta: '10,184km² | 약 1,350만 명 | 31개 시군',
    date: '2026년 3월 20일',
    landmarks: [
      { name: '수원화성', addr: '경기도 수원시 팔달구', img: IMG.gyeongbok, visited: true },
      { name: '???', addr: '경기도 ...', visited: false },
      { name: '???', addr: '경기도 ...', visited: false },
    ],
  },
};

export function findRegion(name) {
  return REGIONS.find((r) => r.name === name) || null;
}

/** 상세 데이터가 없는 지역은 수집 수 기반 더미를 생성 (프로토타입 getDetail 동일 로직) */
export function getRegionDetail(region) {
  if (REGION_DETAIL[region.name]) return REGION_DETAIL[region.name];

  const landmarks = Array.from({ length: Math.min(region.total, 9) }, (_, i) => ({
    name: i < region.collected ? `랜드마크 ${i + 1}` : '???',
    addr: `${region.name} ...`,
    img: i < region.collected ? IMG.gyeongbok : undefined,
    visited: i < region.collected,
  }));

  return {
    area: '—',
    population: '—',
    districts: region.shortName || region.name,
    summary: `${region.name}의 대표 랜드마크를 방문 인증하고 카드를 모아보세요.`,
    homepage: null,
    meta: `${region.name} 탐험 지역`,
    date: '2026년 3월 24일',
    landmarks,
  };
}

export function getNationalStats(regions = REGIONS) {
  const collected = regions.reduce((acc, r) => acc + r.collected, 0);
  const total = regions.reduce((acc, r) => acc + r.total, 0);
  return {
    collected,
    total,
    percent: total > 0 ? Math.round((collected / total) * 100) : 0,
  };
}
