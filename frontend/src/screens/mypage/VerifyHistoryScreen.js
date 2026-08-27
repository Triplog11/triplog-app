import React from 'react';
import { StyleSheet, View, FlatList } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { VERIFY_HISTORY } from '../../data/activity';

/** 인증 내역 — 타임라인 형태 (인증 이력 API 연동 전 목데이터) */
export default function VerifyHistoryScreen() {
  const totalXp = VERIFY_HISTORY.reduce((sum, item) => sum + item.xp, 0);

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={VERIFY_HISTORY}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={
          <View>
          <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.sampleNote}>
            지금은 예시 데이터예요. 곧 실제 기록으로 채워져요!
          </CustomText>
          <View style={styles.summary}>
            <View style={styles.summaryItem}>
              <CustomText variant="Caption" color={theme.colors.textSecondary}>총 인증</CustomText>
              <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.bold}>
                {VERIFY_HISTORY.length}회
              </CustomText>
            </View>
            <View style={styles.summaryDivider} />
            <View style={styles.summaryItem}>
              <CustomText variant="Caption" color={theme.colors.textSecondary}>획득 경험치</CustomText>
              <CustomText variant="Heading/H4" color={theme.colors.primary} style={styles.bold}>
                {totalXp} XP
              </CustomText>
            </View>
          </View>
          </View>
        }
        ListEmptyComponent={
          <View style={styles.empty}>
            <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
              아직 인증 내역이 없어요. 첫 번째 랜드마크를 인증해 보세요!
            </CustomText>
          </View>
        }
        renderItem={({ item, index }) => (
          <View style={styles.row}>
            <View style={styles.timeline}>
              <View style={styles.timelineDot} />
              {index < VERIFY_HISTORY.length - 1 && <View style={styles.timelineLine} />}
            </View>
            <View style={styles.card}>
              <View style={styles.cardHeader}>
                <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
                  {item.place}
                </CustomText>
                <View style={styles.methodPill}>
                  <Ionicons name="location" size={10} color={theme.colors.primary} />
                  <CustomText variant="Caption" color={theme.colors.primary} style={styles.bold}>
                    {item.method}
                  </CustomText>
                </View>
              </View>
              <CustomText variant="Caption" color={theme.colors.textSecondary}>
                {item.region} · {item.date}
              </CustomText>
              <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.reward}>
                +{item.xp} XP · {item.point}점
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
    backgroundColor: theme.colors.surface,
  },
  sampleNote: {
    textAlign: 'center',
    marginBottom: theme.spacing.sm,
  },
  list: {
    padding: theme.spacing.lg,
  },
  bold: {
    fontWeight: 'bold',
  },
  summary: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    paddingVertical: theme.spacing.base,
    marginBottom: theme.spacing.base,
  },
  summaryItem: {
    flex: 1,
    alignItems: 'center',
    gap: 3,
  },
  summaryDivider: {
    width: 1,
    height: 32,
    backgroundColor: theme.colors.border,
  },
  row: {
    flexDirection: 'row',
    gap: theme.spacing.sm,
  },
  timeline: {
    width: 14,
    alignItems: 'center',
    paddingTop: 6,
  },
  timelineDot: {
    width: 9,
    height: 9,
    borderRadius: 5,
    backgroundColor: theme.colors.primary,
  },
  timelineLine: {
    width: 2,
    flex: 1,
    backgroundColor: theme.colors.border,
    marginTop: 3,
  },
  card: {
    flex: 1,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
    marginBottom: theme.spacing.sm,
    gap: 3,
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  methodPill: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 3,
    backgroundColor: theme.colors.primarySoft,
    borderRadius: theme.rounded.sm,
    paddingHorizontal: 6,
    paddingVertical: 2,
  },
  reward: {
    marginTop: 3,
  },
  empty: {
    paddingTop: 60,
    paddingHorizontal: 30,
  },
  emptyText: {
    textAlign: 'center',
  },
});
