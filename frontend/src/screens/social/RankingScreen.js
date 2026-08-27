import React, { useState, useEffect, useCallback } from 'react';
import {
  StyleSheet,
  View,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useAuth } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchRankings, fetchMyRanking, RANKING_TYPE } from '../../api/stats';
import RankingPodium from './ranking/RankingPodium';
import RankingRow from './ranking/RankingRow';
import MyRankingCard from './ranking/MyRankingCard';

const TABS = [
  { key: RANKING_TYPE.TOTAL, label: '전체 랭킹' },
  { key: RANKING_TYPE.MONTHLY, label: '월간 랭킹' },
];

const PAGE_SIZE = 50;

/** 랭킹 데이터 미존재(404)는 빈 상태로 간주하고 삼킨다. 그 외 에러는 다시 던진다. */
function swallowNotFound(error) {
  if (error?.status === 404) return null;
  throw error;
}

/** 랭킹 — 전체/월간 탭, TOP3 시상대, 순위 리스트, 하단 고정 내 순위 */
export default function RankingScreen() {
  const { user } = useAuth();
  const [rankingType, setRankingType] = useState(RANKING_TYPE.TOTAL);
  const [rankings, setRankings] = useState([]);
  const [myRank, setMyRank] = useState(null);
  const [status, setStatus] = useState('loading'); // loading | ready | error
  const [refreshing, setRefreshing] = useState(false);

  const load = useCallback(async (type, { silent = false } = {}) => {
    if (!silent) setStatus('loading');
    try {
      const [list, mine] = await Promise.all([
        fetchRankings({ rankingType: type, size: PAGE_SIZE }).catch(swallowNotFound),
        fetchMyRanking().catch(swallowNotFound),
      ]);
      setRankings(Array.isArray(list?.rankings) ? list.rankings : []);
      setMyRank(mine ?? null);
      setStatus('ready');
    } catch (error) {
      console.error('랭킹 조회 실패:', error);
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    load(rankingType);
  }, [rankingType, load]);

  const handleRefresh = useCallback(async () => {
    setRefreshing(true);
    await load(rankingType, { silent: true });
    setRefreshing(false);
  }, [load, rankingType]);

  const top3 = rankings.slice(0, 3);
  const rest = rankings.slice(3);
  const isMonthly = rankingType === RANKING_TYPE.MONTHLY;

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.tabSwitcher}>
        {TABS.map((t) => {
          const active = rankingType === t.key;
          return (
            <TouchableOpacity
              key={t.key}
              style={[styles.tabButton, active && styles.tabButtonActive]}
              onPress={() => setRankingType(t.key)}
              activeOpacity={0.8}
            >
              <CustomText
                variant="Label/Medium"
                color={active ? theme.colors.primary : theme.colors.textSecondary}
                style={styles.bold}
              >
                {t.label}
              </CustomText>
            </TouchableOpacity>
          );
        })}
      </View>

      {status === 'loading' && (
        <View style={styles.center}>
          <ActivityIndicator size="large" color={theme.colors.primary} />
        </View>
      )}

      {status === 'error' && (
        <View style={styles.center}>
          <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.centerText}>
            랭킹을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
          </CustomText>
          <TouchableOpacity style={styles.retryPill} onPress={() => load(rankingType)} activeOpacity={0.85}>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        </View>
      )}

      {status === 'ready' && (
        <ScrollView
          contentContainerStyle={styles.scroll}
          showsVerticalScrollIndicator={false}
          refreshControl={
            <RefreshControl
              refreshing={refreshing}
              onRefresh={handleRefresh}
              tintColor={theme.colors.primary}
              colors={[theme.colors.primary]}
            />
          }
        >
          {rankings.length === 0 ? (
            <View style={styles.emptyBox}>
              <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.centerText}>
                {isMonthly
                  ? '이번 달 랭킹이 아직 비어 있어요. 첫 인증으로 순위를 만들어 보세요!'
                  : '아직 랭킹 데이터가 없어요. 첫 인증으로 순위를 만들어 보세요!'}
              </CustomText>
            </View>
          ) : (
            <>
              <RankingPodium top3={top3} />
              <View style={styles.list}>
                {rest.map((player) => (
                  <RankingRow key={player.rank} player={player} />
                ))}
              </View>
            </>
          )}
        </ScrollView>
      )}

      {status === 'ready' && myRank && (
        <MyRankingCard myRank={myRank} isMonthly={isMonthly} fallbackUser={user} />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  bold: { fontWeight: 'bold' },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: theme.spacing.lg,
    gap: theme.spacing.base,
  },
  centerText: { textAlign: 'center' },
  retryPill: {
    borderRadius: theme.rounded.full,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 8,
  },
  emptyBox: {
    paddingVertical: 48,
    paddingHorizontal: theme.spacing.lg,
    alignItems: 'center',
  },
  tabSwitcher: {
    flexDirection: 'row',
    marginHorizontal: theme.spacing.lg,
    marginTop: theme.spacing.base,
    backgroundColor: theme.colors.surfaceDim,
    borderRadius: theme.rounded.card,
    padding: 4,
    gap: 4,
  },
  tabButton: {
    flex: 1,
    alignItems: 'center',
    paddingVertical: 9,
    borderRadius: theme.rounded.md,
  },
  tabButtonActive: {
    backgroundColor: theme.colors.canvas,
  },
  scroll: {
    padding: theme.spacing.lg,
    paddingBottom: 96,
  },
  list: {
    gap: theme.spacing.sm,
  },
});
