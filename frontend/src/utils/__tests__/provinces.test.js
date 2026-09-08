import { buildProvinceStats, filterProvinceRegions, PROVINCE_CODES } from '../provinces';

describe('provinces utils', () => {
  const mockRegions = [
    { regionId: 1, regionName: '서울특별시 종로구', legalRegionCode: '1111000000', visited: true },
    { regionId: 2, regionName: '서울특별시 강남구', legalRegionCode: '1168000000', visited: false },
    { regionId: 3, regionName: '부산광역시 해운대구', legalRegionCode: '2635000000', visited: true },
    { regionId: 4, regionName: '경기도 수원시 팔달구', legalRegionCode: '4111500000', visited: true },
    { regionId: 5, regionName: '경기도 성남시 분당구', legalRegionCode: '4113500000', visited: false },
  ];

  describe('PROVINCE_CODES', () => {
    it('17개 시·도만 담는다 — 전북/강원의 신·구 명칭이 중복 항목으로 남지 않는다', () => {
      expect(Object.keys(PROVINCE_CODES)).toHaveLength(17);
      expect(PROVINCE_CODES).not.toHaveProperty('전북특별자치도');
      expect(PROVINCE_CODES).not.toHaveProperty('강원도');
    });

    it('개편 전후 코드를 한 항목에 함께 담는다', () => {
      expect(PROVINCE_CODES['전라북도']).toEqual(['52', '45']);
      expect(PROVINCE_CODES['강원특별자치도']).toEqual(['51', '42']);
    });
  });

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

    it('전북은 구 코드(45)와 신 코드(52)를 모두 같은 시·도로 묶는다', () => {
      const jeonbuk = filterProvinceRegions(
        [
          { regionName: '전라북도 전주시 완산구', legalRegionCode: '4511100000', visited: true },
          { regionName: '전북특별자치도 군산구', legalRegionCode: '5213000000', visited: false },
        ],
        '전라북도'
      );
      expect(jeonbuk).toHaveLength(2);
    });

    it('코드가 없으면 개편 전후 이름을 모두 인정해 접두 매칭한다', () => {
      const jeonbuk = filterProvinceRegions(
        [
          { regionName: '전북특별자치도 남원시', visited: true },
          { regionName: '전라남도 목포시', visited: false },
        ],
        '전라북도'
      );
      expect(jeonbuk).toHaveLength(1);
      expect(jeonbuk[0].regionName).toBe('전북특별자치도 남원시');
    });
  });

  describe('buildProvinceStats', () => {
    it('모든 시·도에 대해 visited / total 통계를 집계한다', () => {
      const stats = buildProvinceStats(mockRegions);

      expect(stats.length).toBe(17);
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

    it('전북의 신·구 코드 지역이 하나의 시·도로 합쳐진다', () => {
      const stats = buildProvinceStats([
        { regionName: '전라북도 전주시', legalRegionCode: '4511100000', visited: true },
        { regionName: '전북특별자치도 익산시', legalRegionCode: '5214000000', visited: false },
      ]);

      expect(stats.filter((s) => s.name.startsWith('전'))).toEqual([
        { name: '전라북도', collected: 1, total: 2 },
        { name: '전라남도', collected: 0, total: 0 },
      ]);
    });

    it('빈 regions 목록이 주어져도 모든 시·도에 대해 0으로 집계된다', () => {
      const stats = buildProvinceStats([]);
      expect(stats.length).toBe(17);
      stats.forEach((s) => {
        expect(s.collected).toBe(0);
        expect(s.total).toBe(0);
      });
    });
  });
});

describe('전남광주통합특별시(코드 12) 분리', () => {
  // 서버는 광주와 전남을 하나의 시·도(코드 12)로 내려주지만 지도는 둘을 따로 그린다.
  const merged = [
    { regionName: '전남광주통합특별시 동구', legalRegionCode: '12', legalDistrictCode: '210', visited: true },
    { regionName: '전남광주통합특별시 서구', legalRegionCode: '12', legalDistrictCode: '240', visited: false },
    { regionName: '전남광주통합특별시 남구', legalRegionCode: '12', legalDistrictCode: '270', visited: false },
    { regionName: '전남광주통합특별시 북구', legalRegionCode: '12', legalDistrictCode: '300', visited: false },
    { regionName: '전남광주통합특별시 광산구', legalRegionCode: '12', legalDistrictCode: '330', visited: false },
    { regionName: '전남광주통합특별시 목포시', legalRegionCode: '12', legalDistrictCode: '110', visited: true },
    { regionName: '전남광주통합특별시 여수시', legalRegionCode: '12', legalDistrictCode: '130', visited: true },
    { regionName: '전남광주통합특별시 담양군', legalRegionCode: '12', legalDistrictCode: '710', visited: false },
  ];

  it('광주광역시는 구역 코드가 자치구인 것만 가져온다', () => {
    const own = filterProvinceRegions(merged, '광주광역시');
    expect(own.map((r) => r.regionName)).toEqual([
      '전남광주통합특별시 동구',
      '전남광주통합특별시 서구',
      '전남광주통합특별시 남구',
      '전남광주통합특별시 북구',
      '전남광주통합특별시 광산구',
    ]);
  });

  it('전라남도는 자치구를 뺀 나머지를 가져온다', () => {
    const own = filterProvinceRegions(merged, '전라남도');
    expect(own.map((r) => r.regionName)).toEqual([
      '전남광주통합특별시 목포시',
      '전남광주통합특별시 여수시',
      '전남광주통합특별시 담양군',
    ]);
  });

  it('한 지역이 두 시·도에 동시에 속하지 않는다', () => {
    const gwangju = filterProvinceRegions(merged, '광주광역시');
    const jeonnam = filterProvinceRegions(merged, '전라남도');
    expect(gwangju.length + jeonnam.length).toBe(merged.length);
    expect(gwangju.some((g) => jeonnam.includes(g))).toBe(false);
  });

  it('통합 코드에서도 시·도별 방문 수가 따로 집계된다', () => {
    const stats = buildProvinceStats(merged);
    const gwangju = stats.find((s) => s.name === '광주광역시');
    const jeonnam = stats.find((s) => s.name === '전라남도');
    expect(gwangju).toEqual({ name: '광주광역시', collected: 1, total: 5 });
    expect(jeonnam).toEqual({ name: '전라남도', collected: 2, total: 3 });
  });
});
