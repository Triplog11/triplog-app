import React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { Feather } from '@expo/vector-icons';
import theme from '../../../theme/theme';

/** EXPLORE-02 통계 카드 2개 — 아이콘 원 + 값 + 라벨, 틸 보더 */
export default function RegionStatCards({ percent, style }) {
  const items = [
    { value: `${percent}%`, label: '총 방문횟수' },
    { value: `${percent}%`, label: '지역 달성률' },
  ];

  return (
    <View style={[styles.row, style]}>
      {items.map((item) => (
        <View key={item.label} style={styles.card}>
          <View style={styles.iconCircle}>
            <Feather name="crosshair" size={18} color={theme.colors.white} />
          </View>
          <View style={styles.textCol}>
            <Text style={styles.value}>{item.value}</Text>
            <Text style={styles.label}>{item.label}</Text>
          </View>
        </View>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    gap: 10,
  },
  card: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingVertical: 14,
    paddingHorizontal: 12,
    borderWidth: 1.5,
    borderColor: theme.colors.primary,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.white,
    minWidth: 0,
  },
  iconCircle: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: theme.colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  textCol: {
    gap: 2,
    minWidth: 0,
    flexShrink: 1,
  },
  value: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.xl,
    fontWeight: '700',
    lineHeight: Math.round(theme.typography.size.xl * 1.2),
    color: theme.colors.text,
  },
  label: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.xs,
    fontWeight: '500',
    color: theme.colors.textSecondary,
  },
});
