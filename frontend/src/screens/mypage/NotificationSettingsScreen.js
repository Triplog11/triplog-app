import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, ActivityIndicator, Switch } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchNotificationSettings, updateNotificationSettings } from '../../api/notifications';

/**
 * 알림 설정 항목 — key는 백엔드 필드명 그대로.
 * ⚠️ isWeeklyMissonCompleted는 백엔드 필드명 오타(Misson)를 그대로 따른다.
 */
const SETTING_ROWS = [
  { key: 'isLevelUp', label: '레벨업 알림' },
  { key: 'isRankUp', label: '랭크 상승 알림' },
  { key: 'isBadgeAcquired', label: '뱃지 획득 알림' },
  { key: 'isCardAcquired', label: '카드 획득 알림' },
  { key: 'isRegionCompleted', label: '지역 완성 알림' },
  { key: 'isLandmarkVerified', label: '랜드마크 인증 알림' },
  { key: 'isWeeklyMissonCompleted', label: '주간 미션 완료 알림' },
];

/** 설정 row가 아직 없을 때(404) 시작 기본값 — 전부 켜짐 */
const DEFAULT_SETTINGS = SETTING_ROWS.reduce((acc, row) => ({ ...acc, [row.key]: true }), {});

/** 알림 설정 — 7개 플래그 토글, 낙관적 업데이트 후 PATCH(전 필드 전송) */
export default function NotificationSettingsScreen() {
  const [settings, setSettings] = useState(null);
  const [status, setStatus] = useState('loading'); // loading | ready | error

  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const result = await fetchNotificationSettings();
      setSettings(result ?? DEFAULT_SETTINGS);
      setStatus('ready');
    } catch (error) {
      // 설정 row가 아직 없으면 404 → 기본값(전부 켜짐)으로 시작, 저장 시 생성된다
      if (error?.status === 404) {
        setSettings(DEFAULT_SETTINGS);
        setStatus('ready');
        return;
      }
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleToggle = useCallback(
    async (key) => {
      if (!settings) return;
      const previous = settings;
      // 낙관적 업데이트 — 새 객체로 해당 플래그만 뒤집는다 (불변)
      const next = { ...previous, [key]: !previous[key] };
      setSettings(next);
      try {
        // PATCH는 7개 플래그 전부 필수(@NotNull)이므로 next를 통째로 보낸다
        await updateNotificationSettings(next);
      } catch (error) {
        // 실패 시 이전 상태로 되돌린다 (설정 row 미생성 시 백엔드가 404를 준다)
        setSettings(previous);
      }
    },
    [settings]
  );

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
            알림 설정을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
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
      <ScrollView contentContainerStyle={styles.scroll} showsVerticalScrollIndicator={false}>
        <View style={styles.menuContainer}>
          {SETTING_ROWS.map((row, index) => (
            <View
              key={row.key}
              style={[styles.row, index === SETTING_ROWS.length - 1 && styles.rowLast]}
            >
              <CustomText variant="Body/Medium" color={theme.colors.text} style={styles.rowLabel}>
                {row.label}
              </CustomText>
              <Switch
                value={!!settings?.[row.key]}
                onValueChange={() => handleToggle(row.key)}
                trackColor={{ true: theme.colors.primary, false: theme.colors.border }}
                thumbColor={theme.colors.white}
              />
            </View>
          ))}
        </View>
      </ScrollView>
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
  bold: {
    fontWeight: 'bold',
  },
  scroll: {
    padding: theme.spacing.lg,
  },
  menuContainer: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    overflow: 'hidden',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 14,
    paddingHorizontal: theme.spacing.base,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.border,
  },
  rowLast: {
    borderBottomWidth: 0,
  },
  rowLabel: {
    fontWeight: '600',
  },
});
