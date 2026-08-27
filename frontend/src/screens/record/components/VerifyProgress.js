import React, { useEffect, useRef, useState, useCallback } from 'react';
import { StyleSheet, View, Animated, Easing, TouchableOpacity, Linking } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import * as Location from 'expo-location';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { distanceInMeters, formatDistance, getLandmarkCoords } from '../../../utils/geo';
import OutOfRangeModal from './OutOfRangeModal';

const RADIUS_M = 100;

/**
 * 3단계 — 현재 위치 확인 (DESIGN.md §14/§15: 담담한 톤, 스프링 금지).
 * 랜드마크 좌표가 있으면 100m 반경을 검사하고, 없으면(현재 백엔드) 거리는 `--m`로 두고
 * 위치 확인만 마친 뒤 다음 단계로 넘긴다.
 *
 * @param onVerified ({coords, accuracyM, distanceM}) 위치 확인을 마치고 기록 작성으로 이동
 */
export default function VerifyProgress({ landmark, onVerified }) {
  const pulse = useRef(new Animated.Value(0.35)).current;
  const [status, setStatus] = useState('loading'); // loading | denied | error | ready
  const [position, setPosition] = useState(null); // {coords, accuracyM, distanceM}
  const [outOfRange, setOutOfRange] = useState(null); // remainingM

  const landmarkCoords = getLandmarkCoords(landmark);

  const locate = useCallback(async () => {
    setStatus('loading');
    setOutOfRange(null);
    try {
      const { status: permission } = await Location.requestForegroundPermissionsAsync();
      if (permission !== 'granted') {
        setStatus('denied');
        return;
      }
      // 실내·에뮬레이터에서 현재 위치가 즉시 나오지 않는 경우가 있어 마지막 위치로 폴백한다
      let current = null;
      try {
        current = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
      } catch (error) {
        current = await Location.getLastKnownPositionAsync();
      }
      if (!current) {
        setStatus('error');
        return;
      }
      const coords = { lat: current.coords.latitude, lng: current.coords.longitude };
      const accuracyM = current.coords.accuracy != null ? Math.round(current.coords.accuracy) : null;
      const distanceM = landmarkCoords ? distanceInMeters(coords, landmarkCoords) : null;
      setPosition({ coords, accuracyM, distanceM });
      setStatus('ready');
      if (distanceM != null && distanceM > RADIUS_M) {
        setOutOfRange(distanceM - RADIUS_M);
      }
    } catch (error) {
      console.error('현재 위치를 확인하지 못했어요:', error);
      setStatus('error');
    }
    // 좌표는 랜드마크가 바뀔 때만 달라진다
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [landmark?.landmarkId]);

  useEffect(() => {
    locate();
  }, [locate]);

  useEffect(() => {
    if (status !== 'loading') return undefined;
    const loop = Animated.loop(
      Animated.sequence([
        Animated.timing(pulse, { toValue: 1, duration: 750, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
        Animated.timing(pulse, { toValue: 0.35, duration: 750, easing: Easing.inOut(Easing.ease), useNativeDriver: true }),
      ]),
    );
    loop.start();
    return () => loop.stop();
  }, [pulse, status]);

  const withinRange = status === 'ready' && !outOfRange;
  const distanceLabel = status === 'ready' ? formatDistance(position?.distanceM) : '--m';

  return (
    <View style={styles.container}>
      <Animated.View style={[styles.pulseRing, { opacity: status === 'loading' ? pulse : 1 }]}>
        <View style={[styles.pulseDot, withinRange && styles.pulseDotDone]}>
          <Ionicons name={withinRange ? 'checkmark' : 'location'} size={30} color="#FFFFFF" />
        </View>
      </Animated.View>

      <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
        {status === 'loading' && '현재 위치를 확인하고 있어요'}
        {status === 'ready' && '현재 위치 확인 완료'}
        {status === 'denied' && '위치 권한이 필요해요'}
        {status === 'error' && '현재 위치를 확인하지 못했어요'}
      </CustomText>
      <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.subtitle}>
        {landmark.landmarkName}
      </CustomText>

      {status === 'denied' && (
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.helpText}>
          방문 인증은 현재 위치로 확인해요. 설정에서 트립로그의 위치 권한을 허용한 뒤 다시 시도해 주세요.
        </CustomText>
      )}
      {status === 'error' && (
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.helpText}>
          GPS 신호가 약하거나 위치 서비스가 꺼져 있을 수 있어요. 잠시 후 다시 시도해 주세요.
        </CustomText>
      )}

      <View style={styles.infoCard}>
        <InfoRow label="랜드마크까지" value={distanceLabel} />
        <View style={styles.divider} />
        <InfoRow label="위치 정확도" value={position?.accuracyM != null ? `±${position.accuracyM}m` : '--m'} />
        <View style={styles.divider} />
        <InfoRow label="인증 반경" value={`${RADIUS_M}m`} />
      </View>
      {status === 'ready' && !landmarkCoords && (
        <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.helpText}>
          이 랜드마크는 아직 좌표 정보가 없어 거리 확인 없이 진행해요.
        </CustomText>
      )}

      <View style={styles.footer}>
        {withinRange && (
          <TouchableOpacity style={styles.primaryBtn} onPress={() => onVerified(position)} activeOpacity={0.9}>
            <CustomText variant="UI/Button" color="#FFFFFF" style={styles.bold}>
              기록 작성하기
            </CustomText>
          </TouchableOpacity>
        )}
        {status === 'denied' && (
          <TouchableOpacity style={styles.primaryBtn} onPress={() => Linking.openSettings()} activeOpacity={0.9}>
            <CustomText variant="UI/Button" color="#FFFFFF" style={styles.bold}>
              설정 열기
            </CustomText>
          </TouchableOpacity>
        )}
        {(status === 'denied' || status === 'error') && (
          <TouchableOpacity style={styles.ghostBtn} onPress={locate} activeOpacity={0.8}>
            <CustomText variant="UI/Button" color={theme.colors.textSecondary} style={styles.bold}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        )}
      </View>

      <OutOfRangeModal
        visible={outOfRange != null}
        radiusM={RADIUS_M}
        remainingM={outOfRange}
        onRetry={locate}
        onClose={() => setOutOfRange(null)}
      />
    </View>
  );
}

function InfoRow({ label, value }) {
  return (
    <View style={styles.infoRow}>
      <CustomText variant="Body/Small" color={theme.colors.textSecondary}>{label}</CustomText>
      <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
        {value}
      </CustomText>
    </View>
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
  pulseDotDone: {
    backgroundColor: theme.colors.primary,
  },
  title: {
    fontWeight: 'bold',
    textAlign: 'center',
  },
  subtitle: {
    marginTop: 6,
  },
  helpText: {
    marginTop: theme.spacing.md,
    textAlign: 'center',
  },
  bold: {
    fontWeight: 'bold',
  },
  infoCard: {
    alignSelf: 'stretch',
    marginTop: theme.spacing.xl,
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
  footer: {
    alignSelf: 'stretch',
    marginTop: 'auto',
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
