/**
 * 알림 / 찜 / 여행 기록 / 인증 내역 목데이터 — 해당 API가 아직 없어 임시 사용.
 */

export const NOTIFICATIONS = [
  { id: 1, type: 'badge', title: '새 뱃지를 획득했어요', body: '"첫 발자국" 뱃지가 도감에 추가됐어요.', time: '방금 전', unread: true },
  { id: 2, type: 'card', title: '카드를 수집했어요', body: '국립중앙과학관 카드가 도감에 담겼어요.', time: '1시간 전', unread: true },
  { id: 3, type: 'ranking', title: '이번 주 랭킹이 갱신됐어요', body: '현재 23위예요. 지난주보다 4계단 올랐어요.', time: '어제', unread: false },
  { id: 4, type: 'event', title: '여름 시즌이 시작됐어요', body: '시즌 종료까지 12일 남았어요.', time: '3일 전', unread: false },
];

export const NOTIFICATION_ICONS = {
  badge: 'ribbon-outline',
  card: 'albums-outline',
  ranking: 'trophy-outline',
  event: 'megaphone-outline',
};

export const WISHLIST = [
  { id: 9, name: '북촌한옥마을', region: '서울 종로구', grade: 'Rare' },
  { id: 16, name: '수원화성', region: '경기 수원시', grade: 'Epic' },
  { id: 18, name: '성산일출봉', region: '제주 서귀포시', grade: 'Legendary' },
  { id: 12, name: '광안리해수욕장', region: '부산 수영구', grade: 'Common' },
];

export const TRAVEL_LOGS = [
  {
    id: 1,
    date: '2026.07.12',
    place: '국립중앙과학관',
    region: '대전 유성구',
    rating: 4,
    note: '아이들이 시시해할까 걱정했는데 생각보다 체험할 게 많아서 오랜만에 추억을 쌓았어요.',
    photoCount: 3,
  },
  {
    id: 2,
    date: '2026.07.05',
    place: '전주한옥마을',
    region: '전북 전주시',
    rating: 5,
    note: '한복 입고 골목 돌아다니는 재미가 쏠쏠했어요. 비빔밥도 완벽했습니다.',
    photoCount: 5,
  },
  {
    id: 3,
    date: '2026.06.28',
    place: '설악산 국립공원',
    region: '강원 속초시',
    rating: 5,
    note: '울산바위까지 오르는 길이 힘들었지만 정상에서 본 풍경이 모든 걸 보상해 줬어요.',
    photoCount: 8,
  },
];

export const VERIFY_HISTORY = [
  { id: 1, place: '국립중앙과학관', region: '대전 유성구', date: '2026.07.12 14:20', method: 'GPS', xp: 40, point: 80 },
  { id: 2, place: '전주한옥마을', region: '전북 전주시', date: '2026.07.05 11:05', method: 'GPS', xp: 60, point: 120 },
  { id: 3, place: '설악산 국립공원', region: '강원 속초시', date: '2026.06.28 09:40', method: 'GPS', xp: 100, point: 200 },
  { id: 4, place: '남산타워', region: '서울 용산구', date: '2026.06.10 19:12', method: 'GPS', xp: 35, point: 70 },
  { id: 5, place: '경복궁', region: '서울 종로구', date: '2026.06.10 13:30', method: 'GPS', xp: 60, point: 120 },
];
