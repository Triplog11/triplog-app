import React from 'react';
import { StyleSheet, View, FlatList, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';

const DUMMY_FEEDS = [
  { id: '1', author: '행복한여행가', content: '오늘 날씨가 너무 좋아서 수원 화성 행궁에 다녀왔어요! 역사 공부도 되고 뱃지도 받으니 일석이조네요 🏯✨', likes: 12, comments: 3, avatarColor: '#EFF6FF', avatarText: '행' },
  { id: '2', author: '경기투어마스터', content: '남한산성 등산로 코스 추천합니다! 가볍게 트레킹하고 도토리묵 먹고 가세요 ⛰️', likes: 24, comments: 8, avatarColor: '#FEF3C7', avatarText: '경' },
  { id: '3', author: '인증요정', content: '광교 야경 대박입니다... 야경 인증 뱃지 클리어했어요! 🌃', likes: 18, comments: 5, avatarColor: '#F3E8FF', avatarText: '인' },
];

export default function CommunityScreen() {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color="#0F172A">
          커뮤니티 피드 💬
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
          여행자들의 실시간 방문 후기를 확인하세요.
        </CustomText>
      </View>

      <FlatList 
        data={DUMMY_FEEDS}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContainer}
        showsVerticalScrollIndicator={false}
        renderItem={({ item }) => (
          <View style={styles.feedCard}>
            <View style={styles.authorRow}>
              <View style={[styles.avatar, { backgroundColor: item.avatarColor }]}>
                <CustomText variant="Label/Small" color="#475569">{item.avatarText}</CustomText>
              </View>
              <CustomText variant="Heading/H5" color="#0F172A" style={styles.authorName}>
                {item.author}
              </CustomText>
            </View>
            <CustomText variant="Body/Small" color="#334155" style={styles.feedContent}>
              {item.content}
            </CustomText>
            <View style={styles.metaRow}>
              <CustomText variant="Caption" color="#64748B" style={styles.metaText}>
                ❤️ 좋아요 {item.likes}개
              </CustomText>
              <CustomText variant="Caption" color="#64748B" style={styles.metaText}>
                💬 댓글 {item.comments}개
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
    paddingBottom: 16,
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
  feedCard: {
    backgroundColor: '#FFFFFF',
    padding: 20,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.03,
    shadowRadius: 12,
    elevation: 2,
  },
  authorRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 14,
  },
  avatar: {
    width: 32,
    height: 32,
    borderRadius: 16,
    marginRight: 10,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  authorName: {
    fontWeight: 'bold',
  },
  feedContent: {
    lineHeight: 22,
    marginBottom: 16,
    fontWeight: '400',
  },
  metaRow: {
    flexDirection: 'row',
    gap: 16,
    borderTopWidth: 1,
    borderTopColor: '#F1F5F9',
    paddingTop: 12,
  },
  metaText: {
    fontWeight: '500',
  },
});
