/**
 * 도감(수집) 목데이터 — 랜드마크/지역/카드 API가 아직 없어 임시 사용.
 * 관광 API(backend feature/56)가 열리면 실데이터로 교체할 것.
 * 구조는 프로토타입 2(lib/data.ts)를 따른다.
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

export const LANDMARK_CARDS = [
  { id: 1, name: '성심당 본점', region: '대전 중구', grade: 'Epic', obtained: true, date: '2026.03.15', description: '대전의 명물 빵집. 1956년 창업 이래 대전을 대표하는 베이커리로, 튀김소보로와 부추빵이 유명합니다.' },
  { id: 2, name: '한밭수목원', region: '대전 서구', grade: 'Rare', obtained: true, date: '2026.03.22', description: '도심 속 거대한 수목원으로 동원과 서원으로 나뉘어 있습니다. 도시 수목원 중 국내 최대 규모입니다.' },
  { id: 3, name: '엑스포과학공원', region: '대전 유성구', grade: 'Rare', obtained: true, date: '2026.04.01', description: '1993년 대전엑스포 부지에 조성된 과학 테마공원. 한빛탑이 랜드마크입니다.' },
  { id: 4, name: '유성온천', region: '대전 유성구', grade: 'Common', obtained: true, date: '2026.04.10', description: '라듐 성분이 포함된 약알칼리성 온천으로 유명한 대전의 온천 관광지입니다.' },
  { id: 5, name: '장태산 자연휴양림', region: '대전 서구', grade: 'Legendary', obtained: true, date: '2026.05.03', description: '메타세쿼이아 숲길로 유명한 자연휴양림. 하늘 높이 뻗은 나무 사이를 걷는 힐링 코스입니다.' },
  { id: 6, name: '대청호 오백리길', region: '대전 대덕구', grade: 'Rare', obtained: true, date: '2026.05.18', description: '대청호를 따라 이어지는 둘레길. 사계절 아름다운 호수 풍경을 감상할 수 있습니다.' },
  { id: 7, name: '경복궁', region: '서울 종로구', grade: 'Epic', obtained: true, date: '2026.06.10', description: '조선왕조 제일의 법궁. 600년 역사의 웅장한 궁궐 건축을 만날 수 있습니다.' },
  { id: 8, name: '남산타워', region: '서울 용산구', grade: 'Rare', obtained: true, date: '2026.06.10', description: '서울의 상징. 전망대에서 서울 시내 전경을 한눈에 담을 수 있습니다.' },
  { id: 9, name: '북촌한옥마을', region: '서울 종로구', grade: 'Rare', obtained: false, date: null, description: '전통 한옥이 밀집한 서울의 대표 한옥마을입니다.' },
  { id: 10, name: '해운대해수욕장', region: '부산 해운대구', grade: 'Epic', obtained: true, date: '2026.07.21', description: '대한민국 대표 해수욕장. 여름이면 전국에서 피서객이 모여듭니다.' },
  { id: 11, name: '감천문화마을', region: '부산 사하구', grade: 'Rare', obtained: true, date: '2026.07.21', description: '한국의 마추픽추라 불리는 계단식 마을. 알록달록한 집들이 장관입니다.' },
  { id: 12, name: '광안리해수욕장', region: '부산 수영구', grade: 'Common', obtained: false, date: null, description: '광안대교 야경으로 유명한 해수욕장입니다.' },
  { id: 13, name: '설악산 국립공원', region: '강원 속초시', grade: 'Legendary', obtained: true, date: '2026.06.28', description: '한국의 명산. 울산바위, 비선대 등 절경이 가득합니다.' },
  { id: 14, name: '경포대', region: '강원 강릉시', grade: 'Rare', obtained: false, date: null, description: '동해안 최고의 해변과 정자. 벚꽃 명소로도 유명합니다.' },
  { id: 15, name: '전주한옥마을', region: '전북 전주시', grade: 'Epic', obtained: true, date: '2026.07.05', description: '가장 한국적인 도시 전주. 한옥과 미식의 거리입니다.' },
  { id: 16, name: '수원화성', region: '경기 수원시', grade: 'Epic', obtained: false, date: null, description: '유네스코 세계문화유산. 정조의 꿈이 담긴 성곽 도시입니다.' },
  { id: 17, name: '순천만국가정원', region: '전남 순천시', grade: 'Rare', obtained: false, date: null, description: '대한민국 1호 국가정원. 갈대밭 습지가 장관입니다.' },
  { id: 18, name: '성산일출봉', region: '제주 서귀포시', grade: 'Legendary', obtained: false, date: null, description: '유네스코 세계자연유산. 제주 여행의 백미입니다.' },
];

export const PROVINCE_LANDMARK_DATA = {
  대전: { key: '대전', name: '대전광역시', region: '충청권', description: '과학도시 대전. 성심당, 엑스포과학공원 등 대전만의 특색 있는 명소들이 가득합니다.', cardIds: [1, 2, 3, 4, 5, 6] },
  서울: { key: '서울', name: '서울특별시', region: '수도권', description: '대한민국의 수도. 600년 역사의 고궁부터 현대적인 도심 문화까지 다채로운 매력의 도시입니다.', cardIds: [7, 8, 9] },
  부산: { key: '부산', name: '부산광역시', region: '경상권', description: '대한민국 제2의 도시이자 최대 항구도시. 해운대, 광안리 등 아름다운 해변이 유명합니다.', cardIds: [10, 11, 12] },
  강원: { key: '강원', name: '강원특별자치도', region: '강원권', description: '청정 자연의 보고. 설악산, 강릉 경포대 등 아름다운 자연경관이 펼쳐집니다.', cardIds: [13, 14] },
  전북: { key: '전북', name: '전북특별자치도', region: '전라권', description: '맛과 멋의 고장. 전주한옥마을에서 전통 문화와 미식을 함께 경험할 수 있습니다.', cardIds: [15] },
  경기: { key: '경기', name: '경기도', region: '수도권', description: '수도권의 허브. 수원화성, 남이섬 등 역사와 자연이 어우러진 명소들이 있습니다.', cardIds: [16] },
  전남: { key: '전남', name: '전라남도', region: '전라권', description: '남도의 예향. 순천만국가정원, 여수 밤바다 등 힐링 명소가 가득합니다.', cardIds: [17] },
  제주: { key: '제주', name: '제주특별자치도', region: '제주권', description: '대한민국 대표 휴양지. 화산이 빚은 신비로운 자연을 만날 수 있습니다.', cardIds: [18] },
};

export function getCardById(id) {
  return LANDMARK_CARDS.find((card) => card.id === id) ?? null;
}

export function getProvinceProgress(provinceKey) {
  const province = PROVINCE_LANDMARK_DATA[provinceKey];
  if (!province) return { collected: 0, total: 0, percent: 0 };
  const cards = province.cardIds.map(getCardById).filter(Boolean);
  const collected = cards.filter((card) => card.obtained).length;
  const total = cards.length;
  return { collected, total, percent: total ? Math.round((collected / total) * 100) : 0 };
}
