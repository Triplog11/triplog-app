import { authedRequest } from './client';

/** 북마크(찜) 대상 타입 */
export const BOOKMARK_TYPE = {
  REGION: 'REGION',
  LANDMARK: 'LANDMARK',
  EVENT: 'EVENT',
};

/**
 * 북마크 등록.
 * @param {string} bookmarkType REGION | LANDMARK | EVENT
 * @param {number} bookmarkIdentifier 대상 id
 * @returns {bookmarkId, bookmarkType, bookmarkIdentifier, bookmarked}
 */
export function createBookmark(bookmarkType, bookmarkIdentifier) {
  return authedRequest('/bookmarks', {
    method: 'POST',
    body: { bookmarkType, bookmarkIdentifier },
  });
}

/**
 * 북마크 목록 조회 (타입별).
 * @returns {page, size, totalElements, totalPages, bookmarks: [...]}
 *          엔트리 shape는 타입별로 다름:
 *          LANDMARK {bookmarkId, landmarkId, landmarkName, regionId, regionName, contentId}
 *          REGION   {bookmarkId, regionId, regionName}
 *          EVENT    {bookmarkId, eventId, eventTitle, eventImageUrl, eventStart, eventEnd}
 */
export function fetchBookmarks({ bookmarkType, page = 0, size = 10 }) {
  const params = new URLSearchParams({
    bookmarkType,
    page: String(page),
    size: String(size),
  });
  return authedRequest(`/bookmarks?${params.toString()}`);
}

/**
 * 북마크 해제.
 * @returns {isDeleted}
 */
export function deleteBookmark(bookmarkId) {
  return authedRequest(`/bookmarks/${bookmarkId}`, { method: 'DELETE' });
}
