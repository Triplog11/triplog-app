import React, { useState } from 'react';
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, SafeAreaView, Dimensions } from 'react-native';
import theme from '../../theme/theme';
import { Feather } from '@expo/vector-icons';
import KoreaMap from './components/KoreaMap';

const { width } = Dimensions.get('window');

// 더미 지역 데이터 (service-policy.md 기준 예시 반영)
const initialRegionData = [
  { id: '1', name: '유성구', province: '대전광역시', progress: 100, completed: true, visitedCount: 3, totalCount: 3, badge: '대전 입문자' },
  { id: '2', name: '중구', province: '대전광역시', progress: 80, completed: false, visitedCount: 4, totalCount: 5, badge: null },
  { id: '3', name: '서구', province: '대전광역시', progress: 60, completed: false, visitedCount: 3, totalCount: 5, badge: null },
  { id: '4', name: '동구', province: '대전광역시', progress: 30, completed: false, visitedCount: 1, totalCount: 3, badge: null },
  { id: '5', name: '대덕구', province: '대전광역시', progress: 0, completed: false, visitedCount: 0, totalCount: 4, badge: null },
];

export default function CollectionScreen({ navigation }) {
  const [activeSubTab, setActiveSubTab] = useState('map'); // 'map' | 'list'
  const [regions, setRegions] = useState(initialRegionData);

  // 방문 완료 지역 계산
  const completedCount = regions.filter(r => r.completed).length;

  return (
    <SafeAreaView style={styles.container}>
      {/* 상단 타이틀 영역 */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>나의 여행 도감 🎒</Text>
        <Text style={styles.headerSub}>
          전국 시/군/구 중 <Text style={styles.highlightText}>{completedCount}곳</Text>을 완료했어요
        </Text>
      </View>

      {/* 서브 탭 스위처 (지도 vs 목록) */}
      <View style={styles.tabContainer}>
        <TouchableOpacity
          style={[styles.tabButton, activeSubTab === 'map' && styles.activeTabButton]}
          onPress={() => setActiveSubTab('map')}
        >
          <Feather name="map" size={16} color={activeSubTab === 'map' ? theme.colors.primary : theme.colors.textSecondary} />
          <Text style={[styles.tabButtonText, activeSubTab === 'map' && styles.activeTabButtonText]}>지도 보기</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.tabButton, activeSubTab === 'list' && styles.activeTabButton]}
          onPress={() => setActiveSubTab('list')}
        >
          <Feather name="list" size={16} color={activeSubTab === 'list' ? theme.colors.primary : theme.colors.textSecondary} />
          <Text style={[styles.tabButtonText, activeSubTab === 'list' && styles.activeTabButtonText]}>도감 목록</Text>
        </TouchableOpacity>
      </View>

      {/* 콘텐츠 영역 */}
      {activeSubTab === 'map' ? (
        <View style={styles.mapWrapper}>
          <ScrollView contentContainerStyle={styles.mapScroll} maximumZoomScale={2.5} minimumZoomScale={1} showsVerticalScrollIndicator={false}>
            {/* SVG 한국 지도 로드 */}
            <KoreaMap 
              regions={regions} 
              onRegionPress={(regionName) => {
                const found = regions.find(r => r.name === regionName);
                navigation.navigate('Detail', { 
                  regionName: regionName, 
                  progress: found ? found.progress : 0 
                });
              }} 
            />
          </ScrollView>
          <View style={styles.mapGuideCard}>
            <View style={styles.guideItem}>
              <View style={[styles.guideColor, { backgroundColor: theme.colors.primary }]} />
              <Text style={styles.guideText}>완료(100%)</Text>
            </View>
            <View style={styles.guideItem}>
              <View style={[styles.guideColor, { backgroundColor: theme.colors.blueTint, borderColor: theme.colors.primary, borderWidth: 1 }]} />
              <Text style={styles.guideText}>진행 중</Text>
            </View>
            <View style={styles.guideItem}>
              <View style={[styles.guideColor, { backgroundColor: theme.colors.surface }]} />
              <Text style={styles.guideText}>미방문</Text>
            </View>
          </View>
        </View>
      ) : (
        <ScrollView contentContainerStyle={styles.listContainer} showsVerticalScrollIndicator={false}>
          <Text style={styles.sectionTitle}>대전광역시 도감</Text>
          {regions.map((region) => (
            <TouchableOpacity
              key={region.id}
              style={[
                styles.regionCard,
                region.completed ? styles.completedCard : region.progress > 0 ? styles.inProgressCard : styles.unvisitedCard
              ]}
              onPress={() => navigation.navigate('Detail', { regionName: region.name, progress: region.progress })}
            >
              <View style={styles.cardHeader}>
                <View>
                  <Text style={styles.regionNameText}>{region.name}</Text>
                  <Text style={styles.provinceText}>{region.province}</Text>
                </View>
                <View style={styles.badgeRow}>
                  {region.completed && (
                    <View style={styles.completedBadge}>
                      <Text style={styles.completedBadgeText}>정복 완료 🏆</Text>
                    </View>
                  )}
                  <Text style={[styles.progressText, region.completed && { color: theme.colors.success }]}>
                    {region.progress}%
                  </Text>
                </View>
              </View>

              {/* 진행도 게이지 바 */}
              <View style={styles.progressTrack}>
                <View
                  style={[
                    styles.progressBar,
                    {
                      width: `${region.progress}%`,
                      backgroundColor: region.completed ? theme.colors.success : theme.colors.primary
                    }
                  ]}
                />
              </View>

              <View style={styles.cardFooter}>
                <Text style={styles.footerText}>
                  방문 랜드마크: {region.visitedCount} / {region.totalCount}
                </Text>
                {region.badge && (
                  <Text style={styles.badgeText}>획득 뱃지: {region.badge}</Text>
                )}
              </View>
            </TouchableOpacity>
          ))}
        </ScrollView>
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.canvas,
  },
  header: {
    paddingHorizontal: 24,
    paddingTop: 20,
    paddingBottom: 16,
  },
  headerTitle: {
    fontSize: 24,
    fontWeight: '700',
    color: theme.colors.textPrimary,
  },
  headerSub: {
    fontSize: 14,
    color: theme.colors.textSecondary,
    marginTop: 6,
  },
  highlightText: {
    color: theme.colors.primary,
    fontWeight: '700',
  },
  tabContainer: {
    flexDirection: 'row',
    paddingHorizontal: 24,
    marginBottom: 16,
    gap: 12,
  },
  tabButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: theme.rounded.full,
    backgroundColor: theme.colors.surface,
    gap: 6,
  },
  activeTabButton: {
    backgroundColor: theme.colors.blueWash,
    borderColor: theme.colors.primary,
    borderWidth: 1,
  },
  tabButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: theme.colors.textSecondary,
  },
  activeTabButtonText: {
    color: theme.colors.primary,
  },
  mapWrapper: {
    flex: 1,
    position: 'relative',
  },
  mapScroll: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 20,
  },
  mapGuideCard: {
    position: 'absolute',
    bottom: 24,
    right: 24,
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderRadius: theme.rounded.sm,
    padding: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 3,
    gap: 6,
  },
  guideItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  guideColor: {
    width: 14,
    height: 14,
    borderRadius: 3,
  },
  guideText: {
    fontSize: 11,
    color: theme.colors.textBody,
    fontWeight: '600',
  },
  listContainer: {
    paddingHorizontal: 24,
    paddingBottom: 40,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: theme.colors.textPrimary,
    marginBottom: 12,
    marginTop: 8,
  },
  regionCard: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.lg,
    padding: 16,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  completedCard: {
    backgroundColor: theme.colors.canvas,
    borderColor: theme.colors.success,
  },
  inProgressCard: {
    backgroundColor: theme.colors.blueTint,
    borderColor: theme.colors.primary,
  },
  unvisitedCard: {
    backgroundColor: theme.colors.canvas,
    borderColor: theme.colors.border,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 12,
  },
  regionNameText: {
    fontSize: 18,
    fontWeight: '700',
    color: theme.colors.textPrimary,
  },
  provinceText: {
    fontSize: 12,
    color: theme.colors.textSecondary,
    marginTop: 2,
  },
  badgeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  completedBadge: {
    backgroundColor: 'rgba(34, 197, 94, 0.15)',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 6,
  },
  completedBadgeText: {
    fontSize: 11,
    color: theme.colors.success,
    fontWeight: '700',
  },
  progressText: {
    fontSize: 16,
    fontWeight: '700',
    color: theme.colors.primary,
  },
  progressTrack: {
    height: 6,
    backgroundColor: '#E2E8F0',
    borderRadius: 3,
    overflow: 'hidden',
    marginBottom: 12,
  },
  progressBar: {
    height: '100%',
    borderRadius: 3,
  },
  cardFooter: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  footerText: {
    fontSize: 12,
    color: theme.colors.textSecondary,
  },
  badgeText: {
    fontSize: 12,
    fontWeight: '600',
    color: theme.colors.primary,
  },
});
