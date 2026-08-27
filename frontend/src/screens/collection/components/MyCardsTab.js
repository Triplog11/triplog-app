import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, FlatList, ActivityIndicator, Image } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { GRADE_CONFIG, GRADE_ORDER, toCardModel } from '../../../data/collection';
import { fetchMyCards } from '../../../api/landmarks';
import { EmptyStateAssets } from '../../../assets';
import LandmarkCardItem from './LandmarkCardItem';

const PAGE_SIZE = 20;

/**
 * 도감 "카드 목록" 탭 — GET /landmarks/me 무한 스크롤 + 등급 필터(불러온 카드 기준).
 * 404는 빈 목록으로 취급한다.
 */
export default function MyCardsTab({ onSelectCard }) {
  const [gradeFilter, setGradeFilter] = useState('All');
  const [cards, setCards] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [status, setStatus] = useState('loading'); // loading | ready | error | more
  const [refreshing, setRefreshing] = useState(false);

  const loadPage = useCallback(async (targetPage, { replace = false } = {}) => {
    try {
      const result = await fetchMyCards({ page: targetPage, size: PAGE_SIZE });
      const items = (result?.items ?? []).map(toCardModel);
      setCards((prev) => (replace ? items : [...prev, ...items]));
      setPage(targetPage);
      setTotalPages(result?.totalPages ?? 0);
      setTotalElements(result?.totalElements ?? items.length);
      setStatus('ready');
    } catch (error) {
      if (error?.status === 404) {
        setCards([]);
        setTotalPages(0);
        setTotalElements(0);
        setStatus('ready');
        return;
      }
      setStatus(replace ? 'error' : 'ready');
    }
  }, []);

  useEffect(() => {
    setStatus('loading');
    loadPage(0, { replace: true });
  }, [loadPage]);

  const retry = () => {
    setStatus('loading');
    loadPage(0, { replace: true });
  };

  const refresh = async () => {
    setRefreshing(true);
    await loadPage(0, { replace: true });
    setRefreshing(false);
  };

  const hasMore = page + 1 < totalPages;
  const loadMore = () => {
    if (status !== 'ready' || !hasMore) return;
    setStatus('more');
    loadPage(page + 1);
  };

  const visible = useMemo(
    () => (gradeFilter === 'All' ? cards : cards.filter((c) => c.grade === gradeFilter)),
    [cards, gradeFilter],
  );

  if (status === 'loading') {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
      </View>
    );
  }

  if (status === 'error') {
    return (
      <View style={styles.center}>
        <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.centerText}>
          카드 목록을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
        </CustomText>
        <TouchableOpacity style={styles.retryPill} onPress={retry} activeOpacity={0.85}>
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
            다시 시도
          </CustomText>
        </TouchableOpacity>
      </View>
    );
  }

  const header = (
    <View>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.gradeFilterRow}>
        {['All', ...GRADE_ORDER].map((g) => {
          const active = gradeFilter === g;
          const config = g === 'All' ? null : GRADE_CONFIG[g];
          return (
            <TouchableOpacity
              key={g}
              style={[
                styles.gradeChip,
                config && !active && { backgroundColor: config.soft },
                active && styles.gradeChipActive,
              ]}
              onPress={() => setGradeFilter(g)}
              activeOpacity={0.8}
            >
              <CustomText
                variant="Label/Medium"
                color={active ? '#FFFFFF' : config ? config.color : theme.colors.textSecondary}
                style={styles.bold}
              >
                {g === 'All' ? '전체' : config.label}
              </CustomText>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
      <View style={styles.cardStatsRow}>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
          <CustomText variant="Body/Small" color={theme.colors.primary} style={styles.bold}>
            {totalElements}
          </CustomText>
          장 수집
        </CustomText>
        <View style={styles.gradeCountRow}>
          {GRADE_ORDER.map((g) => {
            const config = GRADE_CONFIG[g];
            const count = cards.filter((c) => c.grade === g).length;
            return (
              <View key={g} style={[styles.gradeCountPill, { backgroundColor: config.soft }]}>
                <CustomText variant="Caption" color={config.color} style={styles.gradeCountText}>
                  {config.label} {count}
                </CustomText>
              </View>
            );
          })}
        </View>
      </View>
    </View>
  );

  return (
    <FlatList
      data={visible}
      keyExtractor={(item) => String(item.id)}
      numColumns={2}
      columnWrapperStyle={styles.cardColumnWrap}
      contentContainerStyle={styles.cardListContent}
      showsVerticalScrollIndicator={false}
      renderItem={({ item }) => (
        <LandmarkCardItem card={item} wishlisted={false} onPress={() => onSelectCard(item)} />
      )}
      ListHeaderComponent={header}
      ListEmptyComponent={
        <View style={styles.emptyContainer}>
          <Image
            source={EmptyStateAssets.collection}
            style={styles.emptyIllustration}
            resizeMode="contain"
          />
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.centerText}>
            {cards.length === 0
              ? '아직 모은 카드가 없어요. 첫 번째 랜드마크를 인증해 보세요!'
              : '이 등급의 카드는 아직 없어요.'}
          </CustomText>
        </View>
      }
      ListFooterComponent={
        status === 'more' ? <ActivityIndicator size="small" color={theme.colors.primary} style={styles.footer} /> : null
      }
      onEndReached={loadMore}
      onEndReachedThreshold={0.4}
      refreshing={refreshing}
      onRefresh={refresh}
    />
  );
}

const styles = StyleSheet.create({
  bold: {
    fontWeight: 'bold',
  },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: theme.spacing.lg,
    gap: theme.spacing.base,
  },
  centerText: {
    textAlign: 'center',
    paddingVertical: theme.spacing.sm,
  },
  emptyContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: theme.spacing.xl,
    gap: theme.spacing.sm,
  },
  emptyIllustration: {
    width: 180,
    height: 120,
  },
  retryPill: {
    borderRadius: theme.rounded.pill ?? 9999,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 8,
  },
  cardListContent: {
    padding: theme.spacing.lg,
    paddingBottom: 40,
  },
  cardColumnWrap: {
    gap: theme.spacing.sm,
    marginBottom: theme.spacing.sm,
  },
  gradeFilterRow: {
    gap: 6,
    paddingBottom: theme.spacing.sm,
  },
  gradeChip: {
    height: 32,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 14,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
  },
  gradeChipActive: {
    backgroundColor: theme.colors.primary,
  },
  cardStatsRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  gradeCountRow: {
    flexDirection: 'row',
    gap: 4,
  },
  gradeCountPill: {
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 7,
    paddingVertical: 2,
  },
  gradeCountText: {
    fontWeight: 'bold',
    fontSize: 10,
  },
  footer: {
    paddingVertical: theme.spacing.base,
  },
});
