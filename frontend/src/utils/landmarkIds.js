/**
 * 방문 인증(POST /reviews)에 보낼 tourismContentId를 랜드마크 DTO에서 뽑는다.
 *
 * 백엔드 랜드마크 DTO에는 아직 내부 `tourismContentId`가 노출되지 않는다
 * (`contentId`는 TourAPI 외부 id라 다른 값). 백엔드 팀에 tourismContentId 추가를
 * 요청한 상태이며, 추가되기 전까지는 landmarkId를 대신 사용한다.
 * 필드가 내려오기 시작하면 이 헬퍼만 그대로 두어도 자동으로 우선 사용된다.
 */
export function getTourismContentId(landmark) {
  if (!landmark) return null;
  return landmark.tourismContentId ?? landmark.landmarkId ?? null;
}
