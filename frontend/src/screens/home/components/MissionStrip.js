import React, { useState, useEffect } from 'react';
import { StyleSheet, View } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { fetchMyMissions, MISSION_TYPE } from '../../../api/missions';

/**
 * 홈(지도) 헤더 아래 슬림 미션 스트립.
 * 지도가 주인공이라는 원칙을 지키기 위해 콤팩트하게, 진행 중인 주간 미션이 있을 때만 노출한다.
 * 로딩/에러/미션 없음 상태에서는 아무것도 렌더하지 않는다.
 */
export default function MissionStrip() {
  const [missions, setMissions] = useState(null);

  useEffect(() => {
    let mounted = true;
    fetchMyMissions({ missionType: MISSION_TYPE.WEEKLY })
      .then((result) => mounted && setMissions(result?.missions ?? []))
      .catch(() => mounted && setMissions([]));
    return () => {
      mounted = false;
    };
  }, []);

  if (!missions || missions.length === 0) return null;

  const total = missions.length;
  const completedCount = missions.filter((m) => m.completed).length;
  const next = missions.find((m) => !m.completed);
  const allDone = completedCount === total;

  return (
    <View style={styles.strip}>
      <View style={styles.iconWrap}>
        <Ionicons
          name={allDone ? 'checkmark-done' : 'flag'}
          size={16}
          color={theme.colors.primary}
        />
      </View>
      <View style={styles.body}>
        <CustomText variant="Caption" color={theme.colors.textSecondary}>
          이번 주 미션 · {completedCount}/{total} 완료
        </CustomText>
        <CustomText
          variant="Label/Medium"
          color={theme.colors.text}
          style={styles.name}
          numberOfLines={1}
        >
          {allDone ? '이번 주 미션을 모두 끝냈어요!' : next?.missionName}
        </CustomText>
      </View>
      {!allDone && next?.rewardXp != null && (
        <View style={styles.rewardPill}>
          <CustomText variant="Caption" color={theme.colors.primary} style={styles.rewardText}>
            +{next.rewardXp} XP
          </CustomText>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  strip: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    marginHorizontal: theme.spacing.base,
    marginBottom: theme.spacing.sm,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 10,
    backgroundColor: theme.colors.primarySoft,
    borderRadius: theme.rounded.card,
  },
  iconWrap: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: theme.colors.canvas,
    justifyContent: 'center',
    alignItems: 'center',
  },
  body: {
    flex: 1,
    gap: 1,
  },
  name: {
    fontWeight: 'bold',
  },
  rewardPill: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.pill ?? 9999,
    paddingHorizontal: 10,
    paddingVertical: 4,
  },
  rewardText: {
    fontWeight: 'bold',
  },
});
