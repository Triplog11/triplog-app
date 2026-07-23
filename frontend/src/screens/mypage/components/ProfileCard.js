import React from 'react';
import { StyleSheet, View, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 마이페이지 상단 프로필 카드.
 * 레이아웃: 서팍 피그마 마이페이지 V1.0.0 (색상은 DESIGN.md 토큰으로 치환)
 */
export default function ProfileCard({ user, rank, onEditPress }) {
  const nickname = user?.nickname ?? '여행자';
  const level = user?.level ?? 1;
  const tierLabel = user?.tier ? `${user.tier} Rank` : rank.tierLabel;

  return (
    <View style={styles.card}>
      <TouchableOpacity style={styles.editBtn} onPress={onEditPress} activeOpacity={0.8} hitSlop={8}>
        <Ionicons name="pencil" size={16} color="#FFFFFF" />
      </TouchableOpacity>

      <View style={styles.avatar}>
        <Ionicons name="person" size={40} color={theme.colors.primary} />
      </View>

      <CustomText variant="Heading/H2" color="#FFFFFF" style={styles.nickname}>
        {nickname}
      </CustomText>
      <CustomText variant="Body/Small" color="rgba(255,255,255,0.85)" style={styles.level}>
        Lv.{level}
      </CustomText>

      <View style={styles.bottomRow}>
        <View style={styles.tierChip}>
          <CustomText variant="Label/Medium" color={theme.colors.accentGold} style={styles.tierText}>
            🏆 {tierLabel}
          </CustomText>
        </View>
        <View style={styles.scoreGroup}>
          <CustomText variant="Caption" color="rgba(255,255,255,0.85)">
            월간 {rank.monthlyRank}위
          </CustomText>
          <CustomText variant="Heading/H4" color="#FFFFFF" style={styles.score}>
            {rank.totalScore.toLocaleString()}점
          </CustomText>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.xl,
    padding: theme.spacing.lg,
    alignItems: 'center',
  },
  editBtn: {
    position: 'absolute',
    top: theme.spacing.base,
    right: theme.spacing.base,
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: 'rgba(255,255,255,0.2)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  avatar: {
    width: 88,
    height: 88,
    borderRadius: 44,
    backgroundColor: '#FFFFFF',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing.base,
  },
  nickname: {
    marginTop: theme.spacing.base,
    fontWeight: 'bold',
  },
  level: {
    marginTop: 2,
    fontWeight: '600',
  },
  bottomRow: {
    flexDirection: 'row',
    alignItems: 'flex-end',
    justifyContent: 'space-between',
    alignSelf: 'stretch',
    marginTop: theme.spacing.lg,
  },
  tierChip: {
    backgroundColor: '#FFFFFF',
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 14,
    paddingVertical: 7,
  },
  tierText: {
    fontWeight: 'bold',
  },
  scoreGroup: {
    alignItems: 'flex-end',
  },
  score: {
    fontWeight: 'bold',
  },
});
