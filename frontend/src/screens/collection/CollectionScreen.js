import React, { useState, useMemo } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, FlatList } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import {
  LANDMARK_CARDS,
  PROVINCE_LANDMARK_DATA,
  GRADE_CONFIG,
  GRADE_ORDER,
  getProvinceProgress,
} from '../../data/collection';
import LandmarkCardItem from './components/LandmarkCardItem';
import CardDetailModal from './components/CardDetailModal';
import DaejeonMapTab from './components/DaejeonMapTab';
import PhotoPlaceholder from './components/PhotoPlaceholder';

const SUB_TABS = [
  { key: 'region', label: '지역 도감', icon: 'location-outline' },
  { key: 'card', label: '카드 목록', icon: 'book-outline' },
  { key: 'map', label: '지도', icon: 'globe-outline' },
];

const REGION_FILTERS = [
  { key: 'all', label: '전체' },
  { key: 'visited', label: '방문' },
  { key: 'unvisited', label: '미방문' },
];

/** 도감 — 지역 도감 / 카드 목록 / 지도 3개 서브탭 (프로토타입 2 레이아웃, 목데이터) */
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
        <RegionTab onSelectRegion={(key) => navigation.navigate('RegionCollection', { provinceKey: key })} />
      )}
      {subTab === 'card' && <CardGridTab onSelectCard={setSelectedCard} />}
      {subTab === 'map' && (
        <DaejeonMapTab onRegionPress={() => navigation.navigate('RegionCollection', { provinceKey: '대전' })} />
      )}

      <CardDetailModal card={selectedCard} onClose={() => setSelectedCard(null)} onVerifyPress={openVerify} />
    </SafeAreaView>
  );
}

/** 지역 도감 탭 — 전체 수집 현황 + 지역 리스트 */
function RegionTab({ onSelectRegion }) {
  const [filter, setFilter] = useState('all');

  const provinces = Object.values(PROVINCE_LANDMARK_DATA);
  const totalCards = LANDMARK_CARDS.length;
  const collectedCards = LANDMARK_CARDS.filter((c) => c.obtained).length;
  const visitedProvinces = provinces.filter((p) => getProvinceProgress(p.key).collected > 0);

  const filtered = provinces.filter((p) => {
    const { collected } = getProvinceProgress(p.key);
    if (filter === 'visited') return collected > 0;
    if (filter === 'unvisited') return collected === 0;
    return true;
  });

  return (
    <ScrollView contentContainerStyle={styles.regionScroll} showsVerticalScrollIndicator={false}>
      {/* 전체 수집 현황 */}
      <View style={styles.statsCard}>
        <View style={styles.statsHeader}>
          <View>
            <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
              전체 수집 현황
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.textSecondary}>
              {visitedProvinces.length}개 지역 방문
            </CustomText>
          </View>
          <CustomText variant="Heading/H3" color={theme.colors.primary} style={styles.bold}>
            {collectedCards}/{totalCards}
          </CustomText>
        </View>
        <View style={styles.progressTrack}>
          <View style={[styles.progressFill, { width: `${Math.round((collectedCards / totalCards) * 100)}%` }]} />
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

      {/* 지역 리스트 */}
      {filtered.map((province) => {
        const { collected, total, percent } = getProvinceProgress(province.key);
        const complete = percent === 100;
        return (
          <TouchableOpacity
            key={province.key}
            style={styles.regionRow}
            onPress={() => onSelectRegion(province.key)}
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
                  {province.name}
                </CustomText>
                {complete && <Ionicons name="trophy" size={12} color={theme.colors.success} />}
              </View>
              <CustomText variant="Caption" color={theme.colors.textSecondary} style={styles.regionMeta}>
                {province.region} · {collected}/{total}장
              </CustomText>
              <View style={styles.regionProgressTrack}>
                <View
                  style={[
                    styles.progressFill,
                    complete && styles.progressFillComplete,
                    { width: `${percent}%` },
                  ]}
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
      })}
    </ScrollView>
  );
}

/** 카드 목록 탭 — 등급 필터 + 2열 카드 그리드 */
function CardGridTab({ onSelectCard }) {
  const [gradeFilter, setGradeFilter] = useState('All');

  const cards = useMemo(
    () => (gradeFilter === 'All' ? LANDMARK_CARDS : LANDMARK_CARDS.filter((c) => c.grade === gradeFilter)),
    [gradeFilter],
  );
  const collectedCount = LANDMARK_CARDS.filter((c) => c.obtained).length;

  return (
    <FlatList
      data={cards}
      keyExtractor={(item) => String(item.id)}
      numColumns={2}
      columnWrapperStyle={styles.cardColumnWrap}
      contentContainerStyle={styles.cardListContent}
      showsVerticalScrollIndicator={false}
      renderItem={({ item }) => (
        <LandmarkCardItem card={item} wishlisted={false} onPress={() => onSelectCard(item)} />
      )}
      ListHeaderComponent={
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
                    {g === 'All' ? '전체' : g}
                  </CustomText>
                </TouchableOpacity>
              );
            })}
          </ScrollView>
          <View style={styles.cardStatsRow}>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
              <CustomText variant="Body/Small" color={theme.colors.primary} style={styles.bold}>
                {collectedCount}
              </CustomText>
              {' '}/ {LANDMARK_CARDS.length}장 수집
            </CustomText>
            <View style={styles.gradeCountRow}>
              {GRADE_ORDER.map((g) => {
                const config = GRADE_CONFIG[g];
                const count = LANDMARK_CARDS.filter((c) => c.grade === g && c.obtained).length;
                return (
                  <View key={g} style={[styles.gradeCountPill, { backgroundColor: config.soft }]}>
                    <CustomText variant="Caption" color={config.color} style={styles.gradeCountText}>
                      {g[0]}{count}
                    </CustomText>
                  </View>
                );
              })}
            </View>
          </View>
        </View>
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
  // 지역 도감 탭
  regionScroll: {
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
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
  // 카드 목록 탭
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
});
