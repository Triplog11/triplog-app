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
const HIDDEN_ROUTES = ['RegionDetail', 'History', 'Detail', 'Community', 'BadgeList', 'ProfileEdit', 'RegionCollection'];

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

function CenterTabItem({ meta, focused, onPress }) {
  return (
    <View style={styles.item}>
      <Pressable
        onPress={onPress}
        style={styles.centerButton}
        accessibilityRole="button"
        accessibilityState={{ selected: focused }}
        accessibilityLabel={meta.label}
      >
        <Feather name={meta.icon} size={24} color={theme.colors.white} />
      </Pressable>
      <Text
        style={[
          styles.label,
          styles.centerLabel,
          { color: focused ? theme.colors.navActive : theme.colors.navInactive },
        ]}
      >
        {meta.label}
      </Text>
    </View>
  );
}

/**
 * Bottom Tab Bar — prototype-2 레이아웃: 흰 배경 + 상단 1px 보더,
 * 가운데 인증 탭은 떠 있는 원형 프라이머리 버튼 (탭바 위로 돌출).
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
          const focused = state.index === index;
          const Item = route.name === 'Record' ? CenterTabItem : TabItem;
          return (
            <Item
              key={route.key}
              meta={meta}
              focused={focused}
              onPress={() => handlePress(route, focused)}
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
    // 가운데 인증 버튼이 탭바 위로 돌출되어야 하므로 clip하지 않는다
    // (숨김 전환은 height+opacity 동시 애니메이션이라 시각적으로 자연스러움)
    overflow: 'visible',
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
  centerButton: {
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: -24,
    shadowColor: '#000000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 12,
    elevation: 6,
  },
  centerLabel: {
    marginTop: 2,
  },
});
