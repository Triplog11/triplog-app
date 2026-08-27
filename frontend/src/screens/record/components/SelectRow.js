import React from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 지역/랜드마크 선택 목록의 공통 행.
 * @param highlighted 방문·획득 등 강조 상태 (아이콘 색 + 배경)
 * @param badge 오른쪽 작은 상태 라벨 (없으면 표시 안 함)
 */
export default function SelectRow({ icon, title, subtitle, highlighted, badge, onPress }) {
  return (
    <TouchableOpacity style={styles.row} onPress={onPress} activeOpacity={0.85}>
      <View style={[styles.thumb, highlighted && styles.thumbHighlighted]}>
        <Ionicons name={icon} size={20} color={highlighted ? theme.colors.primary : theme.colors.textMuted} />
      </View>
      <View style={styles.rowInfo}>
        <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
          {title}
        </CustomText>
        <CustomText variant="Caption" color={theme.colors.textSecondary} numberOfLines={1}>
          {subtitle}
        </CustomText>
      </View>
      {badge ? (
        <View style={styles.badge}>
          <CustomText variant="Caption" color={theme.colors.success} style={styles.bold}>
            {badge}
          </CustomText>
        </View>
      ) : null}
      <Ionicons name="chevron-forward" size={14} color={theme.colors.textMuted} />
    </TouchableOpacity>
  );
}

/** 목록 contentContainerStyle — 탭바의 가운데 인증 플로팅 버튼과 겹치지 않도록 하단 여백 확보 */
export const listContentStyle = {
  padding: theme.spacing.lg,
  paddingBottom: 104,
  gap: theme.spacing.sm,
};

const styles = StyleSheet.create({
  bold: {
    fontWeight: 'bold',
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.sm,
  },
  thumb: {
    width: 44,
    height: 44,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
  thumbHighlighted: {
    backgroundColor: theme.colors.primarySoft,
  },
  rowInfo: {
    flex: 1,
    gap: 2,
  },
  badge: {
    borderRadius: theme.rounded.sm,
    paddingHorizontal: 8,
    paddingVertical: 3,
    backgroundColor: theme.colors.primarySoft,
  },
});
