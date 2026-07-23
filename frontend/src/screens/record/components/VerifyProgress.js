import React, { useEffect, useRef } from 'react';
import { StyleSheet, View, Animated, Easing } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { formatDistance } from '../../../data/nearbyLandmarks';

/**
 * GPS 인증 진행 화면 — DESIGN.md §15: 인증은 담담한 톤, 스프링 금지.
 * 위치 점이 두 투명도 사이를 1.5초 주기로 펄스한다.
 */
export default function VerifyProgress({ landmark, distanceM, onComplete }) {
  const pulse = useRef(new Animated.Value(0.35)).current;

  useEffect(() => {
    const loop = Animated.loop(
      Animated.sequence([
        Animated.timing(pulse, { toValue: 1, duration: 750, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
        Animated.timing(pulse, { toValue: 0.35, duration: 750, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
      ]),
    );
    loop.start();
    return () => loop.stop();
  }, [pulse]);

  useEffect(() => {
    const timer = setTimeout(onComplete, 1800);
    return () => clearTimeout(timer);
  }, [onComplete]);

  return (
    <View style={styles.container}>
      <Animated.View style={[styles.pulseRing, { opacity: pulse }]}>
        <View style={styles.pulseDot}>
          <Ionicons name="location" size={30} color="#FFFFFF" />
        </View>
      </Animated.View>

      <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
        현재 위치를 확인하고 있어요
      </CustomText>
      <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.subtitle}>
        {landmark.name}
      </CustomText>

      <View style={styles.infoCard}>
        <View style={styles.infoRow}>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>랜드마크까지</CustomText>
          <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
            {formatDistance(distanceM)}
          </CustomText>
        </View>
        <View style={styles.divider} />
        <View style={styles.infoRow}>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>인증 반경</CustomText>
          <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
            {landmark.radiusM}m
          </CustomText>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 32,
  },
  pulseRing: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: theme.colors.primarySoft,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 28,
  },
  pulseDot: {
    width: 68,
    height: 68,
    borderRadius: 34,
    backgroundColor: theme.colors.locationBlue,
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    fontWeight: 'bold',
    textAlign: 'center',
  },
  subtitle: {
    marginTop: 6,
  },
  bold: {
    fontWeight: 'bold',
  },
  infoCard: {
    alignSelf: 'stretch',
    marginTop: 32,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    paddingHorizontal: theme.spacing.base,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 14,
  },
  divider: {
    height: 1,
    backgroundColor: theme.colors.border,
  },
});
