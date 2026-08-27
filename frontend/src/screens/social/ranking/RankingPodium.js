import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { tierOf } from '../../../data/ranking';

const BLOCK_HEIGHT = { 1: 78, 2: 58, 3: 44 };

export function initialOf(nickname) {
  return (nickname ?? '나').slice(0, 1);
}

/** TOP 3 시상대 — 2위, 1위, 3위 순으로 배치 */
export default function RankingPodium({ top3 }) {
  const ordered = [top3[1], top3[0], top3[2]];
  return (
    <View style={styles.podium}>
      {ordered.map((player) => {
        if (!player) return null;
        const isFirst = player.rank === 1;
        const tier = tierOf(player.tier);
        return (
          <View key={player.rank} style={styles.podiumItem}>
            {isFirst && (
              <Ionicons name="ribbon" size={18} color={theme.colors.accentGold} style={styles.crown} />
            )}
            <View
              style={[
                styles.podiumAvatar,
                isFirst && styles.podiumAvatarFirst,
                { backgroundColor: tier.soft, borderColor: tier.color },
              ]}
            >
              <CustomText variant="Heading/H5" color={tier.color} style={styles.bold}>
                {initialOf(player.nickname)}
              </CustomText>
            </View>
            <CustomText
              variant="Caption"
              color={theme.colors.text}
              style={[styles.bold, styles.podiumName]}
              numberOfLines={1}
            >
              {player.nickname}
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.textSecondary}>
              {(player.score ?? 0).toLocaleString()}점
            </CustomText>
            <View
              style={[
                styles.podiumBlock,
                { height: BLOCK_HEIGHT[player.rank] ?? 44, backgroundColor: tier.color },
              ]}
            >
              <CustomText variant="Heading/H3" color="#FFFFFF" style={styles.podiumRank}>
                {player.rank}
              </CustomText>
            </View>
          </View>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  bold: { fontWeight: 'bold' },
  podium: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    justifyContent: 'center',
    gap: theme.spacing.sm,
    marginBottom: theme.spacing.lg,
  },
  podiumItem: {
    flex: 1,
    alignItems: 'center',
    gap: 3,
  },
  crown: { marginBottom: 2 },
  podiumAvatar: {
    width: 48,
    height: 48,
    borderRadius: 24,
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
  },
  podiumAvatarFirst: {
    width: 60,
    height: 60,
    borderRadius: 30,
  },
  podiumName: {
    marginTop: 4,
    maxWidth: '100%',
  },
  podiumBlock: {
    alignSelf: 'stretch',
    marginTop: 6,
    borderTopLeftRadius: theme.rounded.md,
    borderTopRightRadius: theme.rounded.md,
    justifyContent: 'center',
    alignItems: 'center',
  },
  podiumRank: {
    fontWeight: 'bold',
    opacity: 0.85,
  },
});
