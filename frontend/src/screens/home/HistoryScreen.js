import React from 'react';
import { StyleSheet, View, FlatList, TouchableOpacity, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';

const DUMMY_HISTORY = [
  { id: '1', place: '수원 화성 행궁', date: '2026.06.10', points: '+150 XP', status: 'GPS + 사진 인증 완료' },
  { id: '2', place: '수원 광교 호수공원', date: '2026.06.08', points: '+100 XP', status: 'GPS 인증 완료' },
  { id: '3', place: '에버랜드 테마파크', date: '2026.06.01', points: '+250 XP', status: 'GPS + 사진 + 리뷰 인증 완료' },
];

export default function HistoryScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color={theme.colors.text}>
          인증 히스토리 📸
        </CustomText>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.headerSubtitle}>
          준수님이 방문을 인증한 기록들입니다.
        </CustomText>
      </View>

      <FlatList 
        data={DUMMY_HISTORY}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContainer}
        showsVerticalScrollIndicator={false}
        renderItem={({ item }) => (
          <TouchableOpacity style={styles.historyCard} activeOpacity={0.8}>
            <View style={styles.cardHeader}>
              <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.placeName}>
                {item.place}
              </CustomText>
              <CustomText variant="Label/Large" color={theme.colors.primary} style={styles.points}>
                {item.points}
              </CustomText>
            </View>
            <CustomText variant="Body/Small" color={theme.colors.success} style={styles.status}>
              ✓ {item.status}
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.date}>
              {item.date}
            </CustomText>
          </TouchableOpacity>
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface, // Slate-50 공통 배경
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
  historyCard: {
    backgroundColor: theme.colors.canvas,
    padding: 20,
    borderRadius: 16,
    borderLeftWidth: 4,
    borderLeftColor: theme.colors.primary, // 브랜드 블루 포인트 사이드 바
    borderWidth: 1,
    borderColor: theme.colors.surfaceDim,
    shadowColor: theme.colors.text,
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.03,
    shadowRadius: 12,
    elevation: 2,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  placeName: {
    fontWeight: 'bold',
  },
  points: {
    fontWeight: 'bold',
  },
  status: {
    fontWeight: '600',
    marginBottom: 10,
  },
  date: {
    fontWeight: '500',
  },
});
