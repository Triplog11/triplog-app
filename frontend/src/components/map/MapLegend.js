import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import theme from '../../theme/theme';
import { SCALE_COLORS, SCALE_LOCATIONS } from '../../utils/mapColor';

/** 범례 행이 차지하는 높이 — 지역 정보 카드를 이만큼 띄워 겹치지 않게 한다 */
export const MAP_LEGEND_HEIGHT = 32;

/**
 * 지도 하단 방문률 범례 — 왼쪽 "미방문", 오른쪽 "100%", 사이에 색 스케일 막대.
 * 막대 색은 지도가 쓰는 theme.mapScale을 그대로 받아 두 색이 항상 같다.
 */
export default function MapLegend({ style }) {
  return (
    <View style={[styles.row, style]} pointerEvents="none">
      <Text style={styles.label}>미방문</Text>
      <LinearGradient
        colors={SCALE_COLORS}
        locations={SCALE_LOCATIONS}
        start={{ x: 0, y: 0.5 }}
        end={{ x: 1, y: 0.5 }}
        style={styles.bar}
      />
      <Text style={styles.label}>100%</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    height: MAP_LEGEND_HEIGHT,
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    paddingHorizontal: theme.spacing.md,
  },
  bar: {
    flex: 1,
    height: 8,
    borderRadius: theme.rounded.full,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  label: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.caption,
    fontWeight: '600',
    color: theme.colors.textSecondary,
  },
});
