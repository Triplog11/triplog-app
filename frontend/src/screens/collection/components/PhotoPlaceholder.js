import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import theme from '../../../theme/theme';

/**
 * 사진 자리 표시 — 랜드마크 사진 API가 아직 없어 사용.
 * 실제 이미지가 들어오면 <Image>로 교체한다.
 */
export default function PhotoPlaceholder({ tint, icon = 'image-outline', size = 26, style }) {
  return (
    <View style={[styles.box, tint ? { backgroundColor: tint } : null, style]}>
      <Ionicons name={icon} size={size} color={theme.colors.textMuted} />
    </View>
  );
}

const styles = StyleSheet.create({
  box: {
    flex: 1,
    width: '100%',
    height: '100%',
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
