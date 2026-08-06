import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, FlatList, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchNotifications, markNotificationRead } from '../../api/notifications';

/** 알림 종류(notificationType)별 아이콘 — 매칭 없으면 기본 벨 아이콘 */
const NOTIFICATION_ICONS = {
  LEVEL_UP: 'trending-up-outline',
  RANK_UP: 'trophy-outline',
  BADGE_ACQUIRED: 'ribbon-outline',
  CARD_ACQUIRED: 'albums-outline',
  REGION_COMPLETED: 'map-outline',
  LANDMARK_VERIFIED: 'checkmark-done-outline',
  WEEKLY_MISSION_COMPLETED: 'flag-outline',
};

function iconFor(notificationType) {
  const key = String(notificationType ?? '').toUpperCase();
  return NOTIFICATION_ICONS[key] ?? 'notifications-outline';
}

/** ISO 문자열(예: "2026-06-25T10:30:00")을 한국어 상대 시간 또는 YYYY.MM.DD로 표기 */
function formatCreatedAt(iso) {
  if (!iso) return '';
  const created = new Date(iso);
  if (Number.isNaN(created.getTime())) return '';

  const diffMs = Date.now() - created.getTime();
  const diffMin = Math.floor(diffMs / 60000);
  if (diffMin < 1) return '방금 전';
  if (diffMin < 60) return `${diffMin}분 전`;

  const diffHour = Math.floor(diffMin / 60);
  if (diffHour < 24) return `${diffHour}시간 전`;

  const diffDay = Math.floor(diffHour / 24);
  if (diffDay < 7) return `${diffDay}일 전`;

  const y = created.getFullYear();
  const m = String(created.getMonth() + 1).padStart(2, '0');
  const d = String(created.getDate()).padStart(2, '0');
  return `${y}.${m}.${d}`;
}

/** 알림 목록 — 백엔드 알림 API 연동 */
export default function NotificationScreen() {
  const [notifications, setNotifications] = useState([]);
  const [status, setStatus] = useState('loading'); // loading | ready | error

  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const result = await fetchNotifications({ page: 0, size: 30 });
      setNotifications(result?.notifications ?? []);
      setStatus('ready');
    } catch (error) {
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handlePress = useCallback(async (item) => {
    if (item.isRead) return;
    try {
      await markNotificationRead(item.notificationId);
      setNotifications((prev) =>
        prev.map((n) =>
          n.notificationId === item.notificationId ? { ...n, isRead: true } : n
        )
      );
    } catch (error) {
      // 읽음 처리 실패 시 목록은 그대로 두어 사용자가 다시 탭할 수 있도록 함
    }
  }, []);

  if (status === 'loading') {
    return (
      <SafeAreaView style={styles.container} edges={['bottom']}>
        <View style={styles.center}>
          <ActivityIndicator size="large" color={theme.colors.primary} />
        </View>
      </SafeAreaView>
    );
  }

  if (status === 'error') {
    return (
      <SafeAreaView style={styles.container} edges={['bottom']}>
        <View style={styles.center}>
          <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.centerText}>
            알림을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
          </CustomText>
          <TouchableOpacity style={styles.retryPill} onPress={load} activeOpacity={0.85}>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={notifications}
        keyExtractor={(item) => String(item.notificationId)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={
          <View style={styles.empty}>
            <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
              아직 도착한 알림이 없어요. 첫 인증을 남기면 소식을 보내드릴게요!
            </CustomText>
          </View>
        }
        renderItem={({ item }) => {
          const unread = item.isRead === false;
          return (
            <TouchableOpacity
              style={[styles.row, unread && styles.rowUnread]}
              onPress={() => handlePress(item)}
              activeOpacity={unread ? 0.7 : 1}
              disabled={!unread}
            >
              <View style={styles.icon}>
                <Ionicons
                  name={iconFor(item.notificationType)}
                  size={18}
                  color={theme.colors.primary}
                />
              </View>
              <View style={styles.body}>
                <View style={styles.titleRow}>
                  <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
                    {item.title}
                  </CustomText>
                  {unread && <View style={styles.dot} />}
                </View>
                <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                  {item.content}
                </CustomText>
                <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.time}>
                  {formatCreatedAt(item.createdAt)}
                </CustomText>
              </View>
            </TouchableOpacity>
          );
        }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: theme.spacing.lg,
    gap: theme.spacing.base,
  },
  centerText: {
    textAlign: 'center',
  },
  retryPill: {
    borderRadius: theme.rounded.pill ?? 9999,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 8,
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
