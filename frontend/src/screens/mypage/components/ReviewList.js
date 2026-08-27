import React, { useState, useCallback } from 'react';
import { StyleSheet, FlatList, ActivityIndicator, RefreshControl } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import theme from '../../../theme/theme';
import { fetchMyReviews } from '../../../api/reviews';
import usePagedList from '../hooks/usePagedList';
import ListStateView from './ListStateView';
import ReviewListItem from './ReviewListItem';
import ReviewDetailModal from './ReviewDetailModal';

const PAGE_SIZE = 20;

/**
 * 내 여행 기록(=인증 내역) 무한 스크롤 목록 — GET /reviews.
 * 여행 기록 / 인증 내역 두 화면이 공유하며, header로 상단 요약만 달리한다.
 * @param {(totalElements:number)=>React.ReactNode} [renderHeader]
 */
export default function ReviewList({ renderHeader, emptyText }) {
  const navigation = useNavigation();
  const [selected, setSelected] = useState(null);

  const fetchPage = useCallback((page) => fetchMyReviews({ page, size: PAGE_SIZE }), []);
  const {
    items, loading, loadingMore, errorMessage, hasMore, totalElements, loadMore, refresh,
  } = usePagedList(fetchPage, (res) => res?.items, [fetchPage]);

  const stateView = (
    <ListStateView
      loading={loading && items.length === 0}
      errorMessage={items.length === 0 ? errorMessage : null}
      empty={!loading && !errorMessage && items.length === 0}
      emptyText={emptyText}
      ctaLabel="탐험하러 가기"
      onCta={() => navigation.navigate('Home')}
      onRetry={refresh}
    />
  );

  return (
    <>
      <FlatList
        data={items}
        keyExtractor={(item) => String(item.reviewId)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={renderHeader ? renderHeader(totalElements) : null}
        ListEmptyComponent={stateView}
        onEndReached={loadMore}
        onEndReachedThreshold={0.4}
        refreshControl={(
          <RefreshControl
            refreshing={loading && items.length > 0}
            onRefresh={refresh}
            tintColor={theme.colors.primary}
          />
        )}
        renderItem={({ item }) => <ReviewListItem item={item} onPress={setSelected} />}
        ListFooterComponent={
          hasMore || loadingMore
            ? <ActivityIndicator size="small" color={theme.colors.primary} style={styles.footer} />
            : null
        }
      />
      {selected && <ReviewDetailModal review={selected} onClose={() => setSelected(null)} />}
    </>
  );
}

const styles = StyleSheet.create({
  list: {
    flexGrow: 1,
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  footer: {
    marginVertical: theme.spacing.base,
  },
});
