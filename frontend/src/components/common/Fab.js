import React from 'react';
import { StyleSheet } from 'react-native';
import { Feather } from '@expo/vector-icons';
import PressableScale from './PressableScale';
import theme from '../../theme/theme';

const SIZE = 44;

/**
 * Floating Locate Button — DESIGN.md §4: 44px 흰 원형, 민트 아이콘, Sharp 그림자.
 * 네이버지도 위치 버튼 UX: 탭 사이클(내 위치 → 나침반 모드 → 북쪽 복귀).
 * active(나침반 모드)일 땐 민트 배경 + 흰 아이콘으로 상태를 표시.
 */
export default function Fab({
  icon = 'crosshair',
  onPress,
  style,
  accessibilityLabel,
  active = false,
}) {
  return (
    <PressableScale
      pressScale={0.94}
      onPress={onPress}
      style={[styles.button, active && styles.buttonActive, style]}
      accessibilityLabel={accessibilityLabel}
    >
      <Feather
        name={icon}
        size={20}
        color={active ? theme.colors.white : theme.colors.primary}
      />
    </PressableScale>
  );
}

const styles = StyleSheet.create({
  button: {
    width: SIZE,
    height: SIZE,
    borderRadius: SIZE / 2,
    backgroundColor: theme.colors.white,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 1,
    borderColor: theme.colors.border,
    ...theme.shadow.sharp,
  },
  buttonActive: {
    backgroundColor: theme.colors.primary,
    borderColor: theme.colors.primaryDark,
  },
});
