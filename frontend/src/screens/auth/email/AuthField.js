import React from 'react';
import { StyleSheet, View, TextInput, TouchableOpacity } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 계정 화면 공용 입력 필드 — 라벨 + 입력 + (선택) 우측 액션 버튼 + 헬퍼 텍스트.
 * @param {{state: 'idle'|'error'|'success', message: string}} [helper]
 * @param {{label: string, onPress: () => void, disabled?: boolean}} [action] 중복확인 등 우측 버튼
 */
export default function AuthField({
  label,
  helper,
  action,
  style,
  onFocus,
  onBlur,
  ...inputProps
}) {
  const [focused, setFocused] = React.useState(false);
  const isError = helper?.state === 'error';
  const helperColor = isError ? theme.colors.error : theme.colors.success;

  const handleFocus = (e) => {
    setFocused(true);
    onFocus?.(e);
  };

  const handleBlur = (e) => {
    setFocused(false);
    onBlur?.(e);
  };

  return (
    <View style={style}>
      <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={styles.label}>
        {label}
      </CustomText>
      <View style={styles.row}>
        <TextInput
          style={[
            styles.input,
            focused && styles.inputFocused,
            isError && styles.inputError,
          ]}
          placeholderTextColor={theme.colors.textMuted}
          autoCapitalize="none"
          autoCorrect={false}
          onFocus={handleFocus}
          onBlur={handleBlur}
          {...inputProps}
        />
        {action && (
          <TouchableOpacity
            style={[styles.actionBtn, action.disabled && styles.actionBtnDisabled]}
            disabled={action.disabled}
            onPress={action.onPress}
            activeOpacity={0.85}
          >
            <CustomText
              variant="UI/Button/Small"
              color={action.disabled ? theme.colors.textMuted : '#FFFFFF'}
              style={styles.actionText}
            >
              {action.label}
            </CustomText>
          </TouchableOpacity>
        )}
      </View>
      {!!helper?.message && (
        <CustomText variant="Body/Small" color={helperColor} style={styles.helper}>
          {helper.message}
        </CustomText>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  label: {
    marginBottom: 8,
    fontWeight: '600',
  },
  row: {
    flexDirection: 'row',
    gap: 8,
  },
  input: {
    flex: 1,
    height: 56,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
    paddingHorizontal: 16,
    color: theme.colors.text,
    fontSize: 15,
    fontFamily: 'Pretendard-Regular',
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  inputFocused: {
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.canvas,
  },
  inputError: {
    borderWidth: 2,
    borderColor: theme.colors.error,
  },
  actionBtn: {
    width: 92,
    height: 56,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.card,
    justifyContent: 'center',
    alignItems: 'center',
  },
  actionBtnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
  },
  actionText: {
    fontWeight: 'bold',
  },
  helper: {
    marginTop: 8,
  },
});
