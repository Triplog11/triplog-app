import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, TouchableOpacity, FlatList, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchNationwideMap } from '../../api/regions';
import CardDetailModal from './components/CardDetailModal';
import MyCardsTab from './components/MyCardsTab';
import PhotoPlaceholder from './components/PhotoPlaceholder';

const SUB_TABS = [
  { key: 'region', label: '지역 도감', icon: 'location-outline' },
  { key: 'card', label: '카드 목록', icon: 'book-outline' },
];

const REGION_FILTERS = [
  { key: 'all', label: '전체' },
  { key: 'visited', label: '방문' },
  { key: 'unvisited', label: '미방문' },
];

/** 도감 — 지역 도감 / 카드 목록 2개 서브탭 (실 API). 전국 지도는 홈 탭이 담당한다. */
export default function CollectionScreen({ navigation }) {
  const [subTab, setSubTab] = useState('region');
  const [selectedCard, setSelectedCard] = useState(null);

  const openVerify = () => {
    setSelectedCard(null);
    navigation.navigate('Record');
  };

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      {/* 서브탭 스위처 */}
      <View style={styles.tabSwitcher}>
        {SUB_TABS.map((tab) => {
          const active = subTab === tab.key;
          return (
            <TouchableOpacity
              key={tab.key}
              style={[styles.tabButton, active && styles.tabButtonActive]}
              onPress={() => setSubTab(tab.key)}
              activeOpacity={0.8}
            >
              <Ionicons
                name={tab.icon}
                size={13}
                color={active ? theme.colors.primary : theme.colors.textSecondary}
              />
              <CustomText
                variant="Label/Medium"
                color={active ? theme.colors.primary : theme.colors.textSecondary}
                style={styles.tabLabel}
              >
                {tab.label}
              </CustomText>
            </TouchableOpacity>
          );
        })}
      </View>

      {subTab === 'region' && (
        <RegionTab
          onSelectRegion={(region) =>
            navigation.navigate('RegionCollection', {
              regionId: region.regionId,
              regionName: region.regionName,
            })
          }
        />
      )}
      {subTab === 'card' && <MyCardsTab onSelectCard={setSelectedCard} />}

      <CardDetailModal card={selectedCard} onClose={() => setSelectedCard(null)} onVerifyPress={openVerify} />
    </SafeAreaView>
  );
}

/** completionRate가 0~1 비율인지 0~100 퍼센트인지 불확실 → 방어적 정규화 */
function toPercent(rate) {
  if (rate == null) return 0;
  return Math.round(rate <= 1 ? rate * 100 : rate);
}

