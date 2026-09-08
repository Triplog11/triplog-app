import { matchRegionByPlace, sortLandmarksByProximity } from '../geo';

/** fetchNationwideMap()의 regions 형태 — 표기 방식이 섞여 있어도 찾을 수 있어야 한다 */
const REGIONS = [
  { regionId: 1, regionName: '유성구', legalRegionCode: '3020000000' },
  { regionId: 2, regionName: '중구', legalRegionCode: '1114000000' },
  { regionId: 3, regionName: '중구', legalRegionCode: '2611000000' },
  { regionId: 4, regionName: '중구', legalRegionCode: '3011000000' },
  { regionId: 5, regionName: '성남시 분당구', legalRegionCode: '4113500000' },
  { regionId: 6, regionName: '수원시 팔달구', legalRegionCode: '4111500000' },
  { regionId: 7, regionName: '수원시 장안구', legalRegionCode: '4111100000' },
  { regionId: 8, regionName: '세종특별자치시', legalRegionCode: '3611000000' },
  { regionId: 9, regionName: '대전광역시 서구', legalRegionCode: '3017000000' },
];

describe('matchRegionByPlace', () => {
  it('시·군·구 이름이 그대로 일치하면 해당 지역을 찾는다', () => {
    const place = { region: '대전광역시', city: '대전광역시', district: '유성구' };
    expect(matchRegionByPlace(place, REGIONS).regionId).toBe(1);
  });

  it('지역 이름에 시·도가 붙어 있어도(대전광역시 서구) 시·군·구만으로 찾는다', () => {
    const place = { region: '대전광역시', district: '서구' };
    expect(matchRegionByPlace(place, REGIONS).regionId).toBe(9);
  });

  it('접미사가 빠진 이름(유성)도 접미사를 뗀 형태로 대조해 찾는다', () => {
    const place = { region: '대전광역시', district: '유성' };
    expect(matchRegionByPlace(place, REGIONS).regionId).toBe(1);
  });

  it('이름이 같은 중구는 시·도로 구분한다', () => {
    expect(matchRegionByPlace({ region: '서울특별시', district: '중구' }, REGIONS).regionId).toBe(2);
    expect(matchRegionByPlace({ region: '부산광역시', district: '중구' }, REGIONS).regionId).toBe(3);
    expect(matchRegionByPlace({ region: '대전광역시', district: '중구' }, REGIONS).regionId).toBe(4);
  });

  it('시와 구가 함께 있는 이름(성남시 분당구)은 구 이름으로 찾는다', () => {
    const place = { region: '경기도', subregion: '성남시', district: '분당구' };
    expect(matchRegionByPlace(place, REGIONS).regionId).toBe(5);
  });

  it('후보가 여러 지역에 걸리면(수원시) 확정하지 않고 null을 돌려준다', () => {
    const place = { region: '경기도', city: '수원시' };
    expect(matchRegionByPlace(place, REGIONS)).toBeNull();
  });

  it('구 이름까지 있으면 같은 수원시 안에서도 정확히 찾는다', () => {
    const place = { region: '경기도', city: '수원시', district: '팔달구' };
    expect(matchRegionByPlace(place, REGIONS).regionId).toBe(6);
  });

  it('시·도에 지역이 하나뿐이면(세종) 영문 표기로도 찾는다', () => {
    expect(matchRegionByPlace({ region: '세종특별자치시' }, REGIONS).regionId).toBe(8);
    expect(matchRegionByPlace({ region: 'Sejong-si', city: 'Sejong' }, REGIONS).regionId).toBe(8);
  });

  it('시·도를 알 수 없어도 이름이 전국에서 유일하면 찾는다', () => {
    expect(matchRegionByPlace({ district: '유성구' }, REGIONS).regionId).toBe(1);
  });

  it('기기 언어가 영문이라 시·군·구를 대조할 수 없으면 null을 돌려준다', () => {
    const place = { region: 'Daejeon', city: 'Yuseong-gu' };
    expect(matchRegionByPlace(place, REGIONS)).toBeNull();
  });

  it('위치 정보나 지역 목록이 없으면 null을 돌려준다', () => {
    expect(matchRegionByPlace(null, REGIONS)).toBeNull();
    expect(matchRegionByPlace({ region: '대전광역시' }, [])).toBeNull();
    expect(matchRegionByPlace({ region: '대전광역시' }, undefined)).toBeNull();
  });
});

describe('sortLandmarksByProximity', () => {
  const NO_COORDS = [
    { landmarkId: 1, landmarkName: '덕수궁' },
    { landmarkId: 2, landmarkName: '경복궁' },
    { landmarkId: 3, landmarkName: '남산서울타워' },
  ];

  it('좌표가 없으면 이름순으로 정렬하고 distanceM은 null로 둔다', () => {
    const sorted = sortLandmarksByProximity(NO_COORDS, { lat: 37.5665, lng: 126.978 });
    expect(sorted.map((l) => l.landmarkName)).toEqual(['경복궁', '남산서울타워', '덕수궁']);
    expect(sorted.every((l) => l.distanceM === null)).toBe(true);
  });

  it('좌표가 있으면 현재 위치에서 가까운 순으로 정렬하고 거리를 채운다', () => {
    const origin = { lat: 37.566535, lng: 126.977969 };
    const landmarks = [
      { landmarkId: 1, landmarkName: '경복궁', latitude: 37.579617, longitude: 126.977041 },
      { landmarkId: 2, landmarkName: '덕수궁', latitude: 37.565804, longitude: 126.975144 },
    ];
    const sorted = sortLandmarksByProximity(landmarks, origin);
    expect(sorted.map((l) => l.landmarkId)).toEqual([2, 1]);
    expect(sorted[0].distanceM).toBeLessThan(sorted[1].distanceM);
  });

  it('현재 위치를 모르면 거리 계산을 건너뛴다', () => {
    const landmarks = [{ landmarkId: 1, landmarkName: '경복궁', latitude: 37.5796, longitude: 126.977 }];
    expect(sortLandmarksByProximity(landmarks, null)[0].distanceM).toBeNull();
  });

  it('원본 배열과 원소를 변경하지 않는다', () => {
    const original = [...NO_COORDS];
    sortLandmarksByProximity(NO_COORDS, null);
    expect(NO_COORDS).toEqual(original);
    expect(NO_COORDS[0]).not.toHaveProperty('distanceM');
  });

  it('목록이 비어 있어도 빈 배열을 돌려준다', () => {
    expect(sortLandmarksByProximity(undefined, null)).toEqual([]);
  });
});
