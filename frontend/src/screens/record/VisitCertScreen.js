import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, Linking } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchLandmarkDetail } from '../../api/landmarks';
import ScreenHeader from './components/ScreenHeader';
import StatusBlock from './components/StatusBlock';
import NearbyScan from './components/NearbyScan';
import NearbyLandmarkList from './components/NearbyLandmarkList';
import RegionSelectList from './components/RegionSelectList';
import LandmarkSelectList from './components/LandmarkSelectList';
import VerifyProgress from './components/VerifyProgress';
import ReviewWrite from './components/ReviewWrite';
import VerifySuccess from './components/VerifySuccess';
import useCertSubmit from './hooks/useCertSubmit';
import useNearbyLandmarks, { NEARBY_STATUS } from './hooks/useNearbyLandmarks';

const STEP = {
  NEARBY: 'nearby', // 1·2·5단계 — 현재 위치 탐색 / 랜드마크 선택 / 주변에 없음
  REGION: 'region', // 폴백 — 지역 직접 선택
  LANDMARK: 'landmark', // 폴백·딥링크 — 지역 안의 랜드마크 선택
  LOCATION: 'location', // 딥링크 — 랜드마크를 지정해 들어온 경우의 위치 확인
  REVIEW: 'review', // 3단계 — 기록 작성
  SUCCESS: 'success', // 4단계 — 완료
};

/** 어떤 경로로 랜드마크를 골랐는지 — 앞뒤 단계를 정하는 기준 */
const FLOW = { NEARBY: 'nearby', MANUAL: 'manual', DEEP_LINK: 'deepLink' };

/**
 * 방문 인증 — 현재 위치에서 인증할 수 있는 랜드마크를 찾아 주는 것이 기본 경로다.
 *
 *  기본 : 위치 탐색 → 랜드마크 선택 → 기록 작성 → 완료
 *  폴백 : 지역 직접 선택 → 랜드마크 선택 → 기록 작성 → 완료 (위치 권한을 거부해도 인증할 수 있다)
 *
 * 라우트 파라미터(다른 화면에서 진입 시 단계 건너뛰기):
 *  - {regionId, regionName?}  → 랜드마크 선택부터 시작
 *  - {landmarkId}             → 랜드마크 상세를 불러온 뒤 위치 확인부터 시작
 * 파라미터는 한 번 소비하면 비워서, 탭을 다시 눌렀을 때 재진입하지 않게 한다.
 */
