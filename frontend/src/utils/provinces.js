/**
 * 시·도 이름 ↔ 법정 시·도 코드(legalRegionCode 앞 2자리) 유틸.
 * 강원/전북은 특별자치도 개편으로 코드가 바뀌어 신·구 코드를 한 항목에 함께 둔다.
 * 지도(KOREA_PROVINCES)가 인식하는 이름을 대표 이름으로 삼아 17개 시·도만 유지한다.
 */
export const PROVINCE_CODES = {
  서울특별시: ['11'],
  부산광역시: ['26'],
  대구광역시: ['27'],
  인천광역시: ['28'],
  // 광주와 전남은 통합 코드 '12'를 함께 쓰므로 아래 GWANGJU_DISTRICT_CODES 로 갈라낸다.
  광주광역시: ['12', '29'],
  대전광역시: ['30'],
  울산광역시: ['31'],
  세종특별자치시: ['36'],
  경기도: ['41'],
  강원특별자치도: ['51', '42'],
  충청북도: ['43'],
  충청남도: ['44'],
  전라북도: ['52', '45'],
  전라남도: ['12', '46'],
  경상북도: ['47'],
  경상남도: ['48'],
  제주특별자치도: ['50'],
};

/**
 * 개편 전후로 명칭이 달라진 시·도의 이름 목록.
 * 코드 매칭이 실패했을 때 이름 접두 폴백이 옛 이름과 새 이름을 모두 인정하도록 한다.
 */
export const PROVINCE_NAME_ALIASES = {
  강원특별자치도: ['강원특별자치도', '강원도'],
  전라북도: ['전라북도', '전북특별자치도'],
};

/**
 * 통합 시·도 코드 '12'(전남광주통합특별시) 안에서 옛 광주광역시에 해당하는 자치구의 구역 코드.
 * 서버는 광주와 전남을 하나의 시·도로 내려주지만 지도는 두 도형을 따로 그리므로,
 * 구역 코드로 갈라내야 두 지역이 각자의 방문률로 칠해진다.
 */
const GWANGJU_DISTRICT_CODES = new Set(['210', '240', '270', '300', '330']);
const MERGED_JEONNAM_GWANGJU_CODE = '12';

/** 통합 코드 '12' 지역이 광주(true)인지 전남(false)인지 가른다. */
function isGwangjuDistrict(region) {
  return GWANGJU_DISTRICT_CODES.has(String(region?.legalDistrictCode ?? ''));
}

/** 해당 시·도의 시·군·구만 골라낸다 — 코드 우선, 이름 접두 매칭 폴백 */
export function filterProvinceRegions(regions, provinceName) {
  const codes = PROVINCE_CODES[provinceName];
  if (codes?.length) {
    const byCode = regions.filter((r) => {
      const code = String(r.legalRegionCode ?? '');
      if (!codes.some((c) => code.startsWith(c))) return false;
      if (code !== MERGED_JEONNAM_GWANGJU_CODE) return true;
      // 통합 코드는 광주와 전남이 섞여 있으므로 구역 코드로 한 번 더 가른다.
      return provinceName === '광주광역시' ? isGwangjuDistrict(r) : !isGwangjuDistrict(r);
    });
    if (byCode.length > 0) return byCode;
  }
  const names = PROVINCE_NAME_ALIASES[provinceName] ?? [provinceName];
  return regions.filter((r) =>
    names.some((name) => String(r.regionName ?? '').startsWith(name))
  );
}

/**
 * 전국 지도 API의 regions[]를 시·도별 방문/전체 수로 집계한다.
 * KoreaMap의 regions prop 형태({name, collected, total})를 그대로 만든다.
 * @param {Array<{regionName, legalRegionCode, visited}>} regions
 * @returns {Array<{name: string, collected: number, total: number}>} 17개 시·도
 */
export function buildProvinceStats(regions) {
  const list = regions ?? [];
  return Object.keys(PROVINCE_CODES).map((name) => {
    const own = filterProvinceRegions(list, name);
    return {
      name,
      collected: own.filter((r) => r.visited).length,
      total: own.length,
    };
  });
}
