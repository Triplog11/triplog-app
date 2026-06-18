import React from 'react';
import { StyleSheet, Text, View, FlatList, TouchableOpacity, SafeAreaView } from 'react-native';

const DUMMY_FEEDS = [
  { id: '1', author: '행복한여행가', content: '오늘 날씨가 너무 좋아서 수원 화성 행궁에 다녀왔어요! 역사 공부도 되고 뱃지도 받으니 일석이조네요 🏯✨', likes: 12, comments: 3 },
  { id: '2', author: '경기투어마스터', content: '남한산성 등산로 코스 추천합니다! 가볍게 트레킹하고 도토리묵 먹고 가세요 ⛰️', likes: 24, comments: 8 },
  { id: '3', place: '광교호수', author: '인증요정', content: '광교 야경 대박입니다... 야경 인증 뱃지 클리어했어요!', likes: 18, comments: 5 },
];

export default function CommunityScreen() {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>커뮤니티 피드 💬</Text>
        <Text style={styles.subtitle}>여행자들의 실시간 방문 후기를 확인하세요.</Text>
      </View>

      <FlatList 
        data={DUMMY_FEEDS}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContainer}
        renderItem={({ item }) => (
          <View style={styles.feedCard}>
            <View style={styles.authorRow}>
              <View style={styles.avatar} />
              <Text style={styles.authorName}>{item.author}</Text>
            </View>
            <Text style={styles.feedContent}>{item.content}</Text>
            <View style={styles.metaRow}>
              <Text style={styles.metaText}>❤️ 좋아요 {item.likes}개</Text>
              <Text style={styles.metaText}>💬 댓글 {item.comments}개</Text>
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
  },
  feedCard: {
    backgroundColor: '#1E293B',
    padding: 20,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#334155',
  },
  authorRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  avatar: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: '#38BDF8',
    marginRight: 10,
  },
  authorName: {
    color: '#F8FAFC',
    fontWeight: 'bold',
    fontSize: 14,
  },
  feedContent: {
    color: '#E2E8F0',
    fontSize: 14,
    lineHeight: 22,
    marginBottom: 16,
  },
  metaRow: {
    flexDirection: 'row',
    gap: 16,
  },
  metaText: {
    color: '#64748B',
    fontSize: 12,
  },
});
