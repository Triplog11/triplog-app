import React from 'react';
import { StyleSheet, TouchableOpacity, ActivityIndicator } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/** 계정 화면 풀폭 Primary CTA — 전송 중에는 폭을 유지한 채 인디케이터로 교체 */
export default function AuthSubmitButton({ label, onPress, disabled, loading }) {
  const inactive = disabled || loading;
  return (
    <TouchableOpacity
      style={[styles.btn, disabled && styles.btnDisabled]}
      disabled={inactive}
      onPress={onPress}
      activeOpacity={0.9}
      accessibilityRole="button"
      accessibilityState={{ disabled: inactive, busy: loading }}
    >
      {loading ? (
        <ActivityIndicator size="small" color="#FFFFFF" />
      ) : (
        <CustomText
          variant="UI/Button"
          color={disabled ? theme.colors.textMuted : '#FFFFFF'}
          style={styles.text}
        >
          {label}
        </CustomText>
      )}
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  btn: {
    height: 56,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.cta,
    justifyContent: 'center',
    alignItems: 'center',
  },
  btnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
  },
  text: {
    fontWeight: 'bold',
  },
});
