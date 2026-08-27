import { useState, useCallback } from 'react';
import { useFocusEffect } from '@react-navigation/native';
import { fetchMyPage } from '../../../api/mypage';
import { fetchMyStats } from '../../../api/stats';
import { fetchMyCards } from '../../../api/landmarks';
import { fetchMyReviews } from '../../../api/reviews';

const RECENT_CARDS_SIZE = 6;
const RECENT_REVIEWS_SIZE = 3;

const INITIAL_SECTION = { items: [], total: null, loading: true, errorMessage: null };

/** 목록 응답 → 섹션 상태 (404는 빈 목록) */
async function loadSection(fetcher, pick) {
  try {
    const result = await fetcher();
    return { items: pick(result) ?? [], total: result?.totalElements ?? 0, loading: false, errorMessage: null };
  } catch (error) {
    if (error?.status === 404) {
      return { items: [], total: 0, loading: false, errorMessage: null };
    }
    return { ...INITIAL_SECTION, loading: false, errorMessage: error?.message ?? '불러오지 못했어요.' };
  }
}

/**
 * 마이페이지 데이터 — 요약(fetchMyPage) + XP 정책(fetchMyStats) + 최근 카드 + 최근 기록.
 * 화면 포커스마다 다시 불러와서 인증 직후 카운트가 갱신된다.
 * 각 요청은 독립적으로 실패해도 다른 섹션을 막지 않는다.
 */
export default function useMyPageData() {
  const [summary, setSummary] = useState(null);
  const [summaryError, setSummaryError] = useState(null);
  const [stats, setStats] = useState(null);
  const [cards, setCards] = useState(INITIAL_SECTION);
  const [reviews, setReviews] = useState(INITIAL_SECTION);

  const load = useCallback(async () => {
    const [summaryResult, statsResult, cardsResult, reviewsResult] = await Promise.all([
      fetchMyPage().then((r) => ({ data: r ?? null, error: null })).catch((error) => ({ data: null, error })),
      fetchMyStats().then((r) => r ?? null).catch(() => null),
      loadSection(() => fetchMyCards({ size: RECENT_CARDS_SIZE }), (r) => r?.items),
      loadSection(() => fetchMyReviews({ size: RECENT_REVIEWS_SIZE }), (r) => r?.items),
    ]);
    setSummary(summaryResult.data);
    setSummaryError(summaryResult.error?.message ?? null);
    setStats(statsResult);
    setCards(cardsResult);
    setReviews(reviewsResult);
  }, []);

  useFocusEffect(
    useCallback(() => {
      let active = true;
      load().catch(() => {
        // 개별 실패는 위에서 처리 — 여기 도달하면 예기치 못한 오류
        if (active) setSummaryError('마이페이지를 불러오지 못했어요. 잠시 후 다시 시도해 주세요.');
      });
      return () => {
        active = false;
      };
    }, [load]),
  );

  return { summary, summaryError, stats, cards, reviews, reload: load };
}
