import React from 'react';
import { StyleSheet, View, FlatList } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { NOTIFICATIONS, NOTIFICATION_ICONS } from '../../data/activity';

/** 알림 목록 (알림 API 연동 전 목데이터) */
export default function NotificationScreen() {
  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={NOTIFICATIONS}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={
          <View style={styles.empty}>
            <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
              아직 받은 알림이 없어요. 첫 인증을 남기면 소식을 보내드릴게요!
            </CustomText>
          </View>
        }
        renderItem={({ item }) => (
          <View style={[styles.row, item.unread && styles.rowUnread]}>
            <View style={styles.icon}>
              <Ionicons
                name={NOTIFICATION_ICONS[item.type] ?? 'notifications-outline'}
                size={18}
                color={theme.colors.primary}
              />
            </View>
            <View style={styles.body}>
              <View style={styles.titleRow}>
                <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
                  {item.title}
                </CustomText>
                {item.unread && <View style={styles.dot} />}
              </View>
              <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                {item.body}
              </CustomText>
              <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.time}>
                {item.time}
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
  list: {
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  row: {
    flexDirection: 'row',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  rowUnread: {
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
  },
  icon: {
    width: 36,
    height: 36,
    borderRadius: 18,
    backgroundColor: theme.colors.canvas,
    justifyContent: 'center',
    alignItems: 'center',
  },
  body: {
    flex: 1,
    gap: 3,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  bold: {
    fontWeight: 'bold',
  },
  dot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: theme.colors.error,
  },
  time: {
    marginTop: 2,
  },
  empty: {
    paddingTop: 80,
    paddingHorizontal: 30,
  },
  emptyText: {
    textAlign: 'center',
  },
});