export default function VisitCertScreen({ navigation, route }) {
  const [step, setStep] = useState(STEP.NEARBY);
  const [flow, setFlow] = useState(FLOW.NEARBY);
  const [region, setRegion] = useState(null);
  const [landmark, setLandmark] = useState(null);
  const [deepLink, setDeepLink] = useState({ status: 'idle', message: '' });
  const { submit, submitting, errorMessage, result, reset: resetSubmit } = useCertSubmit();
  const nearby = useNearbyLandmarks();
  const { scan: scanNearby } = nearby;

  const params = route?.params ?? {};
  const paramRegionId = params.regionId;
  const paramRegionName = params.regionName;
  const paramLandmarkId = params.landmarkId;

  const loadLandmarkFromParam = useCallback(async (landmarkId) => {
    setDeepLink({ status: 'loading', message: '' });
    try {
      const detail = await fetchLandmarkDetail(landmarkId);
      setRegion({ regionId: detail.regionId, regionName: detail.regionName });
      setLandmark(detail);
      setStep(STEP.LOCATION);
      setDeepLink({ status: 'idle', message: '' });
    } catch (error) {
      console.error('랜드마크 정보를 불러오지 못했어요:', error);
      setDeepLink({ status: 'error', message: error?.message ?? '랜드마크 정보를 불러오지 못했어요.' });
    }
  }, []);

  useEffect(() => {
    if (paramLandmarkId == null && paramRegionId == null) return;
    resetSubmit();
    setFlow(FLOW.DEEP_LINK);
    if (paramLandmarkId != null) {
      loadLandmarkFromParam(paramLandmarkId);
    } else {
      setRegion({ regionId: paramRegionId, regionName: paramRegionName ?? '' });
      setLandmark(null);
      setStep(STEP.LANDMARK);
    }
    navigation.setParams({ regionId: undefined, regionName: undefined, landmarkId: undefined });
  }, [paramLandmarkId, paramRegionId, paramRegionName, navigation, loadLandmarkFromParam, resetSubmit]);

  useEffect(() => {
    // 딥링크로 들어온 경우가 아니면, 화면에 들어서자마자 주변 랜드마크를 찾는다
    if (paramLandmarkId != null || paramRegionId != null) return;
    scanNearby();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const restart = useCallback(() => {
    resetSubmit();
    setRegion(null);
    setLandmark(null);
    setFlow(FLOW.NEARBY);
    setStep(STEP.NEARBY);
    if (nearby.status === NEARBY_STATUS.IDLE) scanNearby();
  }, [resetSubmit, scanNearby, nearby.status]);

  const rescan = useCallback(() => {
    resetSubmit();
    setRegion(null);
    setLandmark(null);
    setFlow(FLOW.NEARBY);
    setStep(STEP.NEARBY);
    scanNearby();
  }, [resetSubmit, scanNearby]);

  const startManualSelect = useCallback(() => {
    resetSubmit();
    setRegion(null);
    setLandmark(null);
    setFlow(FLOW.MANUAL);
    setStep(STEP.REGION);
  }, [resetSubmit]);

  const goBackFromScan = useCallback(() => {
    if (navigation.canGoBack()) navigation.goBack();
    else navigation.navigate('Home');
  }, [navigation]);

  /** 기본·폴백 경로는 곧바로 기록 작성으로 합류하고, 딥링크만 위치 확인을 한 번 더 거친다 */
  const pickLandmark = useCallback(
    (item, pickedRegion) => {
      resetSubmit();
      if (pickedRegion) setRegion(pickedRegion);
      setLandmark(item);
      setStep(flow === FLOW.DEEP_LINK ? STEP.LOCATION : STEP.REVIEW);
    },
    [flow, resetSubmit],
  );

  const reviewBackStep = () => {
    if (flow === FLOW.DEEP_LINK) return STEP.LOCATION;
    return flow === FLOW.MANUAL ? STEP.LANDMARK : STEP.NEARBY;
  };

  const handleSubmitReview = async (review) => {
    const response = await submit(landmark, review);
    if (response) setStep(STEP.SUCCESS);
  };

  if (deepLink.status !== 'idle') {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="방문 인증" onBack={restart} />
        {deepLink.status === 'loading' ? (
          <StatusBlock loading />
        ) : (
          <StatusBlock message={deepLink.message} actionLabel="처음부터 다시 하기" onAction={restart} />
        )}
      </SafeAreaView>
    );
  }

  if (step === STEP.SUCCESS && landmark) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <VerifySuccess
          landmark={landmark}
          result={result}
          onGoCollection={() => {
            restart();
            navigation.navigate('Collection', {
              screen: 'RegionCollection',
              params: { regionId: region?.regionId, regionName: region?.regionName },
            });
          }}
          onGoHome={() => {
            restart();
            navigation.navigate('Home');
          }}
        />
      </SafeAreaView>
    );
  }

  if (step === STEP.REVIEW && landmark) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="기록 작성" onBack={submitting ? undefined : () => setStep(reviewBackStep())} />
        <ReviewWrite
          landmark={landmark}
          submitting={submitting}
          errorMessage={errorMessage}
          onSubmit={handleSubmitReview}
        />
      </SafeAreaView>
    );
  }

  if (step === STEP.LOCATION && landmark) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="위치 확인" onBack={() => setStep(region ? STEP.LANDMARK : STEP.NEARBY)} />
        <VerifyProgress landmark={landmark} onVerified={() => setStep(STEP.REVIEW)} />
      </SafeAreaView>
    );
  }

  if (step === STEP.LANDMARK && region) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title={region.regionName || '랜드마크 선택'} onBack={startManualSelect} />
        <LandmarkSelectList region={region} onSelect={(item) => pickLandmark(item)} />
      </SafeAreaView>
    );
  }

  if (step === STEP.REGION) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="방문 인증" onBack={restart} />
        <View style={styles.listHeader}>
          <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.bold}>
            인증할 지역을 선택해 주십시오
          </CustomText>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
            현재 위치를 쓰지 않고 지역을 직접 골라 인증합니다.
          </CustomText>
        </View>
        <RegionSelectList
          onSelect={(item) => {
            setRegion({ regionId: item.regionId, regionName: item.regionName });
            setStep(STEP.LANDMARK);
          }}
        />
      </SafeAreaView>
    );
  }

  if (nearby.status === NEARBY_STATUS.READY) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title="방문 인증" />
        <NearbyLandmarkList
          landmarks={nearby.landmarks}
          placeLabel={nearby.placeLabel}
          onSelect={(item) => pickLandmark(item, nearby.region)}
          onRetry={rescan}
        />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScreenHeader title="방문 인증" />
      <NearbyScan
        status={nearby.status === NEARBY_STATUS.IDLE ? NEARBY_STATUS.SCANNING : nearby.status}
        placeLabel={nearby.placeLabel}
        errorMessage={nearby.errorMessage}
        onRetry={rescan}
        onManualSelect={startManualSelect}
        onBack={goBackFromScan}
        onOpenSettings={() => Linking.openSettings()}
      />
    </SafeAreaView>
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
    gap: 4,
  },
});
