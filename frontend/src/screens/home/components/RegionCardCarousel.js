import React, { useState } from 'react';
import { StyleSheet, View, Text, Image } from 'react-native';
import Animated, { FadeIn } from 'react-native-reanimated';
import PressableScale from '../../../components/common/PressableScale';
import theme from '../../../theme/theme';

const CARD_WIDTH = 280;

/**
 * EXPLORE-02 캐러셀 뷰 — 뒤로 겹친 스택 카드. 카드 탭 시 다음 방문 랜드마크로 순환.
 */
export default function RegionCardCarousel({ landmarks, collected, total, date, meta }) {
  const visited = landmarks.filter((lm) => lm.visited);
  const [index, setIndex] = useState(Math.max(0, visited.length - 1));
  const current = visited[index] || landmarks[0];

  const handleNext = () => {
    if (visited.length > 1) {
      setIndex((prev) => (prev + 1) % visited.length);
    }
  };

  return (
    <View>
      <View style={styles.stack}>
        <View style={[styles.layer, styles.layerBack]} />
        <View style={[styles.layer, styles.layerMid]} />
        <PressableScale
          pressScale={0.98}
          onPress={handleNext}
          style={styles.card}
          accessibilityLabel="다음 랜드마크 카드"
        >
          <Animated.View key={current?.name || 'empty'} entering={FadeIn.duration(theme.motion.normal)}>
            <View style={styles.imageWrap}>
              {current?.img ? (
                <Image source={{ uri: current.img }} style={styles.image} />
              ) : (
                <View style={[styles.image, styles.imageEmpty]} />
              )}
              <View style={styles.imageDim} pointerEvents="none" />
              <View style={styles.chip}>
                <Text style={styles.chipText}>모두</Text>
              </View>
            </View>
            <View style={styles.info}>
              <Text style={styles.name}>{current?.name || '???'}</Text>
              <Text style={styles.addr}>{current?.addr || ''}</Text>
            </View>
          </Animated.View>
        </PressableScale>
      </View>

      <Text style={styles.pager}>
        {collected} / {total}
      </Text>

      <View style={styles.infoBox}>
        <Text style={styles.infoDate}>{date}</Text>
        <Text style={styles.infoMeta}>{meta}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  stack: {
    width: '100%',
    maxWidth: CARD_WIDTH,
    alignSelf: 'center',
    marginTop: 8,
    minHeight: 320,
  },
  layer: {
    position: 'absolute',
    top: 0,
    alignSelf: 'center',
    width: '88%',
    height: 280,
    borderRadius: theme.rounded.lg,
    backgroundColor: theme.colors.surfaceDim,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  layerBack: {
    top: 24,
    opacity: 0.45,
    transform: [{ translateX: -14 }, { scale: 0.92 }],
  },
  layerMid: {
    top: 12,
    opacity: 0.7,
    transform: [{ translateX: -7 }, { scale: 0.96 }],
  },
  card: {
    backgroundColor: theme.colors.white,
    borderRadius: theme.rounded.lg,
    borderWidth: 1,
    borderColor: theme.colors.border,
    overflow: 'hidden',
    maxWidth: CARD_WIDTH,
    alignSelf: 'center',
    width: '100%',
  },
  imageWrap: {
    aspectRatio: 4 / 5,
    overflow: 'hidden',
  },
  image: {
    width: '100%',
    height: '100%',
  },
  imageEmpty: {
    backgroundColor: theme.colors.surfaceDim,
  },
  imageDim: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(30, 60, 80, 0.25)',
  },
  chip: {
    position: 'absolute',
    top: 12,
    right: 12,
    paddingVertical: 4,
    paddingHorizontal: 12,
    borderRadius: theme.rounded.full,
    backgroundColor: theme.colors.accentMint,
  },
  chipText: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.xs,
    fontWeight: '600',
    color: theme.colors.white,
  },
  info: {
    paddingHorizontal: 16,
    paddingTop: 14,
    paddingBottom: 16,
  },
  name: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.md,
    fontWeight: '700',
    color: theme.colors.text,
  },
  addr: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.xs,
    color: theme.colors.textSecondary,
    marginTop: 4,
  },
  pager: {
    textAlign: 'center',
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.sm,
    fontWeight: '600',
    color: theme.colors.textSecondary,
    marginTop: 16,
    marginBottom: 20,
  },
  infoBox: {
    paddingVertical: 18,
    paddingHorizontal: 20,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.rounded.lg,
    backgroundColor: theme.colors.white,
    marginBottom: 20,
  },
  infoDate: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.md,
    fontWeight: '700',
    color: theme.colors.text,
    marginBottom: 8,
  },
  infoMeta: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.xs,
    color: theme.colors.textSecondary,
    lineHeight: 18,
  },
});
