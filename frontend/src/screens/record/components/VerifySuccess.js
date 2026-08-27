import React, { useEffect, useRef } from 'react';
import { StyleSheet, View, TouchableOpacity, Animated, Easing, ScrollView } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 5단계 — 인증 완료 전용 화면 (DESIGN.md §14: 토스트가 아닌 세리머니).
 * 체크마크는 표준 이징, 보상 행은 100ms 간격으로 순차 페이드.
 *
 * @param result {isVerified, rewards:[{policyId, description, xp, score}], totalXp, totalScore}
 */
export default function VerifySuccess({ landmark, result, onGoCollection, onGoHome }) {
  const rewards = result?.rewards ?? [];
  const verified = result?.isVerified !== false;
  const checkScale = useRef(new Animated.Value(0.6)).current;

  useEffect(() => {
    Animated.timing(checkScale, {
      toValue: 1,
      duration: theme.motion.slow,
      easing: Easing.bezier(...theme.motion.easeStandard),
      useNativeDriver: true,
    }).start();
  }, [checkScale]);

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.scroll} showsVerticalScrollIndicator={false}>
        <Animated.View style={[styles.checkCircle, { transform: [{ scale: checkScale }] }]}>
          <Ionicons name="checkmark" size={44} color="#FFFFFF" />
        </Animated.View>

        <CustomText variant="Heading/H3" color={theme.colors.text} style={styles.title}>
          {verified ? '인증 완료' : '기록 저장 완료'}
        </CustomText>
        <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.subtitle}>
          {landmark.landmarkName}
        </CustomText>
        {!verified && (
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.note}>
            이번 방문은 인증으로 집계되지 않았어요. 기록은 여행 기록에서 확인할 수 있어요.
          </CustomText>
        )}

        <View style={styles.rewardCard}>
          {rewards.length === 0 ? (
            <View style={styles.rewardRow}>
              <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                {verified ? '이미 획득한 랜드마크라 추가 보상은 없어요' : '지급된 보상이 없어요'}
              </CustomText>
            </View>
          ) : (
            rewards.map((reward, index) => (
              <RewardRow key={reward.policyId ?? index} reward={reward} index={index} last={index === rewards.length - 1} />
            ))
          )}
          <View style={styles.totalRow}>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>합계</CustomText>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
              +{result?.totalXp ?? 0} XP · +{result?.totalScore ?? 0}점
            </CustomText>
          </View>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity style={styles.primaryBtn} onPress={onGoCollection} activeOpacity={0.9}>
          <CustomText variant="UI/Button" color="#FFFFFF" style={styles.bold}>
            도감 보러 가기
          </CustomText>
        </TouchableOpacity>
        <TouchableOpacity style={styles.ghostBtn} onPress={onGoHome} activeOpacity={0.8}>
          <CustomText variant="UI/Button" color={theme.colors.textSecondary} style={styles.bold}>
            홈으로
          </CustomText>
        </TouchableOpacity>
      </View>
    </View>
  );
}

function RewardRow({ reward, index, last }) {
  const opacity = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.timing(opacity, {
      toValue: 1,
      duration: theme.motion.standard,
      delay: 100 * (index + 1),
      easing: Easing.bezier(...theme.motion.easeEnter),
      useNativeDriver: true,
    }).start();
  }, [opacity, index]);

  return (
    <Animated.View style={{ opacity }}>
      <View style={styles.rewardRow}>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.rewardLabel} numberOfLines={2}>
          {reward.description}
        </CustomText>
        <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
          +{reward.xp ?? 0} XP · +{reward.score ?? 0}점
        </CustomText>
      </View>
      {!last && <View style={styles.divider} />}
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scroll: {
    alignItems: 'center',
    paddingTop: 48,
    paddingHorizontal: theme.spacing.lg,
  },
  checkCircle: {
    width: 88,
    height: 88,
    borderRadius: 44,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: theme.spacing.lg,
  },
  title: {
    fontWeight: 'bold',
  },
  subtitle: {
    marginTop: 6,
  },
  note: {
    marginTop: theme.spacing.md,
    textAlign: 'center',
  },
  bold: {
    fontWeight: 'bold',
  },
  rewardCard: {
    alignSelf: 'stretch',
    marginTop: theme.spacing.xl,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    paddingHorizontal: theme.spacing.base,
  },
  rewardRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: theme.spacing.sm,
    paddingVertical: 14,
  },
  rewardLabel: {
    flex: 1,
  },
  divider: {
    height: 1,
    backgroundColor: theme.colors.border,
  },
  totalRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 14,
    borderTopWidth: 1,
    borderTopColor: theme.colors.borderStrong,
  },
  footer: {
    padding: theme.spacing.lg,
    // 탭바의 가운데 인증 플로팅 버튼과 겹치지 않도록 하단 여백 확보
    paddingBottom: 104,
    gap: theme.spacing.sm,
  },
  primaryBtn: {
    height: 56,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  ghostBtn: {
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
