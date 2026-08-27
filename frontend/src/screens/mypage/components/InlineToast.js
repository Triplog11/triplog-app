import React, { useEffect, useRef } from 'react';
import { StyleSheet, Animated } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

const AUTO_DISMISS_MS = 2500;

/**
 * 하단 토스트 — DESIGN.md §4 Toast(다크 배경, 흰 14px, 2.5s 자동 닫힘).
 * message가 바뀔 때마다 나타났다가 사라진다.
 */
export default function InlineToast({ message, onDismiss }) {
  const opacity = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (!message) return undefined;
    Animated.timing(opacity, {
      toValue: 1, duration: theme.motion.standard, useNativeDriver: true,
    }).start();
    const timer = setTimeout(() => {
      Animated.timing(opacity, {
        toValue: 0, duration: theme.motion.standard, useNativeDriver: true,
      }).start(() => onDismiss?.());
    }, AUTO_DISMISS_MS);
    return () => clearTimeout(timer);
  }, [message, opacity, onDismiss]);

  if (!message) return null;

  return (
    <Animated.View style={[styles.toast, { opacity }]} pointerEvents="none">
      <CustomText variant="Body/Medium" color={theme.colors.white}>
        {message}
      </CustomText>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  toast: {
    position: 'absolute',
    left: theme.spacing.lg,
    right: theme.spacing.lg,
    bottom: theme.spacing.xl,
    backgroundColor: theme.colors.text,
    borderRadius: theme.rounded.md,
    paddingVertical: theme.spacing.md,
    paddingHorizontal: theme.spacing.base,
    alignItems: 'center',
  },
});
