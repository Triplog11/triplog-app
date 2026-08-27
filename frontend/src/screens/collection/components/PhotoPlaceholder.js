import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Image } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import theme from '../../../theme/theme';
import { CardAssets } from '../../../assets';

/**
 * 카드/지역 사진 슬롯.
 * uri가 있으면 실제 이미지를, 없거나 로드에 실패하면 기본 랜드마크 카드 이미지를 보여준다.
 */
export default function PhotoPlaceholder({ uri, tint, icon = 'image-outline', size = 26, style }) {
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    setFailed(false);
  }, [uri]);

  if (uri && !failed) {
    return (
      <Image
        source={{ uri }}
        style={[styles.box, style]}
        resizeMode="cover"
        onError={() => setFailed(true)}
        accessibilityIgnoresInvertColors
      />
    );
  }

  return (
    <Image
      source={CardAssets.defaultCard}
      style={[styles.box, style]}
      resizeMode="cover"
      accessibilityIgnoresInvertColors
    />
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
