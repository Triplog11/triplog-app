import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchRegionDetail } from '../../api/regions';
import LandmarkCardItem from './components/LandmarkCardItem';
import CardDetailModal from './components/CardDetailModal';
import PhotoPlaceholder from './components/PhotoPlaceholder';

/** 지역 도감 상세 — 히어로 + 수집 진행률 + 랜드마크 카드 그리드 (실 API) */
export default function RegionCollectionScreen({ route, navigation }) {
  const { regionId, regionName } = route.params ?? {};
  const [region, setRegion] = useState(null);
  const [status, setStatus] = useState('loading'); // loading | ready | error
  const [selectedCard, setSelectedCard] = useState(null);

  const load = useCallback(async () => {
    if (regionId == null) {
      setStatus('error');
      return;
    }
    setStatus('loading');
    try {
      const result = await fetchRegionDetail(regionId);
      setRegion(result ?? null);
      setStatus('ready');
    } catch (error) {
      setStatus('error');
    }
  }, [regionId]);

  useEffect(() => {
    load();
  }, [load]);

  const openVerify = () => {
    setSelectedCard(null);
    navigation.navigate('Record');
  };

  // 실 랜드마크 → 카드 카드 형태로 어댑트 (등급 없음)
  const items = region?.landmarks?.items ?? [];
  const cards = items.map((lm) => ({
    id: lm.landmarkId,
    landmarkId: lm.landmarkId,
    name: lm.landmarkName,
    obtained: lm.acquired,
    region: region?.regionName ?? regionName,
    grade: null,
  }));
  const total = cards.length;
  const collected = cards.filter((c) => c.obtained).length;
  const percent = total ? Math.round((collected / total) * 100) : 0;
  const complete = total > 0 && percent === 100;
  const displayName = region?.regionName ?? regionName ?? '지역';

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      {/* 히어로 */}
      <View style={styles.hero}>
        <PhotoPlaceholder icon="map-outline" size={40} />
        <View style={styles.heroOverlay} />
        <TouchableOpacity style={styles.backBtn} onPress={() => navigation.goBack()} hitSlop={8}>
          <Ionicons name="chevron-back" size={18} color="#FFFFFF" />
        </TouchableOpacity>
        <View style={styles.heroText}>
          <CustomText variant="Heading/H4" color="#FFFFFF" style={styles.bold}>
            {displayName}
          </CustomText>
          {region?.visited != null && (
            <CustomText variant="Caption" color="rgba(255,255,255,0.85)">
              {region.visited ? '방문한 지역' : '아직 방문 전'}
            </CustomText>
          )}
        </View>
      </View>

      {status === 'loading' && (
        <View style={styles.center}>
          <ActivityIndicator size="large" color={theme.colors.primary} />
        </View>
      )}

      {status === 'error' && (
        <View style={styles.center}>
          <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.centerText}>
            지역 정보를 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
          </CustomText>
          <TouchableOpacity style={styles.retryPill} onPress={load} activeOpacity={0.85}>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        </View>
      )}

      {status === 'ready' && (
        <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scrollContent}>
          {/* 수집 진행률 */}
          <View style={styles.progressCard}>
            <View style={styles.progressHeader}>
              <View>
                <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
                  수집 진행률
                </CustomText>
                <CustomText variant="Caption" color={theme.colors.textSecondary}>
                  {collected}/{total}장 수집
                </CustomText>
              </View>
              <CustomText
                variant="Heading/H3"
                color={complete ? theme.colors.success : theme.colors.primary}
                style={styles.bold}
              >
                {percent}%
              </CustomText>
            </View>
            <View style={styles.progressTrack}>
              <View
                style={[styles.progressFill, complete && styles.progressFillComplete, { width: `${percent}%` }]}
              />
            </View>
            {complete && (
              <View style={styles.completeRow}>
                <Ionicons name="trophy" size={13} color={theme.colors.success} />
                <CustomText variant="Caption" color={theme.colors.success} style={styles.bold}>
                  {displayName} 완전 정복!
                </CustomText>
              </View>
            )}
          </View>

          {region?.regionOverview ? (
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.description}>
              {region.regionOverview}
            </CustomText>
          ) : null}

          {/* 카드 그리드 */}
          <CustomText variant="Heading/H5" color={theme.colors.text} style={[styles.bold, styles.gridTitle]}>
            랜드마크 카드
          </CustomText>
          {total === 0 ? (
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.emptyText}>
              아직 등록된 랜드마크가 없어요. 곧 채워질 예정이에요!
            </CustomText>
          ) : (
            <View style={styles.grid}>
              {cards.map((card) => (
                <View key={card.id} style={styles.gridItem}>
                  <LandmarkCardItem card={card} wishlisted={false} onPress={() => setSelectedCard(card)} />
                </View>
              ))}
            </View>
          )}
        </ScrollView>
      )}

      <CardDetailModal card={selectedCard} onClose={() => setSelectedCard(null)} onVerifyPress={openVerify} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  scrollContent: {
    paddingBottom: 40,
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
  },
  retryPill: {
    borderRadius: theme.rounded.pill ?? 9999,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 8,
  },
  hero: {
    height: 150,
  },
  heroOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.3)',
  },
  backBtn: {
    position: 'absolute',
    top: 12,
    left: 16,
    width: 30,
    height: 30,
    borderRadius: 15,
    backgroundColor: 'rgba(0,0,0,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  heroText: {
    position: 'absolute',
    left: 16,
    right: 16,
    bottom: 12,
  },
  bold: {
    fontWeight: 'bold',
  },
  progressCard: {
    margin: theme.spacing.lg,
    marginBottom: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  progressHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  progressTrack: {
    height: 9,
    borderRadius: 5,
    backgroundColor: theme.colors.surfaceDim,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    borderRadius: 5,
    backgroundColor: theme.colors.primary,
  },
  progressFillComplete: {
    backgroundColor: theme.colors.success,
  },
  completeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
    marginTop: theme.spacing.sm,
  },
  description: {
    paddingHorizontal: theme.spacing.lg,
    marginBottom: theme.spacing.base,
  },
  gridTitle: {
    paddingHorizontal: theme.spacing.lg,
    marginBottom: theme.spacing.sm,
  },
  emptyText: {
    paddingHorizontal: theme.spacing.lg,
    paddingVertical: theme.spacing.lg,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    paddingHorizontal: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  gridItem: {
    width: '48%',
    flexGrow: 1,
  },
});
