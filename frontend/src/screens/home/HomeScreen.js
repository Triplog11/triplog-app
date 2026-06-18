import React from 'react';
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, SafeAreaView } from 'react-native';

export default function HomeScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* 상단 웰컴 및 레벨 정보 */}
        <View style={styles.profileSection}>
          <View>
            <Text style={styles.welcomeText}>안녕하세요,</Text>
            <Text style={styles.usernameText}>김준수님 👋</Text>
          </View>
          <View style={styles.badgeContainer}>
            <Text style={styles.levelBadge}>LV.42</Text>
          </View>
        </View>

        {/* 경험치 바 */}
        <View style={styles.xpContainer}>
          <View style={styles.xpHeader}>
            <Text style={styles.xpLabel}>다음 레벨까지</Text>
            <Text style={styles.xpValue}>68% (680/1000 XP)</Text>
          </View>
          <View style={styles.xpTrack}>
            <View style={[styles.xpBar, { width: '68%' }]} />
          </View>
        </View>

        {/* 대시보드 카드 */}
        <View style={styles.statGrid}>
          <TouchableOpacity 
            style={styles.statCard}
            onPress={() => navigation.navigate('History')}
          >
            <Text style={styles.statVal}>128회</Text>
            <Text style={styles.statLabel}>방문 인증 📸</Text>
          </TouchableOpacity>

          <View style={styles.statCard}>
            <Text style={styles.statVal}>15개</Text>
            <Text style={styles.statLabel}>획득 배지 🏆</Text>
          </View>
        </View>

        {/* 추천 장소 */}
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionTitle}>오늘의 추천 여행지 ✈️</Text>
        </View>

        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.recommendScroll}>
          <TouchableOpacity style={styles.recommendCard}>
            <View style={[styles.cardImagePlaceholder, { backgroundColor: '#F59E0B' }]} />
            <Text style={styles.cardTitle}>수원 화성 행궁</Text>
            <Text style={styles.cardSub}>경기 수원시</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.recommendCard}>
            <View style={[styles.cardImagePlaceholder, { backgroundColor: '#10B981' }]} />
            <Text style={styles.cardTitle}>광교 호수공원</Text>
            <Text style={styles.cardSub}>경기 수원시</Text>
          </TouchableOpacity>

          <TouchableOpacity style={styles.recommendCard}>
            <View style={[styles.cardImagePlaceholder, { backgroundColor: '#3B82F6' }]} />
            <Text style={styles.cardTitle}>남한산성 도립공원</Text>
            <Text style={styles.cardSub}>경기 광주시</Text>
          </TouchableOpacity>
        </ScrollView>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0F172A',
  },
  scrollContent: {
    padding: 24,
  },
  profileSection: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 20,
  },
  welcomeText: {
    fontSize: 16,
    color: '#94A3B8',
  },
  usernameText: {
    fontSize: 26,
    fontWeight: 'bold',
    color: '#F8FAFC',
    marginTop: 4,
  },
  badgeContainer: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 20,
    backgroundColor: '#38BDF8',
  },
  levelBadge: {
    color: '#0F172A',
    fontWeight: '900',
    fontSize: 14,
  },
  xpContainer: {
    marginTop: 24,
    backgroundColor: '#1E293B',
    padding: 16,
    borderRadius: 16,
  },
  xpHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  xpLabel: {
    color: '#94A3B8',
    fontSize: 12,
  },
  xpValue: {
    color: '#38BDF8',
    fontSize: 12,
    fontWeight: 'bold',
  },
  xpTrack: {
    height: 8,
    backgroundColor: '#334155',
    borderRadius: 4,
    overflow: 'hidden',
  },
  xpBar: {
    height: '100%',
    backgroundColor: '#38BDF8',
  },
  statGrid: {
    flexDirection: 'row',
    gap: 16,
    marginTop: 20,
  },
  statCard: {
    flex: 1,
    backgroundColor: '#1E293B',
    padding: 20,
    borderRadius: 16,
    alignItems: 'center',
  },
  statVal: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#F8FAFC',
  },
  statLabel: {
    color: '#94A3B8',
    fontSize: 13,
    marginTop: 6,
  },
  sectionHeader: {
    marginTop: 32,
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#F8FAFC',
  },
  recommendScroll: {
    gap: 16,
  },
  recommendCard: {
    width: 160,
    backgroundColor: '#1E293B',
    borderRadius: 16,
    padding: 12,
  },
  cardImagePlaceholder: {
    height: 100,
    borderRadius: 12,
    marginBottom: 10,
  },
  cardTitle: {
    color: '#F8FAFC',
    fontWeight: 'bold',
    fontSize: 14,
  },
  cardSub: {
    color: '#64748B',
    fontSize: 12,
    marginTop: 4,
  },
});
