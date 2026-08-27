import React from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/** 마이페이지 메뉴 한 줄 — 아이콘 + 라벨 + (우측 값 | 셰브런). onPress 없으면 정보 행. */
export default function MenuRow({ icon, label, value, onPress, last, labelColor }) {
  const content = (
    <>
      <View style={styles.menuLeft}>
        <View style={styles.menuIcon}>
          <Ionicons name={icon} size={18} color={theme.colors.primary} />
        </View>
        <CustomText variant="Body/Medium" color={labelColor ?? theme.colors.text} style={styles.menuTitle}>
          {label}
        </CustomText>
      </View>
      {value != null ? (
        <CustomText variant="Body/Small" color={theme.colors.textSecondary}>{value}</CustomText>
      ) : onPress ? (
        <Ionicons name="chevron-forward" size={16} color={theme.colors.textMuted} />
      ) : null}
    </>
  );

  if (!onPress) {
    return <View style={[styles.menuItem, last && styles.menuItemLast]}>{content}</View>;
  }
  return (
    <TouchableOpacity style={[styles.menuItem, last && styles.menuItemLast]} onPress={onPress} activeOpacity={0.7}>
      {content}
    </TouchableOpacity>
  );
}

/** 메뉴 그룹 컨테이너 (흰 카드) */
export function MenuGroup({ children }) {
  return <View style={styles.menuContainer}>{children}</View>;
}

const styles = StyleSheet.create({
  menuContainer: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    overflow: 'hidden',
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: theme.spacing.base,
    paddingHorizontal: theme.spacing.base,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.border,
  },
  menuItemLast: {
    borderBottomWidth: 0,
  },
  menuLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  menuIcon: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: theme.colors.primarySoft,
    justifyContent: 'center',
    alignItems: 'center',
  },
  menuTitle: {
    fontWeight: '600',
  },
});
