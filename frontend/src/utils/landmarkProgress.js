/**
 * 지역 목록의 랜드마크 카드 획득 현황 문구("1/3장").
 * 전국 지도 응답에 개수 필드가 내려올 때만 표시하고, 없으면 null 을 돌려 비율만 보이게 한다.
 */
export function formatLandmarkProgress(region) {
  const acquired = region?.acquiredLandmarkCount;
  const total = region?.totalLandmarkCount;
  if (!Number.isFinite(acquired) || !Number.isFinite(total)) return null;
  return `${acquired}/${total}장`;
}
