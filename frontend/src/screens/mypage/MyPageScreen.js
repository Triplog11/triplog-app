import React, { useState } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import ProfileCard from './components/ProfileCard';
import RecentCardsSection from './components/RecentCardsSection';
import TravelLogSection from './components/TravelLogSection';
import ReviewDetailModal from './components/ReviewDetailModal';
import MenuRow, { MenuGroup } from './components/MenuRow';
import useMyPageData from './hooks/useMyPageData';

const APP_VERSION = `v${require('../../../app.json').expo.version}`;

/** 마이페이지 — GET /users/mypage 요약 + 최근 카드/기록. 포커스마다 갱신. */
export default function MyPageScreen({ navigation }) {
  const { user, logout } = useAuth();
  const { summary, summaryError, stats, cards, reviews, reload } = useMyPageData();
  const [selectedReview, setSelectedReview] = useState(null);

  // XP 바 — 현재 xp는 요약, 다음 레벨 요구치는 /stats/me(requiredXp)에서 가져온다
  const xp = summary?.xp ?? stats?.xp ?? null;
  const xpMax = stats?.requiredXp ?? null;
  const xpRatio = xp != null && xpMax ? Math.min(xp / xpMax, 1) : 0;

  const notifyComingSoon = () => {
    Alert.alert('준비 중', '곧 만나실 수 있어요.');
  };

  const handleLogout = () => {
    Alert.alert('로그아웃', '정말 로그아웃 하시겠습니까?', [
      { text: '취소', style: 'cancel' },
      { text: '로그아웃', onPress: () => logout() },
    ]);
  };

  const countOrDash = (value, unit = '개') => (value != null ? `${value.toLocaleString()}${unit}` : '--');
  const statItems = [
    { key: 'regions', label: '방문 지역', value: countOrDash(summary?.visitedRegionCount) },
    { key: 'certs', label: '인증 횟수', value: countOrDash(summary?.totalCertificationCount, '회') },
    { key: 'badges', label: '획득한 뱃지', value: countOrDash(summary?.acquiredBadgeCount) },
    { key: 'cards', label: '수집한 카드', value: countOrDash(summary?.collectedCardCount, '장') },
  ];

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
        <ProfileCard
          summary={summary}
          fallbackNickname={user?.nickname}
          onEditPress={() => navigation.navigate('ProfileEdit')}
          onAppellationPress={() => navigation.navigate('Appellation')}
          onBadgePress={() => navigation.navigate('BadgeList')}
        />

        {summaryError ? (
          <TouchableOpacity style={styles.errorBanner} onPress={reload} activeOpacity={0.8}>
            <CustomText variant="Body/Small" color={theme.colors.white} style={styles.errorText}>
              {summaryError}
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.white} style={styles.retryText}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        ) : null}

        {/* 경험치 */}
        <View style={styles.xpSection}>
          <View style={styles.xpHeader}>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
              경험치
            </CustomText>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
              <CustomText variant="Body/Small" color={theme.colors.primary} style={styles.bold}>
                {xp ?? '--'}
              </CustomText>
              {' '}/ {xpMax ?? '--'} XP
            </CustomText>
          </View>
          <View style={styles.xpTrack}>
            <View style={[styles.xpFill, { width: `${xpRatio * 100}%` }]} />
          </View>
        </View>

        {/* 통계 2×2 */}
        <View style={styles.statGrid}>
          {statItems.map((stat) => (
            <View key={stat.key} style={styles.statCard}>
              <View style={styles.statRow}>
                <Ionicons name="checkmark-circle" size={22} color={theme.colors.primary} />
                <View style={styles.statTextGroup}>
                  <CustomText variant="Caption" color={theme.colors.textSecondary}>
                    {stat.label}
                  </CustomText>
                  <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.bold}>
                    {stat.value}
                  </CustomText>
                </View>
              </View>
            </View>
          ))}
        </View>

        <RecentCardsSection
          cards={cards.items}
          loading={cards.loading}
          errorMessage={cards.errorMessage}
          onMorePress={() => navigation.navigate('Collection')}
        />

        <TravelLogSection
          reviews={reviews.items}
          totalCount={reviews.total}
          loading={reviews.loading}
          errorMessage={reviews.errorMessage}
          onMorePress={() => navigation.navigate('TravelLog')}
          onEntryPress={setSelectedReview}
        />

        {/* 내 활동 */}
        <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.groupLabel}>
          내 활동
        </CustomText>
        <MenuGroup>
          <MenuRow icon="time-outline" label="인증 내역" onPress={() => navigation.navigate('VerifyHistory')} />
          <MenuRow icon="list-outline" label="활동 내역" onPress={() => navigation.navigate('ActivityHistory')} />
          <MenuRow icon="ribbon-outline" label="뱃지 보관함" onPress={() => navigation.navigate('BadgeList')} />
          <MenuRow icon="pricetag-outline" label="칭호" onPress={() => navigation.navigate('Appellation')} />
          <MenuRow icon="megaphone-outline" label="이벤트" onPress={() => navigation.navigate('EventList')} />
          <MenuRow icon="heart-outline" label="찜한 랜드마크" onPress={() => navigation.navigate('Wishlist')} />
          <MenuRow icon="notifications-outline" label="알림" onPress={() => navigation.navigate('Notification')} last />
        </MenuGroup>

        {/* 설정 (서팍 피그마: 설정 버튼 삭제 → 하단 섹션으로 이동) */}
        <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.groupLabel}>
          설정
        </CustomText>
        <MenuGroup>
          <MenuRow icon="notifications-outline" label="알림 설정" onPress={() => navigation.navigate('NotificationSettings')} />
          <MenuRow icon="help-circle-outline" label="도움말 / 문의" onPress={notifyComingSoon} />
          <MenuRow icon="shield-checkmark-outline" label="이용약관 및 개인정보 처리방침" onPress={notifyComingSoon} />
          {/* 버전 정보 — 우측 표시, 클릭 인터랙션 없음 (피그마 디스크립션 #4) */}
          <MenuRow icon="information-circle-outline" label="버전 정보" value={APP_VERSION} />
          <MenuRow icon="log-out-outline" label="로그아웃" labelColor={theme.colors.primary} onPress={handleLogout} last />
        </MenuGroup>
      </ScrollView>

      {selectedReview && (
        <ReviewDetailModal review={selectedReview} onClose={() => setSelectedReview(null)} />
      )}
    </SafeAreaView>
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
    paddingBottom: theme.spacing.section,
    gap: theme.spacing.base,
  },
  bold: {
    fontWeight: 'bold',
  },
  errorBanner: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.text,
    borderRadius: theme.rounded.md,
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.base,
  },
  errorText: {
    flex: 1,
  },
  retryText: {
    fontWeight: 'bold',
    textDecorationLine: 'underline',
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
  groupLabel: {
    fontWeight: 'bold',
    marginTop: theme.spacing.sm,
  },
});
