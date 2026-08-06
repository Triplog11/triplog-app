import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { StyleSheet, View, TouchableOpacity, Modal, Pressable } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import * as Location from 'expo-location';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { GRADE_CONFIG } from '../../data/collection';
import { NEARBY_LANDMARKS, distanceInMeters, formatDistance } from '../../data/nearbyLandmarks';
import NearbyLandmarkList from './components/NearbyLandmarkList';
import VerifyProgress from './components/VerifyProgress';
import ReviewWrite from './components/ReviewWrite';
import VerifySuccess from './components/VerifySuccess';

const MODE = {
  LIST: 'list',
  DETAIL: 'detail',
  VERIFYING: 'verifying',
  REVIEW: 'review',
  SUCCESS: 'success',
};

/**
 * 방문 인증 — 목록 → 상세 → GPS 확인 → 후기 → 완료.
 * 위치는 실제 GPS를 쓰고, 인증 판정/보상은 백엔드 API 연동 전까지 로컬 처리한다.
 */
export default function VisitCertScreen({ navigation }) {
  const [mode, setMode] = useState(MODE.LIST);
  const [selected, setSelected] = useState(null);
  const [coords, setCoords] = useState(null);
  const [locationState, setLocationState] = useState('loading');
  const [visitedIds, setVisitedIds] = useState(() =>
    NEARBY_LANDMARKS.filter((l) => l.visited).map((l) => l.id),
  );
  const [outOfRange, setOutOfRange] = useState(null);

  const loadLocation = useCallback(async () => {
    setLocationState('loading');
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        setLocationState('denied');
        return;
      }
      // 실내·에뮬레이터에서 현재 위치가 즉시 나오지 않는 경우가 있어 마지막 위치로 폴백한다
      let position = null;
      try {
        position = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
      } catch (error) {
        position = await Location.getLastKnownPositionAsync();
      }
      if (!position) {
        setLocationState('error');
        return;
      }
      setCoords({ lat: position.coords.latitude, lng: position.coords.longitude });
      setLocationState('ready');
    } catch (error) {
      console.warn('현재 위치를 확인하지 못했어요:', error);
      setLocationState('error');
    }
  }, []);

  useEffect(() => {
    loadLocation();
  }, [loadLocation]);

  // 실제 좌표로 거리·인증 가능 여부를 계산한다
  const landmarks = useMemo(() => {
    const list = NEARBY_LANDMARKS.map((landmark) => {
      const distanceM = coords ? distanceInMeters(coords, landmark) : null;
      return {
        ...landmark,
        distanceM,
        visited: visitedIds.includes(landmark.id),
        canVerify: distanceM != null && distanceM <= landmark.radiusM,
      };
    });
    return list.sort((a, b) => (a.distanceM ?? Infinity) - (b.distanceM ?? Infinity));
  }, [coords, visitedIds]);

  const current = selected ? landmarks.find((l) => l.id === selected.id) ?? selected : null;

  const reset = () => {
    setMode(MODE.LIST);
    setSelected(null);
  };

  const handleStartVerify = () => {
    if (!current) return;
    if (!current.canVerify) {
      setOutOfRange(current);
      return;
    }
    setMode(MODE.VERIFYING);
  };

  const handleVerified = useCallback(() => {
    setVisitedIds((prev) => (current && !prev.includes(current.id) ? [...prev, current.id] : prev));
    setMode(MODE.REVIEW);
  }, [current]);

  if (mode === MODE.SUCCESS && current) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <VerifySuccess
          landmark={current}
          onGoCollection={() => {
            reset();
            navigation.navigate('Collection');
          }}
          onDone={reset}
        />
      </SafeAreaView>
    );
  }

  if (mode === MODE.REVIEW && current) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="방문 기록" onBack={() => setMode(MODE.SUCCESS)} />
        <ReviewWrite
          landmark={current}
          onSkip={() => setMode(MODE.SUCCESS)}
          onSubmit={() => setMode(MODE.SUCCESS)}
        />
      </SafeAreaView>
    );
  }

  if (mode === MODE.VERIFYING && current) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="위치 확인" onBack={() => setMode(MODE.DETAIL)} />
        <VerifyProgress landmark={current} distanceM={current.distanceM} onComplete={handleVerified} />
      </SafeAreaView>
    );
  }

  if (mode === MODE.DETAIL && current) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="랜드마크" onBack={reset} />
        <LandmarkDetail
          landmark={current}
          onVerify={handleStartVerify}
          outOfRange={outOfRange}
          onCloseOutOfRange={() => setOutOfRange(null)}
          onRetryLocation={() => {
            setOutOfRange(null);
            loadLocation();
          }}
        />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.listHeader}>
        <CustomText variant="Heading/H3" color={theme.colors.text} style={styles.bold}>
          방문 인증
        </CustomText>
      </View>
      <NearbyLandmarkList
        landmarks={landmarks}
        locationState={locationState}
        onRetryLocation={loadLocation}
        onSelect={(landmark) => {
          setSelected(landmark);
          setMode(MODE.DETAIL);
        }}
      />
    </SafeAreaView>
  );
}

function ScreenHeader({ title, onBack }) {
  return (
    <View style={styles.header}>
      <TouchableOpacity onPress={onBack} hitSlop={10} style={styles.backBtn}>
        <Ionicons name="chevron-back" size={22} color={theme.colors.text} />
      </TouchableOpacity>
      <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
        {title}
      </CustomText>
      <View style={styles.backBtn} />
    </View>
  );
}

