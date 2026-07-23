import React, { useState, useEffect } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, SafeAreaView, Alert } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import { fetchBadges } from '../../api/badges';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import ProfileCard from './components/ProfileCard';
import TravelLogSection from './components/TravelLogSection';
import { MOCK_RANK, MOCK_STATS, MOCK_XP_MAX, MOCK_RECENT_CARDS, MOCK_TRAVEL_LOG } from './mockMyPage';

const APP_VERSION = `v${require('../../../app.json').expo.version}`;

export default function MyPageScreen({ navigation }) {
  const { user, logout } = useAuth();
  const [badgeCount, setBadgeCount] = useState(null);

  // 획득 뱃지 수만 실데이터 — 나머지 통계는 백엔드 API 대기 중(목데이터)
  useEffect(() => {
    let mounted = true;
    fetchBadges({ isAcquired: true, size: 1 })
      .then((result) => {
        if (mounted) setBadgeCount(result?.totalElements ?? null);
      })
      .catch((error) => console.warn('뱃지 수 조회 실패:', error?.status, error?.message));
    return () => {
      mounted = false;
    };
  }, []);

  const xp = user?.xp ?? 0;
  const xpRatio = Math.min(xp / MOCK_XP_MAX, 1);

  const handleLogout = () => {
    Alert.alert('로그아웃', '정말 로그아웃 하시겠습니까?', [
      { text: '취소', style: 'cancel' },
      { text: '로그아웃', onPress: () => logout() },
    ]);
  };

  const stats = [
    { key: 'regions', label: '방문 지역', value: `${MOCK_STATS.visitedRegions}개` },
    { key: 'landmarks', label: '랜드마크 인증', value: `${MOCK_STATS.certifiedLandmarks}회` },
    { key: 'badges', label: '획득한 뱃지', value: badgeCount != null ? `${badgeCount}개` : '--' },
    { key: 'cards', label: '수집 카드', value: `${MOCK_STATS.collectedCards}개` },
  ];

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
        <ProfileCard
          user={user}
          rank={MOCK_RANK}
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
              {' '}/ {MOCK_XP_MAX} XP
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
            <View style={styles.moreRow}>
              <CustomText variant="Body/Small" color={theme.colors.textSecondary}>더보기</CustomText>
              <Ionicons name="chevron-forward" size={14} color={theme.colors.textSecondary} />
            </View>
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

        <TravelLogSection log={MOCK_TRAVEL_LOG} />

        {/* 내 활동 */}
        <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.groupLabel}>
          내 활동
        </CustomText>
        <View style={styles.menuContainer}>
          <TouchableOpacity
            style={[styles.menuItem, styles.menuItemLast]}
            onPress={() => navigation.navigate('BadgeList')}
            activeOpacity={0.7}
          >
            <View style={styles.menuLeft}>
              <View style={styles.menuIcon}>
                <Ionicons name="time-outline" size={18} color={theme.colors.primary} />
              </View>
              <View>
                <CustomText variant="Body/Medium" color={theme.colors.text} style={styles.menuTitle}>
                  활동 히스토리
                </CustomText>
                <CustomText variant="Caption" color={theme.colors.textSecondary}>
                  랜드마크 인증 · 뱃지
                </CustomText>
              </View>
            </View>
            <Ionicons name="chevron-forward" size={16} color={theme.colors.textMuted} />
          </TouchableOpacity>
        </View>

        {/* 설정 (서팍 피그마: 설정 버튼 삭제 → 하단 섹션으로 이동) */}
        <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.groupLabel}>
          설정
        </CustomText>
        <View style={styles.menuContainer}>
          <MenuRow icon="notifications-outline" label="알림 설정" onPress={() => {}} />
          <MenuRow icon="help-circle-outline" label="도움말 / 문의" onPress={() => {}} />
          <MenuRow icon="shield-checkmark-outline" label="이용약관 및 개인정보 처리방침" onPress={() => {}} />
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

function MenuRow({ icon, label, onPress }) {
  return (
    <TouchableOpacity style={styles.menuItem} onPress={onPress} activeOpacity={0.7}>
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
