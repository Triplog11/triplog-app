import { useState, useEffect, useCallback, useRef } from 'react';

/**
 * 페이징 목록 공용 훅 — 무한 스크롤 + 새로고침 + 404(빈 목록/마지막 페이지) 처리.
 * @param {(page:number)=>Promise<object>} fetchPage  page를 받아 응답을 돌려주는 함수
 * @param {(res:object)=>Array} pickItems             응답에서 아이템 배열을 꺼내는 함수
 * @param {Array} deps                                 fetchPage가 바뀌는 의존성
 */
export default function usePagedList(fetchPage, pickItems, deps = []) {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);
  const requestId = useRef(0);

  const load = useCallback(
    async (nextPage, replace) => {
      requestId.current += 1;
      const id = requestId.current;
      if (replace) setLoading(true);
      else setLoadingMore(true);
      setErrorMessage(null);
      try {
        const result = await fetchPage(nextPage);
        if (id !== requestId.current) return;
        const nextItems = pickItems(result) ?? [];
        setItems((prev) => (replace ? nextItems : [...prev, ...nextItems]));
        setPage(nextPage);
        setTotalPages(result?.totalPages ?? 0);
        setTotalElements(result?.totalElements ?? nextItems.length);
      } catch (error) {
        if (id !== requestId.current) return;
        if (error?.status === 404) {
          // 첫 페이지 404 = 빈 목록, 이후 페이지 404 = 목록 끝
          if (replace) {
            setItems([]);
            setTotalElements(0);
          }
          setTotalPages(nextPage);
        } else {
          setErrorMessage(error?.message ?? '목록을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.');
        }
      } finally {
        if (id === requestId.current) {
          setLoading(false);
          setLoadingMore(false);
        }
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    deps,
  );

  useEffect(() => {
    setItems([]);
    load(0, true);
  }, [load]);

  const hasMore = page + 1 < totalPages;

  const loadMore = useCallback(() => {
    if (!loading && !loadingMore && hasMore) load(page + 1, false);
  }, [load, loading, loadingMore, hasMore, page]);

  const refresh = useCallback(() => load(0, true), [load]);

  return {
    items, loading, loadingMore, errorMessage, hasMore, totalElements, loadMore, refresh, setItems,
  };
}
