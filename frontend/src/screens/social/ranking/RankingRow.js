import React from 'react';
import { StyleSheet, View } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { tierOf } from '../../../data/ranking';
import { initialOf } from './RankingPodium';

/** 4위 이하 랭킹 한 줄 */
export default function RankingRow({ player }) {
  const tier = tierOf(player.tier);
  return (
    <View style={styles.row}>
      <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={styles.rankNum}>
        {player.rank}
      </CustomText>
      <View style={[styles.avatar, { backgroundColor: tier.soft }]}>
        <CustomText variant="Label/Medium" color={tier.color} style={styles.bold}>
          {initialOf(player.nickname)}
        </CustomText>
      </View>
      <View style={styles.rowInfo}>
        <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
          {player.nickname}
        </CustomText>
        <CustomText variant="Caption" color={theme.colors.textSecondary}>
          Lv.{player.level ?? 1}
        </CustomText>
      </View>
      <View style={styles.rowRight}>
        <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
          {(player.score ?? 0).toLocaleString()}
        </CustomText>
        <View style={[styles.tierPill, { backgroundColor: tier.soft }]}>
          <CustomText variant="Caption" color={tier.color} style={styles.tierText}>
            {tier.label}
          </CustomText>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  bold: { fontWeight: 'bold' },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.sm,
  },
  rankNum: {
    width: 24,
    textAlign: 'center',
    fontWeight: 'bold',
  },
  avatar: {
    width: 36,
    height: 36,
    borderRadius: 18,
    justifyContent: 'center',
    alignItems: 'center',
  },
  rowInfo: {
    flex: 1,
    gap: 1,
  },
  rowRight: {
    alignItems: 'flex-end',
    gap: 3,
  },
  tierPill: {
    borderRadius: theme.rounded.sm,
    paddingHorizontal: 7,
    paddingVertical: 2,
  },
  tierText: {
    fontWeight: 'bold',
    fontSize: 10,
  },
});
