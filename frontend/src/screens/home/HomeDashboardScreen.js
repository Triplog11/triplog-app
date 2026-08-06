import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, Image } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchMyStats, fetchMyRanking } from '../../api/stats';
import { fetchMyMissions } from '../../api/missions';
import { fetchNationwideMap } from '../../api/regions';
import { MOCK_RECENT_CARDS } from '../mypage/mockMyPage';

/**
 * 홈 = 대시보드 (서팍 피그마 홈 레이아웃, 색상은 DESIGN.md 블루 토큰).
 * 프로필 요약 + 오늘의 미션 + 전국 지도 진입 + 최근 획득 카드.
 * 전국 지도는 '전국 지도 보기'로 NationwideMap 화면에서 연다(지도 hero 원칙 유지).
 */
export default function HomeDashboardScreen({ navigation }) {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [rank, setRank] = useState(null);
  const [missions, setMissions] = useState([]);
  const [mapSummary, setMapSummary] = useState(null);

  const load = useCallback(async () => {
    // 각 요청 독립 — 하나 실패해도 나머지는 표시. 데이터 없음(404)은 조용히 넘긴다.
    fetchMyStats().then(setStats).catch(() => {});
    fetchMyRanking().then(setRank).catch(() => {});
    fetchMyMissions().then((r) => setMissions(r?.missions ?? [])).catch(() => setMissions([]));
    fetchNationwideMap().then(setMapSummary).catch(() => {});
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const nickname = user?.nickname ?? rank?.nickname ?? '여행자';
  const level = stats?.level ?? user?.level ?? 1;
  const xp = stats?.xp ?? 0;
  const requiredXp = stats?.requiredXp ?? null;
  const remainingXp = stats?.remainingXp ?? null;
  const xpRatio = requiredXp ? Math.min(xp / requiredXp, 1) : 0;
  const tier = rank?.tier ?? stats?.currentTier ?? null;
  const score = rank?.overallScore ?? stats?.overallScore ?? 0;
  const monthlyRank = rank?.monthlyRank ?? null;

  const missionTotal = missions.length;
  const missionDone = missions.filter((m) => m.completed).length;

  const mapPercent = normalizePercent(mapSummary?.overallCompletionRate);
  const mapVisited = mapSummary?.visitedRegionCount;

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView contentContainerStyle={styles.scroll} showsVerticalScrollIndicator={false}>
        {/* 헤더 */}
        <View style={styles.headerRow}>
          <View>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
              안녕하세요 {nickname}님
            </CustomText>
            <CustomText variant="Heading/H3" color={theme.colors.text} style={styles.bold}>
              오늘은 어디를 가볼까요?
            </CustomText>
          </View>
          <TouchableOpacity
            style={styles.bell}
            onPress={() => navigation.navigate('MyPage', { screen: 'Notification' })}
            activeOpacity={0.8}
            hitSlop={8}
          >
            <Ionicons name="notifications-outline" size={22} color={theme.colors.text} />
          </TouchableOpacity>
        </View>

        {/* 프로필 요약 카드 */}
        <View style={styles.profileCard}>
          <View style={styles.profileTop}>
            <View style={styles.avatar}>
              <Ionicons name="person" size={26} color="#FFFFFF" />
            </View>
            <View style={styles.profileInfo}>
              <View style={styles.levelRow}>
                <CustomText variant="Heading/H4" color="#FFFFFF" style={styles.bold}>
                  Lv.{level}
                </CustomText>
                {tier && (
                  <View style={styles.tierChip}>
                    <CustomText variant="Caption" color={theme.colors.accentGold} style={styles.bold}>
                      🏆 {tier}
                    </CustomText>
                  </View>
                )}
              </View>
              <CustomText variant="Caption" color="rgba(255,255,255,0.85)">
                {monthlyRank != null ? `월간 ${monthlyRank}위 · ` : ''}
                {score.toLocaleString()}점
              </CustomText>
            </View>
          </View>

          <View style={styles.xpHeader}>
            <CustomText variant="Caption" color="rgba(255,255,255,0.85)">경험치</CustomText>
            <CustomText variant="Caption" color="#FFFFFF" style={styles.bold}>
              {xp} / {requiredXp ?? '—'} XP
            </CustomText>
          </View>
          <View style={styles.xpTrack}>
            <View style={[styles.xpFill, { width: `${xpRatio * 100}%` }]} />
          </View>
          {remainingXp != null && (
            <CustomText variant="Caption" color="rgba(255,255,255,0.85)" style={styles.xpHint}>
              다음 레벨까지 {remainingXp} XP
            </CustomText>
          )}
        </View>

        {/* 오늘의 미션 */}
        {missionTotal > 0 && (
          <View style={styles.sectionCard}>
            <View style={styles.sectionHeader}>
              <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
                오늘의 미션
              </CustomText>
              <CustomText variant="Caption" color={theme.colors.textSecondary}>
                {missionDone}/{missionTotal} 완료
              </CustomText>
            </View>
            <View style={styles.missionList}>
              {missions.map((m) => (
                <View key={m.missionId} style={styles.missionRow}>
                  <Ionicons
                    name={m.completed ? 'checkmark-circle' : 'ellipse-outline'}
                    size={20}
                    color={m.completed ? theme.colors.primary : theme.colors.textMuted}
                  />
                  <CustomText
                    variant="Body/Medium"
                    color={m.completed ? theme.colors.textMuted : theme.colors.text}
                    style={[styles.missionName, m.completed && styles.missionDone]}
                    numberOfLines={1}
                  >
                    {m.missionName}
                  </CustomText>
                  {m.rewardXp != null && (
                    <View style={styles.rewardPill}>
                      <CustomText variant="Caption" color={theme.colors.primary} style={styles.bold}>
                        +{m.rewardXp} XP
                      </CustomText>
                    </View>
                  )}
                </View>
              ))}
            </View>
          </View>
        )}

        {/* 전국 지도 진입 */}
        <TouchableOpacity
          style={styles.mapEntry}
          onPress={() => navigation.navigate('NationwideMap')}
          activeOpacity={0.85}
        >
          <View style={styles.mapEntryIcon}>
            <Ionicons name="map" size={22} color={theme.colors.primary} />
          </View>
          <View style={styles.mapEntryBody}>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
              전국 지도 보기
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.textSecondary}>
              {mapPercent != null || mapVisited != null
                ? `전국 ${mapPercent ?? 0}% · 방문 ${mapVisited ?? 0}곳`
                : '내 발자취를 지도에서 확인해요'}
            </CustomText>
          </View>
          <Ionicons name="chevron-forward" size={18} color={theme.colors.textMuted} />
        </TouchableOpacity>

        {/* 최근 획득 카드 (카드 목록 API 대기 — 목데이터) */}
        <View style={styles.sectionCard}>
          <View style={styles.sectionHeader}>
            <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
              최근 획득 카드
            </CustomText>
            <TouchableOpacity
              style={styles.moreRow}
              onPress={() => navigation.navigate('Collection')}
              activeOpacity={0.7}
            >
              <CustomText variant="Body/Small" color={theme.colors.textSecondary}>더보기</CustomText>
              <Ionicons name="chevron-forward" size={14} color={theme.colors.textSecondary} />
            </TouchableOpacity>
          </View>
          <View style={styles.cardRow}>
            {MOCK_RECENT_CARDS.map((card) => (
              <View key={card.id} style={styles.landmarkCard}>
                <View style={styles.landmarkThumb}>
                  <Ionicons name="image-outline" size={26} color={theme.colors.textMuted} />
                </View>
                <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.landmarkName} numberOfLines={1}>
                  {card.name}
                </CustomText>
                <CustomText variant="Caption" color={theme.colors.textSecondary} numberOfLines={1}>
                  {card.region}
                </CustomText>
              </View>
            ))}
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

/** overallCompletionRate가 0~1 비율인지 0~100 퍼센트인지 불확실 → 방어적 정규화 */
function normalizePercent(rate) {
  if (rate == null) return null;
  return Math.round(rate <= 1 ? rate * 100 : rate);
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  bold: {
    fontWeight: 'bold',
  },
  scroll: {
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.base,
    paddingBottom: 40,
    gap: theme.spacing.base,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
  },
  bell: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: theme.colors.canvas,
    borderWidth: 1,
    borderColor: theme.colors.border,
    justifyContent: 'center',
    alignItems: 'center',
  },
  profileCard: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.xl,
    padding: theme.spacing.lg,
  },
  profileTop: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.base,
    marginBottom: theme.spacing.base,
  },
  avatar: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: 'rgba(255,255,255,0.25)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  profileInfo: {
    flex: 1,
    gap: 4,
  },
  levelRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  tierChip: {
    backgroundColor: '#FFFFFF',
    borderRadius: theme.rounded.pill ?? 9999,
    paddingHorizontal: 10,
    paddingVertical: 3,
  },
  xpHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 6,
  },
  xpTrack: {
    height: 8,
    borderRadius: 4,
    backgroundColor: 'rgba(255,255,255,0.3)',
    overflow: 'hidden',
  },
  xpFill: {
    height: '100%',
    borderRadius: 4,
    backgroundColor: '#FFFFFF',
  },
  xpHint: {
    marginTop: 6,
  },
  sectionCard: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.lg,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.base,
  },
  moreRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 2,
  },
  missionList: {
    gap: theme.spacing.sm,
  },
  missionRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  missionName: {
    flex: 1,
  },
  missionDone: {
    textDecorationLine: 'line-through',
  },
  rewardPill: {
    backgroundColor: theme.colors.primarySoft,
    borderRadius: theme.rounded.pill ?? 9999,
    paddingHorizontal: 10,
    paddingVertical: 3,
  },
  mapEntry: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.base,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  mapEntryIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: theme.colors.primarySoft,
    justifyContent: 'center',
    alignItems: 'center',
  },
  mapEntryBody: {
    flex: 1,
    gap: 2,
  },
  cardRow: {
    flexDirection: 'row',
    gap: theme.spacing.sm,
  },
  landmarkCard: {
    flex: 1,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.md,
    borderWidth: 1,
    borderColor: theme.colors.border,
    overflow: 'hidden',
    paddingBottom: theme.spacing.sm,
  },
  landmarkThumb: {
    height: 80,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  landmarkName: {
    fontWeight: 'bold',
    paddingHorizontal: theme.spacing.sm,
  },
});
