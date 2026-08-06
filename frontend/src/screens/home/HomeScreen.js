import React, { useState, useEffect, useRef, useCallback } from 'react';
import { StyleSheet, View, Text, StatusBar, Platform } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Feather } from '@expo/vector-icons';
import * as Location from 'expo-location';
import KoreaMap from '../../components/map/KoreaMap';
import Fab from '../../components/common/Fab';
import LocationPermissionModal from './components/LocationPermissionModal';
import MissionStrip from './components/MissionStrip';
import theme from '../../theme/theme';
import { REGIONS, getNationalStats } from '../../data/regions';
import { fetchNationwideMap } from '../../api/regions';
import { resolveRegionName, formatPlaceLabel } from '../../utils/geo';

/**
 * 홈 = 전국 지도
 * - 첫 진입: 한국어 사전 안내 모달 → 시스템 위치 권한 → 실제 위경도 투영 마커 + 상세 위치 칩
 * - 나침반 버튼: 탭하면 기기 방향(폰 y축 위쪽)에 맞춰 지도 회전, 다시 탭하면 북쪽 복귀
 */
export default function HomeScreen({ navigation }) {
  const fallbackStats = getNationalStats();
  const [mapStats, setMapStats] = useState(null);
  const mapRef = useRef(null);
  const headingSub = useRef(null);

  // 헤더 통계(전국 달성률/방문 지역 수)를 실 API로 채운다. 실패 시 목데이터로 폴백.
  useEffect(() => {
    let mounted = true;
    fetchNationwideMap()
      .then((result) => mounted && setMapStats(result ?? null))
      .catch((error) => console.warn('전국 지도 요약 조회 실패:', error?.status, error?.message));
    return () => {
      mounted = false;
    };
  }, []);

  // overallCompletionRate가 0~1 비율인지 0~100 퍼센트인지 불확실 → 방어적으로 정규화
  const rawRate = mapStats?.overallCompletionRate;
  const percent =
    rawRate != null ? Math.round(rawRate <= 1 ? rawRate * 100 : rawRate) : fallbackStats.percent;
  const collected =
    mapStats?.visitedRegionCount != null ? mapStats.visitedRegionCount : fallbackStats.collected;

  const [userRegion, setUserRegion] = useState(null);
  const [userCoords, setUserCoords] = useState(null);
  const [placeLabel, setPlaceLabel] = useState(null);
  const [permissionGranted, setPermissionGranted] = useState(false);
  const [showPermissionModal, setShowPermissionModal] = useState(false);
  const [compassOn, setCompassOn] = useState(false);
  // 광역 드릴다운 중에는 나침반 FAB가 하단 지역 카드와 겹치므로 숨긴다
  const [inProvinceView, setInProvinceView] = useState(false);

  const stopCompass = useCallback(() => {
    headingSub.current?.remove();
    headingSub.current = null;
  }, []);

  useEffect(() => () => stopCompass(), [stopCompass]);

  const locate = useCallback(async () => {
    try {
      const pos = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.Balanced,
      });
      const coords = {
        latitude: pos.coords.latitude,
        longitude: pos.coords.longitude,
      };
      setUserCoords(coords);

      // 웹은 reverseGeocode 미지원 — 좌표 마커만 표시
      if (Platform.OS === 'web') return coords;

      const places = await Location.reverseGeocodeAsync(coords);
      const place = places?.[0];
      const region = resolveRegionName(place);
      if (region) setUserRegion(region);
      const label = formatPlaceLabel(place, region);
      if (label) setPlaceLabel(label);
      return coords;
    } catch (error) {
      console.warn('현재 위치를 확인하지 못했어요:', error);
      return null;
    }
  }, []);

  // 첫 진입: 권한 상태 확인 → 미허용이면 한국어 안내 모달부터
  useEffect(() => {
    (async () => {
      const { status, canAskAgain } = await Location.getForegroundPermissionsAsync();
      if (status === 'granted') {
        setPermissionGranted(true);
        locate();
      } else if (canAskAgain) {
        setShowPermissionModal(true);
      }
    })();
  }, [locate]);

  const handleAllowPermission = async () => {
    setShowPermissionModal(false);
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status === 'granted') {
      setPermissionGranted(true);
      locate();
    }
  };

  const startCompass = async () => {
    if (headingSub.current) return;
    headingSub.current = await Location.watchHeadingAsync((h) => {
      const deg = h.trueHeading >= 0 ? h.trueHeading : h.magHeading;
      mapRef.current?.setHeading(deg);
    });
  };

  // 나침반 토글: 켜면 폰이 향한 방향이 지도 위쪽이 되도록 회전, 끄면 북쪽 복귀
  const handleCompassPress = async () => {
    if (!permissionGranted) {
      setShowPermissionModal(true);
      return;
    }

    if (!compassOn) {
      if (!userCoords) await locate();
      mapRef.current?.enterCompass(); // 내 위치를 화면 중앙으로 + 줌인
      await startCompass();
      setCompassOn(true);
    } else {
      stopCompass();
      mapRef.current?.resetHeading();
      setCompassOn(false);
    }
  };

  // 나침반 모드 중 지도를 만지면 즉시 해제 (회전 상태는 유지 — 더블탭으로 북쪽 복귀)
  const handleMapGesture = useCallback(() => {
    if (headingSub.current) {
      stopCompass();
      setCompassOn(false);
    }
  }, [stopCompass]);

  const handleExplore = (regionName) => {
    navigation.navigate('RegionDetail', { regionName });
  };

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <StatusBar barStyle="dark-content" backgroundColor={theme.colors.surface} />

      <View style={styles.head}>
        <Text style={styles.title}>어디로 떠나볼까요</Text>
        <View style={styles.statsRow}>
          <Text style={styles.statText}>
            전국 <Text style={styles.statStrong}>{percent}%</Text>
          </Text>
          <Text style={styles.statText}>
            방문 <Text style={styles.statStrong}>{collected}</Text>곳
          </Text>
          {placeLabel && (
            <View style={styles.locationChip}>
              <Feather name="map-pin" size={12} color={theme.colors.locationBlue} />
              <Text style={styles.locationChipText} numberOfLines={1}>
                {placeLabel}
              </Text>
            </View>
          )}
        </View>
      </View>

      <MissionStrip />

      <KoreaMap
        ref={mapRef}
        regions={REGIONS}
        onExplore={handleExplore}
        userRegion={userRegion}
        userCoords={userCoords}
        compassActive={compassOn}
        onUserGesture={handleMapGesture}
        onProvinceChange={(name) => setInProvinceView(!!name)}
      />

      {!inProvinceView && <Fab
        icon={compassOn ? 'navigation' : 'compass'}
        accessibilityLabel={compassOn ? '나침반 모드 끄기' : '내 방향으로 지도 회전'}
        onPress={handleCompassPress}
        style={styles.fab}
        active={compassOn}
      />}

      <LocationPermissionModal
        visible={showPermissionModal}
        onAllow={handleAllowPermission}
        onLater={() => setShowPermissionModal(false)}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  head: {
    paddingTop: theme.spacing.md,
    paddingHorizontal: theme.spacing.base,
    paddingBottom: theme.spacing.sm,
    alignItems: 'center',
    justifyContent: 'center',
  },
  title: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.heading,
    fontWeight: '700',
    lineHeight: Math.round(theme.typography.size.heading * 1.4),
    color: theme.colors.text,
    marginBottom: theme.spacing.xs,
  },
  statsRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
    maxWidth: '100%',
    paddingHorizontal: 8,
  },
  statText: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.body,
    color: theme.colors.textSecondary,
    fontWeight: '500',
  },
  statStrong: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.title,
    fontWeight: '700',
    color: theme.colors.primary,
  },
  locationChip: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    paddingVertical: 4,
    paddingHorizontal: 10,
    backgroundColor: theme.colors.white,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.rounded.full,
    flexShrink: 1,
  },
  locationChipText: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.caption,
    fontWeight: '600',
    color: theme.colors.textBody,
  },
  fab: {
    position: 'absolute',
    right: 16,
    bottom: 20,
  },
});
