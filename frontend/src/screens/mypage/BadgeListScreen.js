import React from 'react';
import { StyleSheet, Text, View, FlatList, SafeAreaView } from 'react-native';

const DUMMY_BADGES = [
  { id: '1', name: '첫 걸음마', desc: '첫 번째 장소 방문 인증 성공', earned: true, icon: '👣' },
  { id: '2', name: '수원의 지배자', desc: '수원 화성 내 모든 랜드마크 인증', earned: true, icon: '🏯' },
  { id: '3', name: '올빼미 여행자', desc: '야간 시간대(20시 이후) 인증 완료', earned: true, icon: '🦉' },
  { id: '4', name: '소통왕', desc: '커뮤니티 후기 글 10개 이상 작성', earned: false, icon: '💬' },
  { id: '5', name: '프로 정복자', desc: '누적 인증 점수 10,000 XP 돌파', earned: false, icon: '👑' },
];

export default function BadgeListScreen() {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>획득 배지 보관함 🏆</Text>
        <Text style={styles.subtitle}>인증 달성 조건에 맞춰 해금한 뱃지들입니다.</Text>
      </View>

      <FlatList 
        data={DUMMY_BADGES}
        keyExtractor={(item) => item.id}
        numColumns={2}
        contentContainerStyle={styles.listContainer}
        columnWrapperStyle={styles.columnWrapper}
        renderItem={({ item }) => (
          <View style={[styles.badgeCard, !item.earned && styles.disabledCard]}>
            <Text style={styles.badgeIcon}>{item.icon}</Text>
            <Text style={styles.badgeName}>{item.name}</Text>
            <Text style={styles.badgeDesc}>{item.desc}</Text>
            <View style={[styles.statusTag, item.earned ? styles.earnedTag : styles.lockedTag]}>
              <Text style={styles.statusText}>{item.earned ? '획득 완료' : '잠김'}</Text>
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
    backgroundColor: '#0F172A',
  },
  header: {
    padding: 24,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#F8FAFC',
  },
  subtitle: {
    fontSize: 14,
    color: '#94A3B8',
    marginTop: 6,
  },
  listContainer: {
    paddingHorizontal: 24,
    gap: 16,
    paddingBottom: 24,
  },
  columnWrapper: {
    justifyContent: 'space-between',
  },
  badgeCard: {
    width: '47%',
    backgroundColor: '#1E293B',
    padding: 16,
    borderRadius: 20,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#334155',
  },
  disabledCard: {
    opacity: 0.5,
  },
  badgeIcon: {
    fontSize: 40,
    marginBottom: 10,
  },
  badgeName: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#F8FAFC',
    textAlign: 'center',
  },
  badgeDesc: {
    fontSize: 11,
    color: '#94A3B8',
    textAlign: 'center',
    marginTop: 6,
    marginBottom: 12,
    lineHeight: 15,
  },
  statusTag: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 8,
  },
  earnedTag: {
    backgroundColor: '#10B981',
  },
  lockedTag: {
    backgroundColor: '#475569',
  },
  statusText: {
    color: '#FFFFFF',
    fontSize: 10,
    fontWeight: 'bold',
  },
});
