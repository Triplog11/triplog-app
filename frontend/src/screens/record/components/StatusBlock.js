import React from 'react';
import { StyleSheet, View, ActivityIndicator, TouchableOpacity } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/** 목록 화면의 로딩 / 빈 상태 / 오류 상태 블록 */
export default function StatusBlock({ loading, message, actionLabel, onAction }) {
  return (
    <View style={styles.box}>
      {loading ? (
        <ActivityIndicator color={theme.colors.primary} />
      ) : (
        <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.text}>
          {message}
        </CustomText>
      )}
      {!loading && actionLabel && onAction && (
        <TouchableOpacity onPress={onAction} hitSlop={8} activeOpacity={0.8}>
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.action}>
            {actionLabel}
          </CustomText>
        </TouchableOpacity>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  box: {
    paddingTop: 60,
    paddingHorizontal: 30,
    alignItems: 'center',
    gap: theme.spacing.md,
  },
  text: {
    textAlign: 'center',
  },
  action: {
    fontWeight: 'bold',
  },
});
