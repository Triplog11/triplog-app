import { useState, useEffect, useCallback } from 'react';
import { fetchBookmarks, createBookmark, deleteBookmark, BOOKMARK_TYPE } from '../../../api/bookmarks';
import { findLandmarkBookmarkId } from '../../../utils/landmarkBookmark';

/**
 * 랜드마크 찜 상태와 토글.
 * 상세 응답에 찜 여부가 없으므로 찜 목록에서 bookmarkId 를 찾아 상태를 만든다.
 * @returns {{bookmarked: boolean, pending: boolean, toggle: () => Promise<void>, errorMessage: string|null}}
 */
export default function useLandmarkBookmark(landmarkId) {
  const [bookmarkId, setBookmarkId] = useState(null);
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  useEffect(() => {
    setBookmarkId(null);
    setErrorMessage(null);
    if (landmarkId == null) return undefined;
    let mounted = true;
    const fetchPage = (page) => fetchBookmarks({ bookmarkType: BOOKMARK_TYPE.LANDMARK, page, size: 50 });
    findLandmarkBookmarkId(fetchPage, landmarkId)
      .then((id) => mounted && setBookmarkId(id))
      .catch(() => {});
    return () => {
      mounted = false;
    };
  }, [landmarkId]);

  const toggle = useCallback(async () => {
    if (landmarkId == null || pending) return;
    setPending(true);
    setErrorMessage(null);
    try {
      if (bookmarkId != null) {
        const result = await deleteBookmark(bookmarkId);
        if (result?.isDeleted !== false) setBookmarkId(null);
      } else {
        const result = await createBookmark(BOOKMARK_TYPE.LANDMARK, landmarkId);
        setBookmarkId(result?.bookmarkId ?? null);
      }
    } catch (error) {
      setErrorMessage('찜 상태를 바꾸지 못했어요. 잠시 후 다시 시도해 주세요.');
    } finally {
      setPending(false);
    }
  }, [landmarkId, bookmarkId, pending]);

  return { bookmarked: bookmarkId != null, pending, toggle, errorMessage };
}
