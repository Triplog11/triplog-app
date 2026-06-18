import React from 'react';
import { StyleSheet, Text, View, FlatList, SafeAreaView } from 'react-native';

const DUMMY_RANKINGS = [
  { rank: 1, name: '여행대장', level: 'Lv.99', xp: '99,200 XP' },
  { rank: 2, name: '경기도정복자', level: 'Lv.88', xp: '82,400 XP' },
  { rank: 3, name: '방문요정', level: 'Lv.76', xp: '71,100 XP' },
  { rank: 4, name: '김준수 (나)', level: 'Lv.42', xp: '42,680 XP', isMe: true },
  { rank: 5, name: '초보여행러', level: 'Lv.21', xp: '21,300 XP' },
];

export default function RankingScreen() {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>유저 리그 랭킹 🏆</Text>
        <Text style={styles.subtitle}>인증으로 모은 경험치 랭킹 경쟁입니다.</Text>
      </View>

      <FlatList 
        data={DUMMY_RANKINGS}
        keyExtractor={(item) => item.rank.toString()}
        contentContainerStyle={styles.listContainer}
        renderItem={({ item }) => (
          <View style={[styles.rankCard, item.isMe && styles.meCard]}>
            <View style={styles.rankNumContainer}>
              <Text style={[styles.rankNum, item.rank <= 3 && styles.topRankNum]}>
                {item.rank}
              </Text>
            </View>
            <View style={styles.infoContainer}>
              <Text style={styles.userName}>{item.name}</Text>
              <Text style={styles.userLevel}>{item.level}</Text>
            </View>
            <Text style={styles.xpText}>{item.xp}</Text>
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
    gap: 12,
  },
  rankCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#1E293B',
    padding: 16,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#334155',
  },
  meCard: {
    borderColor: '#38BDF8',
    backgroundColor: '#1E293B',
  },
  rankNumContainer: {
    width: 32,
    alignItems: 'center',
  },
  rankNum: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#94A3B8',
  },
  topRankNum: {
    color: '#F59E0B',
    fontSize: 22,
  },
  infoContainer: {
    flex: 1,
    marginLeft: 16,
  },
  userName: {
    color: '#F8FAFC',
    fontWeight: 'bold',
    fontSize: 15,
  },
  userLevel: {
    color: '#64748B',
    fontSize: 12,
    marginTop: 2,
  },
  xpText: {
    color: '#38BDF8',
    fontWeight: 'bold',
    fontSize: 14,
  },
});
