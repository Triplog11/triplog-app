import React from 'react';
import { StyleSheet, View, Text, Image } from 'react-native';
import Animated, { FadeInDown } from 'react-native-reanimated';
import theme from '../../../theme/theme';

const MIN_CELLS = 9;

function CollectCard({ landmark, index }) {
  const locked = !landmark.visited;

  return (
    <Animated.View
      entering={FadeInDown.delay(Math.min(index, 8) * 40).duration(theme.motion.normal)}
      style={styles.card}
    >
      {landmark.visited && landmark.img ? (
        <Image source={{ uri: landmark.img }} style={styles.image} />
      ) : (
        <View style={[styles.image, styles.placeholder]} />
      )}
      <Text style={[styles.name, locked && styles.lockedText]} numberOfLines={1}>
        {landmark.name}
      </Text>
      <Text style={[styles.addr, locked && styles.lockedText]} numberOfLines={1}>
        {landmark.addr}
      </Text>
    </Animated.View>
  );
}

/** EXPLORE-02 카드 그리드 — 3열, 미획득 카드는 잠금(회색) 처리. 9칸 최소 유지 */
export default function RegionCardGrid({ landmarks, regionName }) {
  const fillers = Array.from(
    { length: Math.max(0, MIN_CELLS - landmarks.length) },
    (_, i) => ({
      name: '???',
      addr: `${regionName} ...`,
      visited: false,
      key: `filler-${i}`,
    })
  );
  const cells = [...landmarks, ...fillers];

  return (
    <View style={styles.grid}>
      {cells.map((lm, index) => (
        <CollectCard key={lm.key || `${lm.name}-${index}`} landmark={lm} index={index} />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    rowGap: 12,
    columnGap: 10,
  },
  card: {
    width: '31%',
    flexGrow: 1,
    maxWidth: '32%',
  },
  image: {
    width: '100%',
    aspectRatio: 1,
    borderRadius: theme.rounded.lg,
    backgroundColor: theme.colors.surfaceDim,
  },
  placeholder: {
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  name: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.body,
    fontWeight: '700',
    color: theme.colors.text,
    marginTop: 8,
  },
  addr: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: 10,
    color: theme.colors.textSecondary,
    marginTop: 2,
  },
  lockedText: {
    color: theme.colors.textMuted,
  },
});
