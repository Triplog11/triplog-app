import React, { useEffect } from 'react';
import { StyleSheet, Text, Pressable, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { getFocusedRouteNameFromRoute } from '@react-navigation/native';
import { Feather } from '@expo/vector-icons';
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withTiming,
  Easing,
} from 'react-native-reanimated';
import theme from '../../theme/theme';

const EASE_STANDARD = Easing.bezier(...theme.motion.easeStandard);

const TAB_META = {
  Home: { label: '홈', icon: 'home' },
  Collection: { label: '도감', icon: 'book-open' },
  Record: { label: '인증', icon: 'map-pin' },
  Ranking: { label: '랭킹', icon: 'award' },
  MyPage: { label: '마이', icon: 'user' },
};

/** 이 스택 화면들이 포커스되면 탭바를 접어서 숨김 */
const HIDDEN_ROUTES = ['RegionDetail', 'History', 'Detail', 'Community', 'BadgeList'];

const BAR_CONTENT_HEIGHT = 56;

function TabItem({ meta, focused, onPress }) {
  const tint = focused ? theme.colors.navActive : theme.colors.navInactive;

  return (
    <Pressable
      onPress={onPress}
      style={styles.item}
      accessibilityRole="button"
      accessibilityState={{ selected: focused }}
      accessibilityLabel={meta.label}
      android_ripple={{ color: theme.colors.surfaceDim, borderless: true }}
    >
      <Feather name={meta.icon} size={22} color={tint} />
      <Text style={[styles.label, { color: tint }]}>{meta.label}</Text>
    </Pressable>
  );
}

/**
 * Bottom Tab Bar — DESIGN.md §4: 흰 배경, 상단 1px #DEE2E6 보더,
 * 홈·도감·인증·랭킹·마이 5탭 전원 같은 행. Active #2AC1BC / Inactive #868E96.
 */
export default function TripLogTabBar({ state, navigation }) {
  const insets = useSafeAreaInsets();
  const focusedRoute = state.routes[state.index];
  const nestedName = getFocusedRouteNameFromRoute(focusedRoute);
  const hidden = HIDDEN_ROUTES.includes(nestedName);

  const barHeight = BAR_CONTENT_HEIGHT + insets.bottom;
  const visibility = useSharedValue(hidden ? 0 : 1);

  useEffect(() => {
    visibility.value = withTiming(hidden ? 0 : 1, {
      duration: theme.motion.standard,
      easing: EASE_STANDARD,
    });
  }, [hidden, visibility]);

  const barStyle = useAnimatedStyle(() => ({
    height: visibility.value * barHeight,
    opacity: visibility.value,
  }));

  const handlePress = (route, focused) => {
    const event = navigation.emit({
      type: 'tabPress',
      target: route.key,
      canPreventDefault: true,
    });
    if (!focused && !event.defaultPrevented) {
      navigation.navigate(route.name);
    }
  };

  return (
    <Animated.View style={[styles.bar, barStyle]} pointerEvents={hidden ? 'none' : 'auto'}>
      <View style={[styles.row, { paddingBottom: insets.bottom }]}>
        {state.routes.map((route, index) => {
          const meta = TAB_META[route.name];
          if (!meta) return null;
          return (
            <TabItem
              key={route.key}
              meta={meta}
              focused={state.index === index}
              onPress={() => handlePress(route, state.index === index)}
            />
          );
        })}
      </View>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  bar: {
    backgroundColor: theme.colors.white,
    borderTopWidth: 1,
    borderTopColor: theme.colors.border,
    overflow: 'hidden',
  },
  row: {
    height: '100%',
    flexDirection: 'row',
    alignItems: 'stretch',
  },
  item: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 3,
    minHeight: 44,
  },
  label: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: 11,
    fontWeight: '600',
    lineHeight: 13,
  },
});
