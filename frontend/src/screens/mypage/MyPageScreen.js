import React, { useState, useEffect } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, SafeAreaView, Alert } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import { fetchBadges } from '../../api/badges';
import { fetchMyStats } from '../../api/stats';
import { fetchMyRanking } from '../../api/stats';
import { fetchNationwideMap } from '../../api/regions';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import ProfileCard from './components/ProfileCard';
import TravelLogSection from './components/TravelLogSection';
import { MOCK_RECENT_CARDS, MOCK_TRAVEL_LOG } from './mockMyPage';

const APP_VERSION = `v${require('../../../app.json').expo.version}`;

export default function MyPageScreen({ navigation }) {
  const { user, logout } = useAuth();
  const [badgeCount, setBadgeCount] = useState(null);
  const [myStats, setMyStats] = useState(null);
  const [myRank, setMyRank] = useState(null);
  const [mapSummary, setMapSummary] = useState(null);

  // 실데이터: 뱃지 수 / 내 스탯(레벨·XP·티어) / 내 랭킹 / 전국 지도 요약(방문·완성 지역 수)
  // 각 요청은 독립적으로 실패해도 화면 전체를 막지 않고 해당 값만 '--'로 남긴다.
  useEffect(() => {
    let mounted = true;
    fetchBadges({ isAcquired: true, size: 1 })
      .then((result) => mounted && setBadgeCount(result?.totalElements ?? null))
      .catch((error) => console.warn('뱃지 수 조회 실패:', error?.status, error?.message));
    fetchMyStats()
      .then((result) => mounted && setMyStats(result ?? null))
      .catch((error) => console.warn('내 스탯 조회 실패:', error?.status, error?.message));
    fetchMyRanking()
      .then((result) => mounted && setMyRank(result ?? null))
      .catch((error) => console.warn('내 랭킹 조회 실패:', error?.status, error?.message));
    fetchNationwideMap()
      .then((result) => mounted && setMapSummary(result ?? null))
      .catch((error) => console.warn('전국 지도 요약 조회 실패:', error?.status, error?.message));
    return () => {
      mounted = false;
    };
  }, []);

  const xp = myStats?.xp ?? user?.xp ?? 0;
  const xpMax = myStats?.requiredXp ?? null;
  const xpRatio = xpMax ? Math.min(xp / xpMax, 1) : 0;

  // ProfileCard가 기대하는 rank shape로 변환 (미도착 시 안전 기본값)
  const rankForCard = myRank
    ? {
        tierLabel: myRank.tier ? `${myRank.tier} Rank` : '—',
        monthlyRank: myRank.monthlyRank ?? '—',
        totalScore: myRank.overallScore ?? 0,
      }
    : { tierLabel: '—', monthlyRank: '—', totalScore: 0 };

  const notifyComingSoon = () => {
    Alert.alert('준비 중', '곧 만나실 수 있어요.');
  };

  const handleLogout = () => {
    Alert.alert('로그아웃', '정말 로그아웃 하시겠습니까?', [
      { text: '취소', style: 'cancel' },
      { text: '로그아웃', onPress: () => logout() },
    ]);
  };

  // 스탯 그리드 — 전부 실데이터. 카운트 엔드포인트가 없는 값은 '--'로 남긴다.
  const stats = [
    {
      key: 'regions',
      label: '방문 지역',
      value: mapSummary?.visitedRegionCount != null ? `${mapSummary.visitedRegionCount}개` : '--',
    },
    {
      key: 'completed',
      label: '완성 지역',
      value: mapSummary?.completedRegionCount != null ? `${mapSummary.completedRegionCount}개` : '--',
    },
    { key: 'badges', label: '획득한 뱃지', value: badgeCount != null ? `${badgeCount}개` : '--' },
    {
      key: 'score',
      label: '누적 점수',
      value: myStats?.overallScore != null ? `${myStats.overallScore.toLocaleString()}점` : '--',
    },
  ];

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
        <ProfileCard
          user={user}
          rank={rankForCard}
          onEditPress={() => navigation.navigate('ProfileEdit')}
        />

        {/* 경험치 */}
        <View style={styles.xpSection}>
          <View style={styles.xpHeader}>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.xpTitle}>
              경험치
            </CustomText>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
              <CustomText variant="Body/Small" color={theme.colors.primary} style={styles.xpValue}>
                {xp}
              </CustomText>
              {' '}/ {xpMax ?? '—'} XP
            </CustomText>
          </View>
          <View style={styles.xpTrack}>
            <View style={[styles.xpFill, { width: `${xpRatio * 100}%` }]} />
          </View>
        </View>

        {/* 통계 2×2 */}
        <View style={styles.statGrid}>
          {stats.map((stat) => (
            <View key={stat.key} style={styles.statCard}>
              <View style={styles.statRow}>
                <Ionicons name="checkmark-circle" size={22} color={theme.colors.primary} />
                <View style={styles.statTextGroup}>
                  <CustomText variant="Caption" color={theme.colors.textSecondary}>
                    {stat.label}
                  </CustomText>
                  <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.statValue}>
                    {stat.value}
                  </CustomText>
                </View>
              </View>
            </View>
          ))}
        </View>

        {/* 최근 획득 카드 (목데이터 — 카드 API 대기) */}
        <View style={styles.sectionCard}>
          <View style={styles.sectionHeader}>
            <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.sectionTitle}>
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
                  <Ionicons name="image-outline" size={28} color={theme.colors.textMuted} />
                </View>
                <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.landmarkName}>
                  {card.name}
                </CustomText>
                <CustomText variant="Caption" color={theme.colors.textSecondary}>
                  {card.region}
                </CustomText>
              </View>
            ))}
          </View>
        </View>

        <TravelLogSection log={MOCK_TRAVEL_LOG} onMorePress={() => navigation.navigate('TravelLog')} />

        {/* 내 활동 */}
        <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.groupLabel}>
          내 활동
        </CustomText>
        <View style={styles.menuContainer}>
          <MenuRow icon="time-outline" label="인증 내역" onPress={() => navigation.navigate('VerifyHistory')} />
          <MenuRow icon="ribbon-outline" label="뱃지 보관함" onPress={() => navigation.navigate('BadgeList')} />
          <MenuRow icon="heart-outline" label="찜한 랜드마크" onPress={() => navigation.navigate('Wishlist')} />
          <MenuRow
            icon="notifications-outline"
            label="알림"
            onPress={() => navigation.navigate('Notification')}
            last
          />
        </View>

        {/* 설정 (서팍 피그마: 설정 버튼 삭제 → 하단 섹션으로 이동) */}
        <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.groupLabel}>
          설정
        </CustomText>
        <View style={styles.menuContainer}>
          <MenuRow icon="notifications-outline" label="알림 설정" onPress={notifyComingSoon} />
          <MenuRow icon="help-circle-outline" label="도움말 / 문의" onPress={notifyComingSoon} />
          <MenuRow icon="shield-checkmark-outline" label="이용약관 및 개인정보 처리방침" onPress={notifyComingSoon} />
          {/* 버전 정보 — 우측 표시, 클릭 인터랙션 없음 (피그마 디스크립션 #4) */}
          <View style={styles.menuItem}>
            <View style={styles.menuLeft}>
              <View style={styles.menuIcon}>
                <Ionicons name="information-circle-outline" size={18} color={theme.colors.primary} />
              </View>
              <CustomText variant="Body/Medium" color={theme.colors.text} style={styles.menuTitle}>
                버전 정보
              </CustomText>
            </View>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
              {APP_VERSION}
            </CustomText>
          </View>
          <TouchableOpacity
            style={[styles.menuItem, styles.menuItemLast]}
            onPress={handleLogout}
            activeOpacity={0.7}
          >
            <View style={styles.menuLeft}>
              <View style={styles.menuIcon}>
                <Ionicons name="log-out-outline" size={18} color={theme.colors.primary} />
              </View>
              <CustomText variant="Body/Medium" color={theme.colors.primary} style={styles.menuTitle}>
                로그아웃
              </CustomText>
            </View>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

