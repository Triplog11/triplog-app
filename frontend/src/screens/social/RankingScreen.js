import React, { useState } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { RANKINGS, MY_RANK, SEASON, TIER_CONFIG } from '../../data/ranking';

const TABS = [
  { key: 'season', label: '시즌 랭킹' },
  { key: 'total', label: '전체 랭킹' },
];

/** 랭킹 — 시즌/전체 탭, TOP3 시상대, 순위 리스트, 하단 고정 내 순위 */
export default function RankingScreen({ navigation }) {
  const { user } = useAuth();
  const [tab, setTab] = useState('season');

  const top3 = RANKINGS.slice(0, 3);
  const rest = RANKINGS.slice(3);
  const myTier = user?.tier ? capitalize(user.tier) : 'Bronze';

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      {/* 탭 스위처 */}
      <View style={styles.tabSwitcher}>
        {TABS.map((t) => {
          const active = tab === t.key;
          return (
            <TouchableOpacity
              key={t.key}
              style={[styles.tabButton, active && styles.tabButtonActive]}
              onPress={() => setTab(t.key)}
              activeOpacity={0.8}
            >
              <CustomText
                variant="Label/Medium"
                color={active ? theme.colors.primary : theme.colors.textSecondary}
                style={styles.bold}
              >
                {t.label}
              </CustomText>
            </TouchableOpacity>
          );
        })}
      </View>

      <ScrollView contentContainerStyle={styles.scroll} showsVerticalScrollIndicator={false}>
        {tab === 'season' && (
          <View style={styles.seasonBanner}>
            <View>
              <CustomText variant="Caption" color={theme.colors.textSecondary}>현재 시즌</CustomText>
              <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
                {SEASON.name}
              </CustomText>
            </View>
            <View style={styles.seasonRight}>
              <CustomText variant="Caption" color={theme.colors.textSecondary}>종료까지</CustomText>
              <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
                {SEASON.remainingDays}일 남음
              </CustomText>
            </View>
          </View>
        )}

        {/* TOP 3 시상대 — 2위, 1위, 3위 순으로 배치 */}
        <View style={styles.podium}>
          {[top3[1], top3[0], top3[2]].map((player) => {
            if (!player) return null;
            const isFirst = player.rank === 1;
            const tier = TIER_CONFIG[player.tier] ?? TIER_CONFIG.Bronze;
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
                    {player.nickname.slice(0, 1)}
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
                  {player.score.toLocaleString()}점
                </CustomText>
                <View
                  style={[
                    styles.podiumBlock,
                    { height: isFirst ? 78 : player.rank === 2 ? 58 : 44, backgroundColor: tier.color },
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

        {/* 4위 이하 */}
        <View style={styles.list}>
          {rest.map((player) => {
            const tier = TIER_CONFIG[player.tier] ?? TIER_CONFIG.Bronze;
            return (
              <View key={player.rank} style={styles.row}>
                <CustomText variant="Label/Medium" color={theme.colors.textSecondary} style={styles.rankNum}>
                  {player.rank}
                </CustomText>
                <View style={[styles.avatar, { backgroundColor: tier.soft }]}>
                  <CustomText variant="Label/Medium" color={tier.color} style={styles.bold}>
                    {player.nickname.slice(0, 1)}
                  </CustomText>
                </View>
                <View style={styles.rowInfo}>
                  <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
                    {player.nickname}
                  </CustomText>
                  <CustomText variant="Caption" color={theme.colors.textSecondary}>
                    Lv.{player.level} {player.levelName}
                  </CustomText>
                </View>
                <View style={styles.rowRight}>
                  <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
                    {player.score.toLocaleString()}
                  </CustomText>
                  <View style={[styles.tierPill, { backgroundColor: tier.soft }]}>
                    <CustomText variant="Caption" color={tier.color} style={styles.tierText}>
                      {tier.label}
                    </CustomText>
                  </View>
                </View>
              </View>
            );
          })}
        </View>

        <TouchableOpacity
          style={styles.communityBtn}
          onPress={() => navigation.navigate('Community')}
          activeOpacity={0.85}
        >
          <Ionicons name="chatbubbles-outline" size={18} color={theme.colors.primary} />
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
            여행 피드 보러 가기
          </CustomText>
        </TouchableOpacity>
      </ScrollView>

      {/* 내 순위 — 하단 고정 */}
      <View style={styles.myRankBar}>
        <CustomText variant="Label/Medium" color="#FFFFFF" style={styles.rankNum}>
          {MY_RANK.rank}
        </CustomText>
        <View style={styles.myAvatar}>
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
            {(user?.nickname ?? '나').slice(0, 1)}
          </CustomText>
        </View>
        <View style={styles.rowInfo}>
          <CustomText variant="Label/Medium" color="#FFFFFF" style={styles.bold} numberOfLines={1}>
            {user?.nickname ?? '나'} (나)
          </CustomText>
          <CustomText variant="Caption" color="rgba(255,255,255,0.85)">
            Lv.{user?.level ?? 1} {MY_RANK.levelName} · {TIER_CONFIG[myTier]?.label ?? myTier}
          </CustomText>
        </View>
        <CustomText variant="Label/Medium" color="#FFFFFF" style={styles.bold}>
          {MY_RANK.score.toLocaleString()}
        </CustomText>
      </View>
    </SafeAreaView>
  );
}

function capitalize(value) {
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  bold: {
    fontWeight: 'bold',
  },
  tabSwitcher: {
    flexDirection: 'row',
    marginHorizontal: theme.spacing.lg,
    marginTop: theme.spacing.base,
    backgroundColor: theme.colors.surfaceDim,
    borderRadius: theme.rounded.card,
    padding: 4,
    gap: 4,
  },
  tabButton: {
    flex: 1,
    alignItems: 'center',
    paddingVertical: 9,
    borderRadius: theme.rounded.md,
  },
  tabButtonActive: {
    backgroundColor: theme.colors.canvas,
  },
  scroll: {
    padding: theme.spacing.lg,
    paddingBottom: 24,
  },
  seasonBanner: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: theme.colors.primarySoft,
    borderRadius: theme.rounded.card,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 12,
    marginBottom: theme.spacing.base,
  },
  seasonRight: {
    alignItems: 'flex-end',
  },
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
  crown: {
    marginBottom: 2,
  },
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
  list: {
    gap: theme.spacing.sm,
  },
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
  communityBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 6,
    height: 48,
    marginTop: theme.spacing.base,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
  },
  myRankBar: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.primary,
    marginHorizontal: theme.spacing.lg,
    marginBottom: theme.spacing.base,
    borderRadius: theme.rounded.card,
    padding: theme.spacing.sm,
  },
  myAvatar: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: '#FFFFFF',
    justifyContent: 'center',
    alignItems: 'center',
  },
});
