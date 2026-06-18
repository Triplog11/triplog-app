import React from 'react';
import { StyleSheet, View, FlatList, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';

const DUMMY_BADGES = [
  { id: '1', name: '첫 걸음마', desc: '첫 번째 장소 방문 인증 성공', earned: true, icon: '👣', color: '#EFF6FF', textColor: '#1D4ED8' },
  { id: '2', name: '수원의 지배자', desc: '수원 화성 내 모든 랜드마크 인증', earned: true, icon: '🏯', color: '#FEF3C7', textColor: '#D97706' },
  { id: '3', name: '올빼미 여행자', desc: '야간 시간대(20시 이후) 인증 완료', earned: true, icon: '🦉', color: '#F3E8FF', textColor: '#7C3AED' },
  { id: '4', name: '소통왕', desc: '커뮤니티 후기 글 10개 이상 작성', earned: false, icon: '💬', color: '#F1F5F9', textColor: '#64748B' },
  { id: '5', name: '프로 정복자', desc: '누적 인증 점수 10,000 XP 돌파', earned: false, icon: '👑', color: '#F1F5F9', textColor: '#64748B' },
];

export default function BadgeListScreen() {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color="#0F172A">
          획득 배지 보관함 🏆
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
          인증 달성 조건에 맞춰 해금한 뱃지들입니다.
        </CustomText>
      </View>

      <FlatList 
        data={DUMMY_BADGES}
        keyExtractor={(item) => item.id}
        numColumns={2}
        contentContainerStyle={styles.listContainer}
        columnWrapperStyle={styles.columnWrapper}
        showsVerticalScrollIndicator={false}
        renderItem={({ item }) => (
          <View style={[styles.badgeCard, !item.earned && styles.disabledCard]}>
            <View style={[styles.iconWrapper, { backgroundColor: item.earned ? item.color : '#F1F5F9' }]}>
              <CustomText style={styles.badgeIcon}>{item.icon}</CustomText>
            </View>
            <CustomText variant="Heading/H5" color="#1E293B" style={styles.badgeName}>
              {item.name}
            </CustomText>
            <CustomText variant="Body/Small" color="#64748B" style={styles.badgeDesc}>
              {item.desc}
            </CustomText>
            
            <View style={[
              styles.statusTag, 
              item.earned ? styles.earnedTag : styles.lockedTag
            ]}>
              <CustomText 
                variant="Label/Small" 
                color={item.earned ? '#059669' : '#64748B'}
                style={styles.statusText}
              >
                {item.earned ? '획득 완료' : '잠김'}
              </CustomText>
            </View>
          </View>
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8FAFC', // Slate-50 공통 배경
  },
  header: {
    paddingHorizontal: 24,
    paddingTop: 30,
    paddingBottom: 10,
  },
  headerSubtitle: {
    marginTop: 6,
    fontWeight: '500',
  },
  listContainer: {
    paddingHorizontal: 24,
    gap: 16,
    paddingBottom: 40,
  },
  columnWrapper: {
    justifyContent: 'space-between',
  },
  badgeCard: {
    width: '47%',
    backgroundColor: '#FFFFFF',
    padding: 18,
    borderRadius: 22,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.03,
    shadowRadius: 10,
    elevation: 2,
  },
  disabledCard: {
    opacity: 0.55,
  },
  iconWrapper: {
    width: 64,
    height: 64,
    borderRadius: 32,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 12,
  },
  badgeIcon: {
    fontSize: 32,
  },
  badgeName: {
    fontWeight: 'bold',
    textAlign: 'center',
  },
  badgeDesc: {
    textAlign: 'center',
    marginTop: 6,
    marginBottom: 14,
    lineHeight: 16,
  },
  statusTag: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 8,
  },
  earnedTag: {
    backgroundColor: '#ECFDF5', // Light green tag
  },
  lockedTag: {
    backgroundColor: '#F1F5F9',
  },
  statusText: {
    fontWeight: 'bold',
  },
});
