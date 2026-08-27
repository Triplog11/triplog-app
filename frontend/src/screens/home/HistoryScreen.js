import React, { useCallback, useMemo } from 'react';
import { StyleSheet, View, SectionList, ActivityIndicator, RefreshControl } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchActivityHistory, ACTIVITY_TYPE } from '../../api/mypage';
import usePagedList from '../mypage/hooks/usePagedList';
import ListStateView from '../mypage/components/ListStateView';
import { formatDate, formatReward } from '../mypage/utils/format';

const PAGE_SIZE = 20;

/** 활동 타입별 아이콘 (Ionicons) */
const ACTIVITY_ICON = {
  [ACTIVITY_TYPE.ATTRACTION]: 'location-outline',
  [ACTIVITY_TYPE.LANDMARK]: 'flag-outline',
  [ACTIVITY_TYPE.REGION]: 'map-outline',
  [ACTIVITY_TYPE.CARD]: 'albums-outline',
  [ACTIVITY_TYPE.BADGE]: 'ribbon-outline',
  [ACTIVITY_TYPE.TITLE]: 'pricetag-outline',
  [ACTIVITY_TYPE.LEVEL]: 'trending-up-outline',
  [ACTIVITY_TYPE.RANK]: 'trophy-outline',
  [ACTIVITY_TYPE.MISSION]: 'checkmark-done-outline',
};

/** 활동 배열을 날짜(yyyy.MM.dd)별 섹션으로 묶는다 (입력 순서 유지) */
function groupByDate(activities) {
  const byDate = activities.reduce((acc, activity) => {
    const date = formatDate(activity.createdAt) || '날짜 미상';
    return { ...acc, [date]: [...(acc[date] ?? []), activity] };
  }, {});
  return Object.entries(byDate).map(([title, data]) => ({ title, data }));
}

/** 활동 내역 — GET /mypage/activityhistory 실연동, 날짜별 그룹 + 보상 표시 */
export default function HistoryScreen() {
  const fetchPage = useCallback((page) => fetchActivityHistory({ page, size: PAGE_SIZE }), []);
  const {
    items, loading, loadingMore, errorMessage, hasMore, loadMore, refresh,
  } = usePagedList(fetchPage, (res) => res?.activities, [fetchPage]);

  const sections = useMemo(() => groupByDate(items), [items]);

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <SectionList
        sections={sections}
        keyExtractor={(item) => String(item.activityId)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        stickySectionHeadersEnabled={false}
        onEndReached={loadMore}
        onEndReachedThreshold={0.4}
        refreshControl={(
          <RefreshControl
            refreshing={loading && items.length > 0}
            onRefresh={refresh}
            tintColor={theme.colors.primary}
          />
        )}
        ListEmptyComponent={(
          <ListStateView
            loading={loading && items.length === 0}
            errorMessage={items.length === 0 ? errorMessage : null}
            empty={!loading && !errorMessage && items.length === 0}
            emptyText="아직 활동 내역이 없어요. 첫 번째 랜드마크를 인증해 보세요!"
            onRetry={refresh}
          />
        )}
        renderSectionHeader={({ section }) => (
          <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={styles.sectionTitle}>
            {section.title}
          </CustomText>
        )}
        renderItem={({ item }) => <ActivityRow activity={item} />}
        ListFooterComponent={
          hasMore || loadingMore
            ? <ActivityIndicator size="small" color={theme.colors.primary} style={styles.footer} />
            : null
        }
      />
    </SafeAreaView>
  );
}

function ActivityRow({ activity }) {
  const icon = ACTIVITY_ICON[activity.activityType] ?? 'sparkles-outline';
  const reward = formatReward(activity.xp, activity.score);
  return (
    <View style={styles.card}>
      <View style={styles.iconWrap}>
        <Ionicons name={icon} size={18} color={theme.colors.primary} />
      </View>
      <View style={styles.body}>
        <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.title} numberOfLines={1}>
          {activity.title}
        </CustomText>
        {activity.content ? (
          <CustomText variant="Caption" color={theme.colors.textSecondary} numberOfLines={2}>
            {activity.content}
          </CustomText>
        ) : null}
      </View>
      {reward ? (
        <CustomText variant="Caption" color={theme.colors.primary} style={styles.reward}>
          {reward}
        </CustomText>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  list: {
    flexGrow: 1,
    paddingHorizontal: theme.spacing.lg,
    paddingBottom: theme.spacing.section,
  },
  sectionTitle: {
    fontWeight: 'bold',
    marginTop: theme.spacing.lg,
    marginBottom: theme.spacing.sm,
  },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.md,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.md,
    marginBottom: theme.spacing.sm,
  },
  iconWrap: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: theme.colors.primarySoft,
    justifyContent: 'center',
    alignItems: 'center',
  },
  body: {
    flex: 1,
    gap: 2,
  },
  title: {
    fontWeight: 'bold',
  },
  reward: {
    fontWeight: 'bold',
    textAlign: 'right',
  },
  footer: {
    marginVertical: theme.spacing.base,
  },
});
