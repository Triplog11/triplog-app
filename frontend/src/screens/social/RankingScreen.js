import React from 'react';
import { StyleSheet, View, FlatList, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';

const DUMMY_RANKINGS = [
  { rank: 1, name: '여행대장 👑', level: 'Lv.99', xp: '99,200 XP', color: '#EAB308' }, // Gold
  { rank: 2, name: '경기도정복자 🥈', level: 'Lv.88', xp: '82,400 XP', color: '#94A3B8' }, // Silver
  { rank: 3, name: '방문요정 🥉', level: 'Lv.76', xp: '71,100 XP', color: '#B45309' }, // Bronze
  { rank: 4, name: '김준수 (나) 🎒', level: 'Lv.42', xp: '42,680 XP', isMe: true, color: '#64748B' },
  { rank: 5, name: '초보여행러 👣', level: 'Lv.21', xp: '21,300 XP', color: '#64748B' },
];

export default function RankingScreen() {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color="#0F172A">
          유저 리그 랭킹 🏆
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
          인증으로 모은 경험치 랭킹 경쟁입니다.
        </CustomText>
      </View>

      <FlatList 
        data={DUMMY_RANKINGS}
        keyExtractor={(item) => item.rank.toString()}
        contentContainerStyle={styles.listContainer}
        showsVerticalScrollIndicator={false}
        renderItem={({ item }) => (
          <View style={[
            styles.rankCard, 
            item.isMe ? styles.meCard : styles.normalCard
          ]}>
            <View style={styles.rankNumContainer}>
              <CustomText 
                variant={item.rank <= 3 ? 'Heading/H3' : 'Heading/H4'} 
                color={item.rank <= 3 ? item.color : '#94A3B8'}
                style={styles.rankNum}
              >
                {item.rank}
              </CustomText>
            </View>
            
            <View style={styles.infoContainer}>
              <CustomText 
                variant="Heading/H5" 
                color={item.isMe ? '#1D4ED8' : '#0F172A'} 
                style={styles.userName}
              >
                {item.name}
              </CustomText>
              <CustomText variant="Caption" color="#64748B" style={styles.userLevel}>
                {item.level}
              </CustomText>
            </View>
            
            <CustomText variant="Label/Large" color="#3B82F6" style={styles.xpText}>
              {item.xp}
            </CustomText>
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
    gap: 12,
    paddingBottom: 40,
  },
  rankCard: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    borderRadius: 18,
    borderWidth: 1,
  },
  normalCard: {
    backgroundColor: '#FFFFFF',
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.02,
    shadowRadius: 8,
    elevation: 1,
  },
  meCard: {
    backgroundColor: '#EFF6FF', // Light blue tint for current user
    borderColor: '#93C5FD', // Thicker blue border
    shadowColor: '#3B82F6',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.05,
    shadowRadius: 12,
    elevation: 3,
  },
  rankNumContainer: {
    width: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  rankNum: {
    fontWeight: 'bold',
  },
  infoContainer: {
    flex: 1,
    marginLeft: 14,
  },
  userName: {
    fontWeight: 'bold',
  },
  userLevel: {
    marginTop: 2,
    fontWeight: '500',
  },
  xpText: {
    fontWeight: 'bold',
  },
});
