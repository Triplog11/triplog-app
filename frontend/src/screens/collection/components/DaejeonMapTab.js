import React from 'react';
import { StyleSheet, View, ScrollView } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import KoreaMap from './KoreaMap';

/**
 * 도감 지도 탭 — 기존 대전 구 단위 지도 유지 (임시).
 * TODO: 전국 지도(홈 KoreaMap)로 통합 예정 — 중복 지도 정리(Phase 3)
 */
const DAEJEON_REGIONS = [
  { id: '1', name: '유성구', province: '대전광역시', progress: 100, completed: true, visitedCount: 3, totalCount: 3 },
  { id: '2', name: '중구', province: '대전광역시', progress: 80, completed: false, visitedCount: 4, totalCount: 5 },
  { id: '3', name: '서구', province: '대전광역시', progress: 60, completed: false, visitedCount: 3, totalCount: 5 },
  { id: '4', name: '동구', province: '대전광역시', progress: 30, completed: false, visitedCount: 1, totalCount: 3 },
  { id: '5', name: '대덕구', province: '대전광역시', progress: 0, completed: false, visitedCount: 0, totalCount: 4 },
];

export default function DaejeonMapTab({ onRegionPress }) {
  return (
    <View style={styles.wrapper}>
      <ScrollView
        contentContainerStyle={styles.scroll}
        maximumZoomScale={2.5}
        minimumZoomScale={1}
        showsVerticalScrollIndicator={false}
      >
        <KoreaMap regions={DAEJEON_REGIONS} onRegionPress={onRegionPress} />
      </ScrollView>
      <View style={styles.guideCard}>
        <View style={styles.guideItem}>
          <View style={[styles.guideColor, { backgroundColor: theme.colors.primary }]} />
          <CustomText variant="Caption" color={theme.colors.textSecondary}>완료(100%)</CustomText>
        </View>
        <View style={styles.guideItem}>
          <View style={[styles.guideColor, styles.guideInProgress]} />
          <CustomText variant="Caption" color={theme.colors.textSecondary}>진행 중</CustomText>
        </View>
        <View style={styles.guideItem}>
          <View style={[styles.guideColor, { backgroundColor: theme.colors.surface }]} />
          <CustomText variant="Caption" color={theme.colors.textSecondary}>미방문</CustomText>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
  },
  scroll: {
    alignItems: 'center',
    paddingVertical: theme.spacing.base,
  },
  guideCard: {
    position: 'absolute',
    bottom: theme.spacing.base,
    alignSelf: 'center',
    flexDirection: 'row',
    gap: theme.spacing.base,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.pill,
    borderWidth: 1,
    borderColor: theme.colors.border,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: theme.spacing.sm,
  },
  guideItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
  },
  guideColor: {
    width: 12,
    height: 12,
    borderRadius: 4,
  },
  guideInProgress: {
    backgroundColor: theme.colors.blueTint,
    borderColor: theme.colors.primary,
    borderWidth: 1,
  },
});
