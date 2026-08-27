import React from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/** 인증 플로우 공통 상단 바 — 뒤로가기 + 가운데 제목 */
export default function ScreenHeader({ title, onBack }) {
  return (
    <View style={styles.header}>
      {onBack ? (
        <TouchableOpacity onPress={onBack} hitSlop={10} style={styles.backBtn}>
          <Ionicons name="chevron-back" size={22} color={theme.colors.text} />
        </TouchableOpacity>
      ) : (
        <View style={styles.backBtn} />
      )}
      <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
        {title}
      </CustomText>
      <View style={styles.backBtn} />
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: theme.spacing.base,
    paddingVertical: theme.spacing.sm,
  },
  backBtn: {
    width: 32,
    height: 32,
    justifyContent: 'center',
  },
  bold: {
    fontWeight: 'bold',
  },
});
