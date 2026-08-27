/**
 * 시·도 이름 ↔ 법정 시·도 코드(legalRegionCode 앞 2자리) 유틸.
 * 강원/전북은 특별자치도 개편으로 코드가 바뀌어 신·구 코드를 함께 둔다.
 */
export const PROVINCE_CODES = {
  서울특별시: ['11'],
  부산광역시: ['26'],
  대구광역시: ['27'],
  인천광역시: ['28'],
  광주광역시: ['29'],
  대전광역시: ['30'],
  울산광역시: ['31'],
  세종특별자치시: ['36'],
  경기도: ['41'],
  강원특별자치도: ['51', '42'],
  충청북도: ['43'],
  충청남도: ['44'],
  전라북도: ['52', '45'],
  전북특별자치도: ['52', '45'],
  전라남도: ['46'],
  경상북도: ['47'],
  경상남도: ['48'],
  제주특별자치도: ['50'],
};

/** 해당 시·도의 시·군·구만 골라낸다 — 코드 우선, 이름 접두 매칭 폴백 */
export function filterProvinceRegions(regions, provinceName) {
  const codes = PROVINCE_CODES[provinceName];
  if (codes?.length) {
    const byCode = regions.filter((r) =>
      codes.some((code) => String(r.legalRegionCode ?? '').startsWith(code))
    );
    if (byCode.length > 0) return byCode;
  }
  return regions.filter((r) => String(r.regionName ?? '').startsWith(provinceName));
}

/**
 * 전국 지도 API의 regions[]를 시·도별 방문/전체 수로 집계한다.
 * KoreaMap의 regions prop 형태({name, collected, total})를 그대로 만든다.
 * @param {Array<{regionName, legalRegionCode, visited}>} regions
 * @returns {Array<{name: string, collected: number, total: number}>}
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
