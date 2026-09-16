const PAGE_LIMIT = 10;

/**
 * 찜 목록을 페이지 단위로 넘기며 해당 랜드마크의 bookmarkId 를 찾는다.
 * 랜드마크 상세 응답에 찜 여부가 없어서, 해제에 필요한 bookmarkId 를 목록에서 구한다.
 * @param {(page: number) => Promise<{bookmarks: Array, totalPages: number}>} fetchPage
 * @param {number|string} landmarkId
 * @returns {Promise<number|null>}
 */
export async function findLandmarkBookmarkId(fetchPage, landmarkId) {
  const target = String(landmarkId);
  for (let page = 0; page < PAGE_LIMIT; page += 1) {
    const result = await fetchPage(page);
    const hit = (result?.bookmarks ?? []).find((item) => String(item.landmarkId) === target);
    if (hit) return hit.bookmarkId;
    if (page + 1 >= (result?.totalPages ?? 0)) return null;
  }
  return null;
}
