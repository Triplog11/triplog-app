import React, { useEffect, useRef, useState } from 'react';
import { StyleSheet, View, TouchableOpacity, Animated, Easing, Modal, Pressable } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { GRADE_CONFIG } from '../../../data/collection';

/**
 * 인증 성공 — 전용 확인 화면 (DESIGN.md §14: 토스트가 아닌 세리머니).
 * 카드 획득 팝업은 §15에서 오버슈트가 허용된 두 곳 중 하나.
 */
export default function VerifySuccess({ landmark, onGoCollection, onDone }) {
  const grade = GRADE_CONFIG[landmark.grade];
  const [showCardPopup, setShowCardPopup] = useState(true);

  const checkScale = useRef(new Animated.Value(0.6)).current;
  const rowsOpacity = useRef(new Animated.Value(0)).current;
  const cardScale = useRef(new Animated.Value(0.8)).current;

  useEffect(() => {
    // 체크마크는 표준 이징 (인증은 정확함이 우선)
    Animated.timing(checkScale, {
      toValue: 1,
      duration: theme.motion.slow,
      easing: Easing.bezier(...theme.motion.easeStandard),
      useNativeDriver: true,
    }).start();
    Animated.timing(rowsOpacity, {
      toValue: 1,
      duration: theme.motion.standard,
      delay: 100,
      useNativeDriver: true,
    }).start();
  }, [checkScale, rowsOpacity]);

  useEffect(() => {
    if (!showCardPopup) return;
    // 카드 획득은 오버슈트 허용 (리워드 비트)
    Animated.spring(cardScale, {
      toValue: 1,
      friction: 5,
      tension: 90,
      useNativeDriver: true,
    }).start();
  }, [showCardPopup, cardScale]);

  return (
    <View style={styles.container}>
      <Animated.View style={[styles.checkCircle, { transform: [{ scale: checkScale }] }]}>
        <Ionicons name="checkmark" size={44} color="#FFFFFF" />
      </Animated.View>

      <CustomText variant="Heading/H3" color={theme.colors.text} style={styles.title}>
        인증 완료
      </CustomText>
      <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.subtitle}>
        {landmark.name}
      </CustomText>

      <Animated.View style={[styles.rewardCard, { opacity: rowsOpacity }]}>
        <View style={styles.rewardRow}>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>획득 경험치</CustomText>
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
            +{landmark.xp} XP
          </CustomText>
        </View>
        <View style={styles.divider} />
        <View style={styles.rewardRow}>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>획득 점수</CustomText>
          <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
            +{landmark.point}점
          </CustomText>
        </View>
        <View style={styles.divider} />
        <View style={styles.rewardRow}>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>수집 카드</CustomText>
          <View style={[styles.gradePill, { backgroundColor: grade.soft }]}>
            <CustomText variant="Caption" color={grade.color} style={styles.bold}>
              {landmark.grade}
            </CustomText>
          </View>
        </View>
      </Animated.View>

      <View style={styles.footer}>
        <TouchableOpacity style={styles.primaryBtn} onPress={onGoCollection} activeOpacity={0.9}>
          <CustomText variant="UI/Button" color="#FFFFFF" style={styles.bold}>
            도감 보러 가기
          </CustomText>
        </TouchableOpacity>
        <TouchableOpacity style={styles.ghostBtn} onPress={onDone} activeOpacity={0.8}>
          <CustomText variant="UI/Button" color={theme.colors.textSecondary} style={styles.bold}>
            목록으로
          </CustomText>
        </TouchableOpacity>
      </View>

      {/* 카드 획득 팝업 */}
      <Modal visible={showCardPopup} transparent animationType="fade">
        <Pressable style={styles.popupBackdrop} onPress={() => setShowCardPopup(false)}>
          <Pressable style={styles.popupCard} onPress={() => {}}>
            <View style={styles.newBadge}>
              <CustomText variant="Caption" color={theme.colors.primary} style={styles.bold}>
                NEW CARD!
              </CustomText>
            </View>
            <Animated.View
              style={[styles.cardArt, { backgroundColor: grade.soft, transform: [{ scale: cardScale }] }]}
            >
              <Ionicons name="star" size={34} color={grade.color} />
            </Animated.View>
            <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.bold}>
              {landmark.name}
            </CustomText>
            <View style={[styles.gradePill, { backgroundColor: grade.soft }]}>
              <CustomText variant="Caption" color={grade.color} style={styles.bold}>
                {landmark.grade}
              </CustomText>
            </View>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.popupHint}>
              도감에 새 카드가 담겼어요!
            </CustomText>
            <TouchableOpacity
              style={styles.popupBtn}
              onPress={() => setShowCardPopup(false)}
              activeOpacity={0.9}
            >
              <CustomText variant="UI/Button" color="#FFFFFF" style={styles.bold}>
                확인
              </CustomText>
            </TouchableOpacity>
          </Pressable>
        </Pressable>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
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
    paddingVertical: 14,
  },
  divider: {
    height: 1,
    backgroundColor: theme.colors.border,
  },
  gradePill: {
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 10,
    paddingVertical: 3,
  },
  footer: {
    alignSelf: 'stretch',
    marginTop: theme.spacing.xl,
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
  popupBackdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    paddingHorizontal: theme.spacing.xl,
  },
  popupCard: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.xl,
    padding: theme.spacing.xl,
    alignItems: 'center',
    gap: theme.spacing.md,
  },
  newBadge: {
    backgroundColor: theme.colors.primarySoft,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 12,
    paddingVertical: 4,
  },
  cardArt: {
    width: 84,
    height: 116,
    borderRadius: theme.rounded.card,
    justifyContent: 'center',
    alignItems: 'center',
  },
  popupHint: {
    textAlign: 'center',
  },
  popupBtn: {
    alignSelf: 'stretch',
    height: 52,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing.xs,
  },
});
