import React, { useState, useEffect } from 'react';
import { StyleSheet, Image } from 'react-native';
import theme from '../../../theme/theme';
import { CardAssets } from '../../../assets';
import { optimizeImageUrl } from '../../../utils/imageUrl';

/**
 * 카드/지역 사진 슬롯.
 * uri가 있으면 실제 이미지를, 없거나 로드에 실패하면 기본 랜드마크 카드 이미지를 보여준다.
 * variant는 표시 용도로, Cloudinary 이미지를 어느 폭으로 내려받을지 결정한다.
 *
 * @param {{uri?: string|null, variant?: 'thumb'|'card'|'hero', style?: object}} props
 */
export default function PhotoPlaceholder({ uri, variant = 'card', style }) {
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    setFailed(false);
  }, [uri]);

  if (uri && !failed) {
    return (
      <Image
        source={{ uri: optimizeImageUrl(uri, variant) }}
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
