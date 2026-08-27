import { buildProvinceStats, filterProvinceRegions, PROVINCE_CODES } from '../provinces';

describe('provinces utils', () => {
  const mockRegions = [
    { regionId: 1, regionName: '서울특별시 종로구', legalRegionCode: '1111000000', visited: true },
    { regionId: 2, regionName: '서울특별시 강남구', legalRegionCode: '1168000000', visited: false },
    { regionId: 3, regionName: '부산광역시 해운대구', legalRegionCode: '2635000000', visited: true },
    { regionId: 4, regionName: '경기도 수원시 팔달구', legalRegionCode: '4111500000', visited: true },
    { regionId: 5, regionName: '경기도 성남시 분당구', legalRegionCode: '4113500000', visited: false },
  ];

  describe('filterProvinceRegions', () => {
    it('법정 시·도 코드(앞 2자리)로 해당 시·도의 지역을 필터링한다', () => {
      const seoulRegions = filterProvinceRegions(mockRegions, '서울특별시');
      expect(seoulRegions).toHaveLength(2);
      expect(seoulRegions.map((r) => r.regionName)).toEqual([
        '서울특별시 종로구',
        '서울특별시 강남구',
      ]);
    });

    it('코드가 없거나 일치하지 않아도 이름 접두로 폴백 필터링한다', () => {
      const gyeonggi = filterProvinceRegions(mockRegions, '경기도');
      expect(gyeonggi).toHaveLength(2);
    });

    it('해당하지 않는 시·도는 빈 배열을 반환한다', () => {
      const jeju = filterProvinceRegions(mockRegions, '제주특별자치도');
      expect(jeju).toEqual([]);
    });
  });

  describe('buildProvinceStats', () => {
    it('모든 시·도에 대해 visited / total 통계를 집계한다', () => {
      const stats = buildProvinceStats(mockRegions);
      
      expect(stats.length).toBe(Object.keys(PROVINCE_CODES).length);
      
      const seoul = stats.find((s) => s.name === '서울특별시');
      expect(seoul).toEqual({ name: '서울특별시', collected: 1, total: 2 });

      const busan = stats.find((s) => s.name === '부산광역시');
      expect(busan).toEqual({ name: '부산광역시', collected: 1, total: 1 });

      const gyeonggi = stats.find((s) => s.name === '경기도');
      expect(gyeonggi).toEqual({ name: '경기도', collected: 1, total: 2 });

      const daegu = stats.find((s) => s.name === '대구광역시');
      expect(daegu).toEqual({ name: '대구광역시', collected: 0, total: 0 });
    });

    it('빈 regions 목록이 주어져도 모든 시·도에 대해 0으로 집계된다', () => {
      const stats = buildProvinceStats([]);
      expect(stats.length).toBe(Object.keys(PROVINCE_CODES).length);
      stats.forEach((s) => {
        expect(s.collected).toBe(0);
        expect(s.total).toBe(0);
      });
    });
  });
});
