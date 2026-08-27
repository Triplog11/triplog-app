import React, { useEffect, useRef } from 'react';
import { StyleSheet, View, TouchableOpacity, Animated, Easing } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

/**
 * 풀폭 기본 CTA. 전송 중에는 라벨 대신 흰 점 3개 애니메이션을 보여주고
 * 버튼 너비는 그대로 유지한다 (DESIGN.md §14 "인증 전송" 상태).
 */
export default function SubmitButton({ label, onPress, disabled, loading }) {
  const inactive = disabled || loading;
  return (
    <TouchableOpacity
      style={[styles.btn, disabled && !loading && styles.btnDisabled]}
      onPress={onPress}
      disabled={inactive}
      activeOpacity={0.9}
      accessibilityState={{ disabled: inactive, busy: loading }}
    >
      {loading ? (
        <ThreeDots />
      ) : (
        <CustomText variant="UI/Button" color={disabled ? theme.colors.textMuted : '#FFFFFF'} style={styles.bold}>
          {label}
        </CustomText>
      )}
    </TouchableOpacity>
  );
}

function ThreeDots() {
  const dots = useRef([new Animated.Value(0.3), new Animated.Value(0.3), new Animated.Value(0.3)]).current;

  useEffect(() => {
    const animations = dots.map((dot, index) =>
      Animated.loop(
        Animated.sequence([
          Animated.delay(index * 150),
          Animated.timing(dot, { toValue: 1, duration: 300, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
          Animated.timing(dot, { toValue: 0.3, duration: 300, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
          Animated.delay((2 - index) * 150),
        ]),
      ),
    );
    animations.forEach((a) => a.start());
    return () => animations.forEach((a) => a.stop());
  }, [dots]);

  return (
    <View style={styles.dots}>
      {dots.map((opacity, index) => (
        <Animated.View key={index} style={[styles.dot, { opacity }]} />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  btn: {
    height: 56,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  btnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
  },
  bold: {
    fontWeight: 'bold',
  },
  dots: {
    flexDirection: 'row',
    gap: 6,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#FFFFFF',
  },
});
