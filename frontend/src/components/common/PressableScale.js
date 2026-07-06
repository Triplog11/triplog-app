import React from 'react';
import { Pressable } from 'react-native';
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withTiming,
  Easing,
} from 'react-native-reanimated';
import theme from '../../theme/theme';

const AnimatedPressable = Animated.createAnimatedComponent(Pressable);
const EASE_STANDARD = Easing.bezier(...theme.motion.easeStandard);

/**
 * 카드/버튼 탭 피드백 — DESIGN.md §15 #1: 1.0 → 0.98, motion-fast / ease-standard.
 * "Tactile but not bouncy" — 스프링 오버슈트는 뱃지 획득/찜 토글 전용이므로 여기선 금지.
 */
export default function PressableScale({
  children,
  pressScale = 0.98,
  style,
  onPress,
  disabled,
  hitSlop,
  accessibilityLabel,
  accessibilityRole = 'button',
}) {
  const scale = useSharedValue(1);

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [{ scale: scale.value }],
  }));

  return (
    <AnimatedPressable
      onPress={onPress}
      disabled={disabled}
      hitSlop={hitSlop}
      accessibilityLabel={accessibilityLabel}
      accessibilityRole={accessibilityRole}
      onPressIn={() => {
        scale.value = withTiming(pressScale, {
          duration: theme.motion.fast,
          easing: EASE_STANDARD,
        });
      }}
      onPressOut={() => {
        scale.value = withTiming(1, {
          duration: theme.motion.fast,
          easing: EASE_STANDARD,
        });
      }}
      style={[style, animatedStyle]}
    >
      {children}
    </AnimatedPressable>
  );
}
