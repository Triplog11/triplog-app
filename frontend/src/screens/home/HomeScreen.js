import React from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, SafeAreaView, Dimensions } from 'react-native';
import CustomText from '../../components/common/CustomText';

const { width } = Dimensions.get('window');

export default function HomeScreen({ navigation }) {
  // 모의 데이터 (Data Schema 명세 기반)
  const userStats = {
    nickname: '김준수',
    level: 42,
    xp: 680,
    nextLevelXp: 1000,
    tier: 'Gold',
    appellation: '초보 방랑자 🎒', // 칭호
    overallScore: 3420,
    visitedCount: 128,
    badgeCount: 15,
  };

  const weeklyMissions = [
    { id: 1, name: '경주 역사 유적 지구 방문하기', xp: 150, completed: true },
    { id: 2, name: '인증 리뷰 1개 작성하기', xp: 50, completed: false },
    { id: 3, name: '새로운 지역 지도 오픈하기', xp: 100, completed: false },
  ];

  const earnedBadges = [
    { id: 1, name: '첫 걸음마 👣', color: '#EFF6FF', textColor: '#1D4ED8' },
    { id: 2, name: '역사 탐험가 🏰', color: '#FEF3C7', textColor: '#D97706' },
    { id: 3, name: '야경 사냥꾼 🌃', color: '#F3E8FF', textColor: '#7C3AED' },
    { id: 4, name: '프로 리뷰어 ✍️', color: '#ECFDF5', textColor: '#059669' },
  ];

  const recommendations = [
    { id: 1, title: '수원 화성 행궁', location: '경기 수원시', color: '#FF8A8A' },
    { id: 2, title: '광교 호수공원', location: '경기 수원시', color: '#68B0AB' },
    { id: 3, title: '남한산성 도립공원', location: '경기 광주시', color: '#8FC0A9' },
  ];

  const xpPercentage = `${(userStats.xp / userStats.nextLevelXp) * 100}%`;

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
        
        {/* 1. 상단 프로필 및 칭호 영역 */}
        <View style={styles.profileSection}>
          <View>
            <CustomText variant="Body/Medium" color="#64748B">안녕하세요,</CustomText>
            <View style={styles.nameRow}>
              <CustomText variant="Heading/H2" color="#0F172A" style={styles.usernameText}>
                {userStats.nickname}님
              </CustomText>
              <CustomText variant="Heading/H2" color="#3B82F6"> 👋</CustomText>
            </View>
            <View style={styles.appellationContainer}>
              <CustomText variant="Label/Small" color="#3B82F6" style={styles.appellationText}>
                {userStats.appellation}
              </CustomText>
            </View>
          </View>
          
          <View style={styles.tierBadge}>
            <CustomText variant="UI/Button/Small" color="#FFFFFF" style={styles.tierText}>
              {userStats.tier}
            </CustomText>
          </View>
        </View>

        {/* 2. 경험치 진행률 카드 */}
        <View style={styles.card}>
          <View style={styles.xpHeader}>
            <View style={styles.levelRow}>
              <CustomText variant="Heading/H4" color="#0F172A">LV.{userStats.level}</CustomText>
              <CustomText variant="Caption" color="#94A3B8" style={styles.levelSubText}>레벨 업 도전 중</CustomText>
            </View>
            <CustomText variant="Label/Medium" color="#FF6B6B">
              {userStats.xp} / {userStats.nextLevelXp} XP
            </CustomText>
          </View>
          
          <View style={styles.xpTrack}>
            <View style={[styles.xpBar, { width: xpPercentage }]} />
          </View>
          <View style={styles.xpFooter}>
            <CustomText variant="Caption" color="#94A3B8">다음 단계까지 {100 - (userStats.xp / userStats.nextLevelXp) * 100}% 남았습니다.</CustomText>
          </View>
        </View>

        {/* 3. 활동 스탯 그리드 카드 */}
        <View style={styles.statGrid}>
          <TouchableOpacity 
            style={[styles.statCard, styles.card]}
            onPress={() => navigation.navigate('History')}
            activeOpacity={0.8}
          >
            <CustomText variant="Heading/H1" color="#3B82F6" style={styles.statVal}>
              {userStats.visitedCount}회
            </CustomText>
            <CustomText variant="Label/Medium" color="#64748B" style={styles.statLabel}>
              방문 인증 📸
            </CustomText>
          </TouchableOpacity>

          <View style={[styles.statCard, styles.card]}>
            <CustomText variant="Heading/H1" color="#10B981" style={styles.statVal}>
              {userStats.badgeCount}개
            </CustomText>
            <CustomText variant="Label/Medium" color="#64748B" style={styles.statLabel}>
              획득 배지 🏆
            </CustomText>
          </View>
        </View>

        {/* 4. 주간 미션 영역 (DDL 미션 도메인 반영) */}
        <View style={styles.sectionHeader}>
          <CustomText variant="Heading/H3" color="#0F172A">주간 미션 🎯</CustomText>
          <CustomText variant="Caption" color="#3B82F6">경험치 획득 기회</CustomText>
        </View>
        
        <View style={styles.card}>
          {weeklyMissions.map((mission, idx) => (
            <View 
              key={mission.id} 
              style={[
                styles.missionItem, 
                idx < weeklyMissions.length - 1 && styles.borderBottom
              ]}
            >
              <View style={styles.missionInfo}>
                <View style={[styles.checkbox, mission.completed && styles.checkboxCompleted]}>
                  {mission.completed && <CustomText variant="Label/Small" color="#FFFFFF">✓</CustomText>}
                </View>
                <CustomText 
                  variant="Body/Small" 
                  color={mission.completed ? '#94A3B8' : '#334155'}
                  style={mission.completed && styles.completedText}
                >
                  {mission.name}
                </CustomText>
              </View>
              <View style={[styles.xpTag, mission.completed && styles.xpTagCompleted]}>
                <CustomText 
                  variant="Caption" 
                  color={mission.completed ? '#94A3B8' : '#3B82F6'}
                  style={styles.xpTagText}
                >
                  +{mission.xp} XP
                </CustomText>
              </View>
            </View>
          ))}
        </View>

        {/* 5. 획득 뱃지 갤러리 */}
        <View style={styles.sectionHeader}>
          <CustomText variant="Heading/H3" color="#0F172A">대표 뱃지 🏆</CustomText>
          <TouchableOpacity>
            <CustomText variant="Label/Medium" color="#3B82F6">전체보기</CustomText>
          </TouchableOpacity>
        </View>

        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.badgeScroll}>
          {earnedBadges.map((badge) => (
            <View key={badge.id} style={[styles.badgeItem, { backgroundColor: badge.color }]}>
              <CustomText variant="Body/Medium" color={badge.textColor} style={styles.badgeName}>
                {badge.name}
              </CustomText>
            </View>
          ))}
        </ScrollView>

        {/* 6. 오늘의 추천 여행지 */}
        <View style={styles.sectionHeader}>
          <CustomText variant="Heading/H3" color="#0F172A">오늘의 추천 여행지 ✈️</CustomText>
        </View>

        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.recommendScroll}>
          {recommendations.map((item) => (
            <TouchableOpacity key={item.id} style={[styles.recommendCard, styles.card]} activeOpacity={0.9}>
              <View style={[styles.cardImagePlaceholder, { backgroundColor: item.color }]} />
              <View style={styles.recommendInfo}>
                <CustomText variant="Heading/H5" color="#1E293B" style={styles.cardTitle}>
                  {item.title}
                </CustomText>
                <CustomText variant="Caption" color="#64748B" style={styles.cardSub}>
                  {item.location}
                </CustomText>
              </View>
            </TouchableOpacity>
          ))}
        </ScrollView>

      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8FAFC', // Slate-50: 매우 세련되고 밝은 그레이-화이트
  },
  scrollContent: {
    paddingHorizontal: 20,
    paddingTop: 15,
    paddingBottom: 40,
  },
  profileSection: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginTop: 15,
    marginBottom: 20,
  },
  nameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 2,
  },
  usernameText: {
    fontWeight: 'bold',
  },
  appellationContainer: {
    backgroundColor: '#EFF6FF', // Light blue tint
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 8,
    marginTop: 6,
    alignSelf: 'flex-start',
  },
  appellationText: {
    fontWeight: '600',
  },
  tierBadge: {
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 12,
    backgroundColor: '#EAB308', // Gold Accent Color
    shadowColor: '#EAB308',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 6,
    elevation: 3,
  },
  tierText: {
    fontWeight: 'bold',
    letterSpacing: 0.5,
  },
  card: {
    backgroundColor: '#FFFFFF',
    borderRadius: 16,
    padding: 20,
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.05,
    shadowRadius: 16,
    elevation: 2,
    borderWidth: 1,
    borderColor: '#F1F5F9', // 아주 부드러운 외곽선
  },
  xpHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
    marginBottom: 12,
  },
  levelRow: {
    flexDirection: 'row',
    alignItems: 'baseline',
    gap: 8,
  },
  levelSubText: {
    fontWeight: '500',
  },
  xpTrack: {
    height: 10,
    backgroundColor: '#F1F5F9',
    borderRadius: 6,
    overflow: 'hidden',
  },
  xpBar: {
    height: '100%',
    backgroundColor: '#FF6B6B', // 부드러운 코랄 핑크 게이지바
    borderRadius: 6,
  },
  xpFooter: {
    marginTop: 8,
  },
  statGrid: {
    flexDirection: 'row',
    gap: 16,
    marginTop: 20,
    marginBottom: 25,
  },
  statCard: {
    flex: 1,
    paddingVertical: 18,
    alignItems: 'center',
  },
  statVal: {
    fontWeight: 'bold',
  },
  statLabel: {
    marginTop: 4,
    fontWeight: '500',
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
    marginTop: 10,
    marginBottom: 14,
  },
  missionItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 14,
  },
  borderBottom: {
    borderBottomWidth: 1,
    borderBottomColor: '#F1F5F9',
  },
  missionInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    flex: 1,
  },
  checkbox: {
    width: 20,
    height: 20,
    borderRadius: 6,
    borderWidth: 2,
    borderColor: '#CBD5E1',
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkboxCompleted: {
    borderColor: '#10B981',
    backgroundColor: '#10B981',
  },
  completedText: {
    textDecorationLine: 'line-through',
  },
  xpTag: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
    backgroundColor: '#EFF6FF',
  },
  xpTagCompleted: {
    backgroundColor: '#F1F5F9',
  },
  xpTagText: {
    fontWeight: 'bold',
  },
  badgeScroll: {
    gap: 12,
    paddingBottom: 25,
  },
  badgeItem: {
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#FFFFFF',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.03,
    shadowRadius: 8,
    elevation: 1,
  },
  badgeName: {
    fontWeight: '600',
  },
  recommendScroll: {
    gap: 16,
    paddingBottom: 20,
  },
  recommendCard: {
    width: 200,
    padding: 0, // 이미지 카드형태를 위해 전체 패딩 제거
    overflow: 'hidden',
  },
  cardImagePlaceholder: {
    height: 120,
    width: '100%',
  },
  recommendInfo: {
    padding: 14,
  },
  cardTitle: {
    fontWeight: 'bold',
  },
  cardSub: {
    marginTop: 4,
  },
});
