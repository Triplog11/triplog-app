import React from 'react';
import { StyleSheet, Text, View, FlatList, TouchableOpacity, SafeAreaView } from 'react-native';

const DUMMY_HISTORY = [
  { id: '1', place: '수원 화성 행궁', date: '2026.06.10', points: '+150 XP', status: 'GPS + 사진 인증 완료' },
  { id: '2', place: '수원 광교 호수공원', date: '2026.06.08', points: '+100 XP', status: 'GPS 인증 완료' },
  { id: '3', place: '에버랜드 테마파크', date: '2026.06.01', points: '+250 XP', status: 'GPS + 사진 + 리뷰 인증 완료' },
];

export default function HistoryScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>인증 히스토리 📸</Text>
        <Text style={styles.subtitle}>준수님이 방문을 인증한 기록들입니다.</Text>
      </View>

      <FlatList 
        data={DUMMY_HISTORY}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContainer}
        renderItem={({ item }) => (
          <TouchableOpacity style={styles.historyCard}>
            <View style={styles.cardHeader}>
              <Text style={styles.placeName}>{item.place}</Text>
              <Text style={styles.points}>{item.points}</Text>
            </View>
            <Text style={styles.status}>{item.status}</Text>
            <Text style={styles.date}>{item.date}</Text>
          </TouchableOpacity>
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
  },
  historyCard: {
    backgroundColor: '#1E293B',
    padding: 20,
    borderRadius: 16,
    borderLeftWidth: 4,
    borderLeftColor: '#38BDF8',
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  placeName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#F8FAFC',
  },
  points: {
    color: '#38BDF8',
    fontWeight: 'bold',
    fontSize: 14,
  },
  status: {
    color: '#4ADE80',
    fontSize: 13,
    marginBottom: 10,
  },
  date: {
    color: '#64748B',
    fontSize: 12,
  },
});
