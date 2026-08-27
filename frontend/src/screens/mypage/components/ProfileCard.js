import React from 'react';
import { StyleSheet, View, TouchableOpacity, Image } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 마이페이지 상단 프로필 카드 — GET /users/mypage 요약 기반.
 * 레이아웃: 서팍 피그마 마이페이지 V1.0.0 (색상은 DESIGN.md 토큰으로 치환)
 * @param {object|null} summary  fetchMyPage 응답 (미도착 시 null → '--' 표기)
 */
export default function ProfileCard({
  summary, fallbackNickname, onEditPress, onAppellationPress, onBadgePress,
}) {
  const nickname = summary?.nickname ?? fallbackNickname ?? '여행자';
  const level = summary?.level != null ? `Lv.${summary.level}` : 'Lv.--';
  const tierLabel = summary?.tier ? `${summary.tier} Rank` : '—';
  const score = summary?.overallScore != null ? `${summary.overallScore.toLocaleString()}점` : '--점';
  const monthScore = summary?.monthScore != null ? `${summary.monthScore.toLocaleString()}점` : '--점';
  const appellation = summary?.representativeAppellation ?? null;
  const badge = summary?.representativeBadge ?? null;
  const profileUrl = summary?.profileUrl ?? null;

  return (
    <View style={styles.card}>
      <TouchableOpacity style={styles.editBtn} onPress={onEditPress} activeOpacity={0.8} hitSlop={8}>
        <Ionicons name="pencil" size={16} color={theme.colors.white} />
      </TouchableOpacity>

      <View style={styles.avatarRow}>
        <View style={styles.avatar}>
          {profileUrl ? (
            <Image source={{ uri: profileUrl }} style={styles.avatarImage} />
          ) : (
            <Ionicons name="person" size={40} color={theme.colors.primary} />
          )}
        </View>
        {/* 대표 뱃지 — 아바타 우하단, 탭하면 뱃지 보관함 */}
        <TouchableOpacity
          style={styles.badgeSlot}
          onPress={onBadgePress}
          activeOpacity={0.8}
          hitSlop={6}
          accessibilityLabel={badge ? `대표 뱃지 ${badge.badgeName}` : '대표 뱃지 설정'}
        >
          {badge?.badgeUrl ? (
            <Image source={{ uri: badge.badgeUrl }} style={styles.badgeImage} resizeMode="contain" />
          ) : (
            <Ionicons name={badge ? 'ribbon' : 'ribbon-outline'} size={16} color={theme.colors.primary} />
          )}
        </TouchableOpacity>
      </View>

      <View style={styles.nameRow}>
        <CustomText variant="Heading/H2" color={theme.colors.white} style={styles.nickname}>
          {nickname}
        </CustomText>
        <TouchableOpacity style={styles.appellationChip} onPress={onAppellationPress} activeOpacity={0.8}>
          <CustomText variant="Caption" color={theme.colors.white} style={styles.appellationText}>
            {appellation?.appellationName ?? '칭호 고르기'}
          </CustomText>
        </TouchableOpacity>
      </View>
      <CustomText variant="Body/Small" color={styles.subtle.color} style={styles.level}>
        {level}
      </CustomText>

      <View style={styles.bottomRow}>
        <View style={styles.tierChip}>
          <CustomText variant="Label/Medium" color={theme.colors.accentGold} style={styles.tierText}>
            🏆 {tierLabel}
          </CustomText>
        </View>
        <View style={styles.scoreGroup}>
          <CustomText variant="Caption" color={styles.subtle.color}>
            이번 달 {monthScore}
          </CustomText>
          <CustomText variant="Heading/H4" color={theme.colors.white} style={styles.score}>
            {score}
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
  subtle: {
    color: 'rgba(255,255,255,0.85)',
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
  avatarRow: {
    marginTop: theme.spacing.base,
  },
  avatar: {
    width: 88,
    height: 88,
    borderRadius: 44,
    backgroundColor: theme.colors.white,
    justifyContent: 'center',
    alignItems: 'center',
    overflow: 'hidden',
  },
  avatarImage: {
    width: 88,
    height: 88,
  },
  badgeSlot: {
    position: 'absolute',
    right: -4,
    bottom: -4,
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: theme.colors.white,
    borderWidth: 2,
    borderColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    overflow: 'hidden',
  },
  badgeImage: {
    width: 22,
    height: 22,
  },
  nameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    marginTop: theme.spacing.base,
  },
  nickname: {
    fontWeight: 'bold',
  },
  appellationChip: {
    backgroundColor: 'rgba(255,255,255,0.2)',
    borderRadius: theme.rounded.pill,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 3,
  },
  appellationText: {
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
    backgroundColor: theme.colors.white,
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
