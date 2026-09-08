import React, { useEffect, useRef } from 'react';
import { StyleSheet, View, Animated, Easing, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { NEARBY_STATUS } from '../hooks/useNearbyLandmarks';

/** 상태별 안내 문구 — 원인과 해결 방법을 한 문장에 함께 담는다 (DESIGN.md §10) */
const COPY = {
  [NEARBY_STATUS.SCANNING]: {
    icon: 'location',
    title: '현재 위치에서 인증할 수 있는 랜드마크를 찾고 있습니다',
    description: '위치는 단말기 안에서만 사용하며 서버로 보내지 않습니다.',
  },
  [NEARBY_STATUS.DENIED]: {
    icon: 'lock-closed-outline',
    title: '위치 권한이 필요합니다',
    description:
      '현재 위치로 주변 랜드마크를 찾습니다. 설정에서 위치 권한을 허용하시거나, 지역을 직접 선택해 인증해 주십시오.',
  },
  [NEARBY_STATUS.ERROR]: {
    icon: 'alert-circle-outline',
    title: '현재 위치를 확인하지 못했습니다',
    description:
      'GPS 신호가 약하거나 위치 서비스가 꺼져 있습니다. 잠시 후 다시 시도하시거나, 지역을 직접 선택해 인증해 주십시오.',
  },
  [NEARBY_STATUS.EMPTY]: {
    icon: 'map-outline',
    title: '근처에 인증할 수 있는 랜드마크가 없습니다',
    description:
      '현재 위치가 속한 지역에 등록된 랜드마크가 없습니다. 자리를 옮겨 다시 시도하시거나, 지역을 직접 선택해 인증해 주십시오.',
  },
};

/**
 * 1단계 — 현재 위치 탐색 및 5단계 — 주변에 랜드마크가 없을 때.
 * 위치 권한 거부와 위치 확인 실패도 같은 화면에서 다루고, 어느 경우에나 지역 직접 선택으로 빠져나갈 수 있다.
 *
 * @param status useNearbyLandmarks의 상태 (scanning | denied | error | empty)
 * @param placeLabel 확인된 현재 위치 라벨 (예: '대전 유성구')
 */
export default function NearbyScan({
  status,
  placeLabel,
  errorMessage,
  onRetry,
  onManualSelect,
  onBack,
  onOpenSettings,
}) {
  const pulse = useRef(new Animated.Value(0.35)).current;
  const scanning = status === NEARBY_STATUS.SCANNING;
  const copy = COPY[status] ?? COPY[NEARBY_STATUS.SCANNING];

  useEffect(() => {
    if (!scanning) return undefined;
    const loop = Animated.loop(
      Animated.sequence([
        Animated.timing(pulse, { toValue: 1, duration: 750, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
        Animated.timing(pulse, { toValue: 0.35, duration: 750, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
      ]),
    );
    loop.start();
    return () => loop.stop();
  }, [pulse, scanning]);

  return (
    <View style={styles.container}>
      <Animated.View style={[styles.pulseRing, { opacity: scanning ? pulse : 1 }]}>
        <View style={[styles.pulseDot, !scanning && styles.pulseDotIdle]}>
          <Ionicons name={copy.icon} size={30} color="#FFFFFF" />
        </View>
      </Animated.View>

      <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
        {copy.title}
      </CustomText>
      {placeLabel ? (
        <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.subtitle}>
          현재 위치는 {placeLabel}입니다.
        </CustomText>
      ) : null}
      <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.description}>
        {copy.description}
      </CustomText>
      {status === NEARBY_STATUS.ERROR && errorMessage ? (
        <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.description}>
          {errorMessage}
        </CustomText>
      ) : null}

      <View style={styles.footer}>
        {status === NEARBY_STATUS.DENIED && (
          <FilledButton label="설정 열기" onPress={onOpenSettings} />
        )}
        {(status === NEARBY_STATUS.ERROR || status === NEARBY_STATUS.EMPTY) && (
          <FilledButton label="다시 시도하기" onPress={onRetry} />
        )}
        {status === NEARBY_STATUS.DENIED && <OutlineButton label="다시 시도하기" onPress={onRetry} />}
        {scanning ? (
          <TextButton label="돌아가기" onPress={onBack} />
        ) : (
          <OutlineButton label="지역 직접 선택하기" onPress={onManualSelect} />
        )}
      </View>
    </View>
  );
}

function FilledButton({ label, onPress }) {
  return (
    <TouchableOpacity style={styles.filledBtn} onPress={onPress} activeOpacity={0.9}>
      <CustomText variant="UI/Button" color="#FFFFFF" style={styles.bold}>
        {label}
      </CustomText>
    </TouchableOpacity>
  );
}

function OutlineButton({ label, onPress }) {
  return (
    <TouchableOpacity style={styles.outlineBtn} onPress={onPress} activeOpacity={0.85}>
      <CustomText variant="UI/Button" color={theme.colors.primary} style={styles.bold}>
        {label}
      </CustomText>
    </TouchableOpacity>
  );
}

function TextButton({ label, onPress }) {
  return (
    <TouchableOpacity style={styles.textBtn} onPress={onPress} activeOpacity={0.8}>
      <CustomText variant="UI/Button" color={theme.colors.textSecondary} style={styles.bold}>
        {label}
      </CustomText>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    paddingTop: 48,
    paddingHorizontal: 32,
    // 탭바의 가운데 인증 플로팅 버튼과 겹치지 않도록 하단 여백 확보
    paddingBottom: 104,
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
  pulseDotIdle: {
    backgroundColor: theme.colors.textMuted,
  },
  title: {
    fontWeight: 'bold',
    textAlign: 'center',
  },
  subtitle: {
    marginTop: 6,
    textAlign: 'center',
  },
  description: {
    marginTop: theme.spacing.md,
    textAlign: 'center',
  },
  bold: {
    fontWeight: 'bold',
  },
  footer: {
    alignSelf: 'stretch',
    marginTop: 'auto',
    gap: theme.spacing.sm,
  },
  filledBtn: {
    height: 56,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  outlineBtn: {
    height: 56,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.canvas,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  textBtn: {
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
