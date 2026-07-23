import React from 'react';
import { StyleSheet, View, FlatList } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { TRAVEL_LOGS } from '../../data/activity';

/** 여행 기록 목록 (기록 API 연동 전 목데이터) */
export default function TravelLogScreen() {
  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={TRAVEL_LOGS}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={
          <View style={styles.empty}>
            <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
              아직 남긴 기록이 없어요. 인증을 마치면 후기를 남길 수 있어요!
            </CustomText>
          </View>
        }
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={styles.headerRow}>
              <View>
                <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
                  {item.place}
                </CustomText>
                <CustomText variant="Caption" color={theme.colors.textSecondary}>
                  {item.region} · {item.date}
                </CustomText>
              </View>
              <View style={styles.starRow}>
                {Array.from({ length: 5 }).map((_, i) => (
                  <Ionicons
                    key={i}
                    name={i < item.rating ? 'star' : 'star-outline'}
                    size={12}
                    color={i < item.rating ? theme.colors.warning : theme.colors.border}
                  />
                ))}
              </View>
            </View>

            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.note}>
              {item.note}
            </CustomText>

            {item.photoCount > 0 && (
              <View style={styles.photoRow}>
                <Ionicons name="images-outline" size={14} color={theme.colors.primary} />
                <CustomText variant="Caption" color={theme.colors.primary} style={styles.bold}>
                  사진 {item.photoCount}장
                </CustomText>
              </View>
            )}
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
  list: {
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  card: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
    gap: theme.spacing.sm,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
  },
  bold: {
    fontWeight: 'bold',
  },
  starRow: {
    flexDirection: 'row',
    gap: 1,
  },
  note: {
    lineHeight: 20,
  },
  photoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 5,
  },
  empty: {
    paddingTop: 80,
    paddingHorizontal: 30,
  },
  emptyText: {
    textAlign: 'center',
  },
});