/** 지역 도감 탭 — 전국 지도 API 기반 전체 현황 + 시·군·구 리스트 */
function RegionTab({ onSelectRegion }) {
  const [filter, setFilter] = useState('all');
  const [summary, setSummary] = useState(null);
  const [status, setStatus] = useState('loading'); // loading | ready | error

  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const result = await fetchNationwideMap();
      setSummary(result ?? null);
      setStatus('ready');
    } catch (error) {
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

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
          지역 현황을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
        </CustomText>
        <TouchableOpacity style={styles.retryPill} onPress={load} activeOpacity={0.85}>
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
            다시 시도
          </CustomText>
        </TouchableOpacity>
      </View>
    );
  }

  const regions = summary?.regions ?? [];
  const filtered = regions.filter((r) => {
    if (filter === 'visited') return r.visited;
    if (filter === 'unvisited') return !r.visited;
    return true;
  });

  const header = (
    <View style={styles.statsCard}>
      <View style={styles.statsHeader}>
        <View>
          <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
            전체 수집 현황
          </CustomText>
          <CustomText variant="Caption" color={theme.colors.textSecondary}>
            {summary?.visitedRegionCount ?? 0}개 지역 방문
          </CustomText>
        </View>
        <CustomText variant="Heading/H3" color={theme.colors.primary} style={styles.bold}>
          {summary?.completedRegionCount ?? 0}/{summary?.totalRegionCount ?? 0}
        </CustomText>
      </View>
      <View style={styles.progressTrack}>
        <View style={[styles.progressFill, { width: `${toPercent(summary?.overallCompletionRate)}%` }]} />
      </View>
      <View style={styles.filterRow}>
        {REGION_FILTERS.map((f) => {
          const active = filter === f.key;
          return (
            <TouchableOpacity
              key={f.key}
              style={[styles.filterBtn, active && styles.filterBtnActive]}
              onPress={() => setFilter(f.key)}
              activeOpacity={0.8}
            >
              <CustomText
                variant="Label/Medium"
                color={active ? '#FFFFFF' : theme.colors.textSecondary}
                style={styles.bold}
              >
                {f.label}
              </CustomText>
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );

  const renderRegion = ({ item }) => {
    const percent = toPercent(item.completionRate);
    const complete = item.completed || percent === 100;
    return (
      <TouchableOpacity
        style={styles.regionRow}
        onPress={() => onSelectRegion(item)}
        activeOpacity={0.85}
      >
        <View style={styles.regionThumbWrap}>
          <PhotoPlaceholder icon="map-outline" size={22} />
          {complete && (
            <View style={styles.regionThumbOverlay}>
              <Ionicons name="trophy" size={16} color="#FFFFFF" />
            </View>
          )}
        </View>
        <View style={styles.regionInfo}>
          <View style={styles.regionNameRow}>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
              {item.regionName}
            </CustomText>
            {complete && <Ionicons name="trophy" size={12} color={theme.colors.success} />}
          </View>
          <CustomText variant="Caption" color={theme.colors.textSecondary} style={styles.regionMeta}>
            {item.visited ? '방문한 지역' : '아직 방문 전'}
          </CustomText>
          <View style={styles.regionProgressTrack}>
            <View
              style={[styles.progressFill, complete && styles.progressFillComplete, { width: `${percent}%` }]}
            />
          </View>
        </View>
        <View style={styles.regionRight}>
          <CustomText
            variant="Label/Medium"
            color={complete ? theme.colors.success : percent > 0 ? theme.colors.primary : theme.colors.textMuted}
            style={styles.bold}
          >
            {percent}%
          </CustomText>
          <Ionicons name="chevron-forward" size={14} color={theme.colors.textMuted} />
        </View>
      </TouchableOpacity>
    );
  };

  return (
    <FlatList
      data={filtered}
      keyExtractor={(item) => String(item.regionId)}
      renderItem={renderRegion}
      ListHeaderComponent={header}
      contentContainerStyle={styles.regionScroll}
      showsVerticalScrollIndicator={false}
      ItemSeparatorComponent={() => <View style={styles.regionGap} />}
      ListEmptyComponent={
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.centerText}>
          해당하는 지역이 없어요.
        </CustomText>
      }
    />
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
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
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 4,
    paddingVertical: 9,
    borderRadius: theme.rounded.md,
  },
  tabButtonActive: {
    backgroundColor: theme.colors.canvas,
  },
  tabLabel: {
    fontWeight: '600',
  },
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
    paddingVertical: theme.spacing.lg,
  },
  retryPill: {
    borderRadius: theme.rounded.pill ?? 9999,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 8,
  },
  regionGap: {
    height: theme.spacing.sm,
  },
  // 지역 도감 탭
  regionScroll: {
    padding: theme.spacing.lg,
    paddingBottom: 40,
  },
  statsCard: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
    marginBottom: theme.spacing.sm,
  },
  statsHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  progressTrack: {
    height: 8,
    borderRadius: 4,
    backgroundColor: theme.colors.surfaceDim,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    borderRadius: 4,
    backgroundColor: theme.colors.primary,
  },
  progressFillComplete: {
    backgroundColor: theme.colors.success,
  },
  filterRow: {
    flexDirection: 'row',
    gap: 8,
    marginTop: theme.spacing.base,
  },
  filterBtn: {
    flex: 1,
    height: 32,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
  filterBtnActive: {
    backgroundColor: theme.colors.primary,
  },
  regionRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.sm,
  },
  regionThumbWrap: {
    width: 60,
    height: 60,
    borderRadius: theme.rounded.md,
    overflow: 'hidden',
    backgroundColor: theme.colors.surfaceDim,
  },
  regionThumbOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(18,184,134,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  regionInfo: {
    flex: 1,
    gap: 3,
  },
  regionNameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
  },
  regionMeta: {
    marginBottom: 3,
  },
  regionProgressTrack: {
    height: 5,
    borderRadius: 3,
    backgroundColor: theme.colors.surfaceDim,
    overflow: 'hidden',
  },
  regionRight: {
    alignItems: 'flex-end',
    gap: 4,
  },
});
