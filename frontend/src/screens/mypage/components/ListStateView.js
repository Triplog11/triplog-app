import React from 'react';
import { StyleSheet, View, ActivityIndicator, TouchableOpacity, Image } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 목록 화면 공용 상태 뷰 — 로딩 / 에러(다시 시도) / 빈 상태(+선택 CTA 및 일러스트).
 * 렌더할 상태가 없으면 null을 돌려준다.
 */
export default function ListStateView({
  loading, errorMessage, empty, emptyText, emptyHint, image, ctaLabel, onCta, onRetry,
}) {
  if (loading) {
    return (
      <View style={styles.centerBox}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
      </View>
    );
  }
  if (errorMessage) {
    return (
      <View style={styles.centerBox}>
        <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.text}>
          {errorMessage}
        </CustomText>
        {onRetry && (
          <TouchableOpacity style={styles.btn} onPress={onRetry} activeOpacity={0.8}>
            <CustomText variant="UI/Button/Small" color={theme.colors.white} style={styles.btnText}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        )}
      </View>
    );
  }
  if (empty) {
    return (
      <View style={styles.centerBox}>
        {image && (
          <Image
            source={image}
            style={styles.emptyIllustration}
            resizeMode="contain"
          />
        )}
        <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.text}>
          {emptyText}
        </CustomText>
        {emptyHint ? (
          <CustomText variant="Body/Small" color={theme.colors.textMuted} style={styles.text}>
            {emptyHint}
          </CustomText>
        ) : null}
        {ctaLabel && onCta && (
          <TouchableOpacity style={styles.btn} onPress={onCta} activeOpacity={0.85}>
            <CustomText variant="UI/Button/Small" color={theme.colors.white} style={styles.btnText}>
              {ctaLabel}
            </CustomText>
          </TouchableOpacity>
        )}
      </View>
    );
  }
  return null;
}

const styles = StyleSheet.create({
  centerBox: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: theme.spacing.section,
    paddingVertical: theme.spacing.section,
    gap: theme.spacing.md,
  },
  emptyIllustration: {
    width: 180,
    height: 120,
    marginBottom: theme.spacing.sm,
  },
  text: {
    textAlign: 'center',
  },
  btn: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: theme.spacing.xl,
    height: 40,
    justifyContent: 'center',
    marginTop: theme.spacing.xs,
  },
  btnText: {
    fontWeight: 'bold',
  },
});
