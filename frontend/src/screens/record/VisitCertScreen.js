import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchLandmarkDetail } from '../../api/landmarks';
import ScreenHeader from './components/ScreenHeader';
import StatusBlock from './components/StatusBlock';
import RegionSelectList from './components/RegionSelectList';
import LandmarkSelectList from './components/LandmarkSelectList';
import VerifyProgress from './components/VerifyProgress';
import ReviewWrite from './components/ReviewWrite';
import VerifySuccess from './components/VerifySuccess';
import useCertSubmit from './hooks/useCertSubmit';

const STEP = {
  REGION: 'region',
  LANDMARK: 'landmark',
  LOCATION: 'location',
  REVIEW: 'review',
  SUCCESS: 'success',
};

/**
 * 방문 인증 — 지역 선택 → 랜드마크 선택 → 위치 확인 → 기록 작성 → 완료.
 *
 * 라우트 파라미터(다른 화면에서 진입 시 단계 건너뛰기):
 *  - {regionId, regionName?}  → 랜드마크 선택부터 시작
 *  - {landmarkId}             → 랜드마크 상세를 불러온 뒤 위치 확인부터 시작
 * 파라미터는 한 번 소비하면 비워서, 탭을 다시 눌렀을 때 재진입하지 않게 한다.
 */
export default function VisitCertScreen({ navigation, route }) {
  const [step, setStep] = useState(STEP.REGION);
  const [region, setRegion] = useState(null);
  const [landmark, setLandmark] = useState(null);
  const [deepLink, setDeepLink] = useState({ status: 'idle', message: '' });
  const { submit, submitting, errorMessage, result, reset: resetSubmit } = useCertSubmit();

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
    if (paramLandmarkId != null) {
      loadLandmarkFromParam(paramLandmarkId);
    } else {
      setRegion({ regionId: paramRegionId, regionName: paramRegionName ?? '' });
      setLandmark(null);
      setStep(STEP.LANDMARK);
    }
    navigation.setParams({ regionId: undefined, regionName: undefined, landmarkId: undefined });
  }, [paramLandmarkId, paramRegionId, paramRegionName, navigation, loadLandmarkFromParam, resetSubmit]);

  const restart = useCallback(() => {
    resetSubmit();
    setRegion(null);
    setLandmark(null);
    setStep(STEP.REGION);
  }, [resetSubmit]);

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
          <StatusBlock message={deepLink.message} actionLabel="지역부터 고르기" onAction={restart} />
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
        <ScreenHeader title="기록 작성" onBack={submitting ? undefined : () => setStep(STEP.LOCATION)} />
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
        <ScreenHeader title="위치 확인" onBack={() => setStep(STEP.LANDMARK)} />
        <VerifyProgress landmark={landmark} onVerified={() => setStep(STEP.REVIEW)} />
      </SafeAreaView>
    );
  }

  if (step === STEP.LANDMARK && region) {
    return (
      <SafeAreaView style={styles.container} edges={['top']}>
        <ScreenHeader title={region.regionName || '랜드마크 선택'} onBack={restart} />
        <LandmarkSelectList
          region={region}
          onSelect={(item) => {
            resetSubmit();
            setLandmark(item);
            setStep(STEP.LOCATION);
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
        <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
          인증할 지역을 먼저 선택해 주십시오.
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