/** 랜드마크 상세 — 인증 조건 안내 + 인증 CTA */
function LandmarkDetail({ landmark, onVerify, outOfRange, onCloseOutOfRange, onRetryLocation }) {
  const grade = GRADE_CONFIG[landmark.grade];
  const remaining =
    landmark.distanceM != null ? Math.max(landmark.distanceM - landmark.radiusM, 0) : null;

  return (
    <View style={styles.detailContainer}>
      <View style={[styles.detailHero, { backgroundColor: grade.soft }]}>
        <Ionicons name="location" size={40} color={grade.color} />
      </View>

      <View style={styles.detailBody}>
        <View style={styles.detailTitleRow}>
          <CustomText variant="Heading/H3" color={theme.colors.text} style={styles.bold}>
            {landmark.name}
          </CustomText>
          <View style={[styles.gradePill, { backgroundColor: grade.soft }]}>
            <CustomText variant="Caption" color={grade.color} style={styles.bold}>
              {landmark.grade}
            </CustomText>
          </View>
        </View>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
          {landmark.region}
        </CustomText>

        <View style={styles.infoCard}>
          <View style={styles.infoRow}>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>현재 거리</CustomText>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
              {formatDistance(landmark.distanceM)}
            </CustomText>
          </View>
          <View style={styles.divider} />
          <View style={styles.infoRow}>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>인증 반경</CustomText>
            <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold}>
              {landmark.radiusM}m
            </CustomText>
          </View>
          <View style={styles.divider} />
          <View style={styles.infoRow}>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary}>획득 보상</CustomText>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
              +{landmark.xp} XP · {landmark.point}점
            </CustomText>
          </View>
        </View>
      </View>

      <View style={styles.detailFooter}>
        {landmark.visited ? (
          <View style={styles.doneNotice}>
            <Ionicons name="checkmark-circle" size={18} color={theme.colors.success} />
            <CustomText variant="Body/Medium" color={theme.colors.success} style={styles.bold}>
              이미 인증한 랜드마크예요
            </CustomText>
          </View>
        ) : (
          <>
            {!landmark.canVerify && (
              <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.disabledHint}>
                랜드마크 {landmark.radiusM}m 안에서 인증할 수 있어요
                {remaining ? ` (${formatDistance(remaining)} 더 가까이)` : ''}
              </CustomText>
            )}
            <TouchableOpacity
              style={[styles.verifyBtn, !landmark.canVerify && styles.verifyBtnDisabled]}
              onPress={onVerify}
              disabled={!landmark.canVerify}
              activeOpacity={0.9}
            >
              <CustomText
                variant="UI/Button"
                color={landmark.canVerify ? '#FFFFFF' : theme.colors.textMuted}
                style={styles.bold}
              >
                인증하기
              </CustomText>
            </TouchableOpacity>
          </>
        )}
      </View>

      {/* 반경 이탈 — 토스트가 아닌 모달 (DESIGN.md §14) */}
      <Modal visible={!!outOfRange} transparent animationType="fade" onRequestClose={onCloseOutOfRange}>
        <Pressable style={styles.modalBackdrop} onPress={onCloseOutOfRange}>
          <Pressable style={styles.modalCard} onPress={() => {}}>
            <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.bold}>
              현재 위치가 랜드마크 반경 밖에 있어요
            </CustomText>
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.modalBody}>
              {landmark.radiusM}m 안으로 다가가서 다시 시도해 주세요.
              {remaining ? ` 지금은 ${formatDistance(remaining)} 더 가야 해요.` : ''}
            </CustomText>
            <View style={styles.modalActions}>
              <TouchableOpacity style={styles.modalGhostBtn} onPress={onCloseOutOfRange} activeOpacity={0.8}>
                <CustomText variant="UI/Button/Small" color={theme.colors.textSecondary} style={styles.bold}>
                  닫기
                </CustomText>
              </TouchableOpacity>
              <TouchableOpacity style={styles.modalPrimaryBtn} onPress={onRetryLocation} activeOpacity={0.9}>
                <CustomText variant="UI/Button/Small" color="#FFFFFF" style={styles.bold}>
                  다시 시도
                </CustomText>
              </TouchableOpacity>
            </View>
          </Pressable>
        </Pressable>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  bold: {
    fontWeight: 'bold',
  },
  listHeader: {
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.sm,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: theme.spacing.base,
    paddingVertical: theme.spacing.sm,
  },
  backBtn: {
    width: 32,
    height: 32,
    justifyContent: 'center',
  },
  // 상세
  detailContainer: {
    flex: 1,
  },
  detailHero: {
    height: 160,
    justifyContent: 'center',
    alignItems: 'center',
  },
  detailBody: {
    flex: 1,
    padding: theme.spacing.lg,
    gap: 4,
  },
  detailTitleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  gradePill: {
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 10,
    paddingVertical: 3,
  },
  infoCard: {
    marginTop: theme.spacing.base,
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
  detailFooter: {
    padding: theme.spacing.lg,
    // 탭바의 가운데 인증 플로팅 버튼과 겹치지 않도록 하단 여백 확보
    paddingBottom: 104,
    gap: theme.spacing.sm,
  },
  disabledHint: {
    textAlign: 'center',
  },
  verifyBtn: {
    height: 56,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  verifyBtnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
  },
  doneNotice: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 6,
    height: 56,
  },
  // 반경 이탈 모달
  modalBackdrop: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    paddingHorizontal: theme.spacing.xl,
  },
  modalCard: {
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    padding: theme.spacing.lg,
  },
  modalBody: {
    marginTop: theme.spacing.sm,
  },
  modalActions: {
    flexDirection: 'row',
    gap: theme.spacing.sm,
    marginTop: theme.spacing.lg,
  },
  modalGhostBtn: {
    flex: 1,
    height: 46,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalPrimaryBtn: {
    flex: 1,
    height: 46,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