function MenuRow({ icon, label, onPress, last }) {
  return (
    <TouchableOpacity
      style={[styles.menuItem, last && styles.menuItemLast]}
      onPress={onPress}
      activeOpacity={0.7}
    >
      <View style={styles.menuLeft}>
        <View style={styles.menuIcon}>
          <Ionicons name={icon} size={18} color={theme.colors.primary} />
        </View>
        <CustomText variant="Body/Medium" color={theme.colors.text} style={styles.menuTitle}>
          {label}
        </CustomText>
      </View>
      <Ionicons name="chevron-forward" size={16} color={theme.colors.textMuted} />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  scrollContent: {
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.lg,
    paddingBottom: 40,
    gap: theme.spacing.base,
  },
  xpSection: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  xpHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  xpTitle: {
    fontWeight: 'bold',
  },
  xpValue: {
    fontWeight: 'bold',
  },
  xpTrack: {
    height: 10,
    borderRadius: 5,
    backgroundColor: theme.colors.surfaceDim,
    overflow: 'hidden',
  },
  xpFill: {
    height: '100%',
    borderRadius: 5,
    backgroundColor: theme.colors.primary,
  },
  statGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: theme.spacing.sm,
  },
  statCard: {
    flexBasis: '48%',
    flexGrow: 1,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  statRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  statTextGroup: {
    gap: 2,
  },
  statValue: {
    fontWeight: 'bold',
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
  sectionTitle: {
    fontWeight: 'bold',
  },
  moreRow: {
    flexDirection: 'row',
    alignItems: 'center',
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
    height: 84,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  landmarkName: {
    fontWeight: 'bold',
    paddingHorizontal: theme.spacing.sm,
  },
  groupLabel: {
    fontWeight: 'bold',
    marginTop: theme.spacing.sm,
  },
  menuContainer: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    overflow: 'hidden',
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 16,
    paddingHorizontal: theme.spacing.base,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.border,
  },
  menuItemLast: {
    borderBottomWidth: 0,
  },
  menuLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  menuIcon: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: theme.colors.primarySoft,
    justifyContent: 'center',
    alignItems: 'center',
  },
  menuTitle: {
    fontWeight: '600',
  },
});
