import React from 'react';
import { StyleSheet, View } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { tierOf } from '../../../data/ranking';
import { initialOf } from './RankingPodium';

const WHITE_SOFT = 'rgba(255,255,255,0.85)';

/**
 * 내 순위 카드 — 하단 고정.
 * @param {object} myRank fetchMyRanking 응답 {nickname, totalRank, monthlyRank, overallScore, monthScore, level, tier, nextTier, requiredScore}
 * @param {boolean} isMonthly 월간 탭 여부
 * @param {object} fallbackUser 로그인 유저 (닉네임/레벨 보정용)
 */
export default function MyRankingCard({ myRank, isMonthly, fallbackUser }) {
  const nickname = myRank.nickname ?? fallbackUser?.nickname ?? '나';
  const rank = isMonthly ? myRank.monthlyRank : myRank.totalRank;
  const score = isMonthly ? myRank.monthScore : myRank.overallScore;
  const tier = tierOf(myRank.tier);
  const nextTier = myRank.nextTier ? tierOf(myRank.nextTier) : null;
  const remaining = Math.max((myRank.requiredScore ?? 0) - (myRank.overallScore ?? 0), 0);

  return (
    <View style={styles.card}>
      <View style={styles.mainRow}>
        <CustomText variant="Label/Medium" color="#FFFFFF" style={styles.rankNum}>
          {rank ?? '-'}
        </CustomText>
        <View style={styles.avatar}>
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
            {initialOf(nickname)}
          </CustomText>
        </View>
        <View style={styles.info}>
          <CustomText variant="Label/Medium" color="#FFFFFF" style={styles.bold} numberOfLines={1}>
            {nickname} (나)
          </CustomText>
          <CustomText variant="Caption" color={WHITE_SOFT}>
            Lv.{myRank.level ?? fallbackUser?.level ?? 1} · {tier.label}
          </CustomText>
        </View>
        <CustomText variant="Label/Medium" color="#FFFFFF" style={styles.bold}>
          {(score ?? 0).toLocaleString()}
        </CustomText>
      </View>
      {nextTier && (
        <CustomText variant="Caption" color={WHITE_SOFT} style={styles.nextTier}>
          {nextTier.label}까지 {remaining.toLocaleString()}점 남았어요
        </CustomText>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  bold: { fontWeight: 'bold' },
  card: {
    backgroundColor: theme.colors.primary,
    marginHorizontal: theme.spacing.lg,
    marginBottom: theme.spacing.base,
    borderRadius: theme.rounded.card,
    padding: theme.spacing.sm,
    gap: 6,
  },
  mainRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
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
    backgroundColor: '#FFFFFF',
    justifyContent: 'center',
    alignItems: 'center',
  },
  info: {
    flex: 1,
    gap: 1,
  },
  nextTier: {
    paddingLeft: 24 + theme.spacing.sm,
  },
});
