/**
 * 실제 위경도 → 전국 지도(koreaProvinces) 좌표 투영.
 * 지도 자체가 같은 등장방형 투영으로 생성되므로 역산이 정확하다.
 */
import { KOREA_PROVINCES } from './koreaProvinces';

/**
 * 위경도 → 전국 지도 SVG 좌표. 투영 불가 시 null.
 * @returns { cx, cy } | null
 */
export function projectToNationalSvg(coords) {
  if (!coords || coords.latitude == null || coords.longitude == null) return null;
  const { lonMin, latMax, kx, ky } = KOREA_PROVINCES.projection;
  return {
    cx: (coords.longitude - lonMin) * kx,
    cy: (latMax - coords.latitude) * ky,
  };
}
