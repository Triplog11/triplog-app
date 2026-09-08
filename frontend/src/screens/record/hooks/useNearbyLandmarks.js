import { useState, useRef, useCallback } from 'react';
import * as Location from 'expo-location';
import { fetchNationwideMap, fetchRegionDetail } from '../../../api/regions';
import {
  matchRegionByPlace,
  sortLandmarksByProximity,
  resolveRegionName,
  formatPlaceLabel,
} from '../../../utils/geo';

export const NEARBY_STATUS = {
  IDLE: 'idle',
  SCANNING: 'scanning',
  READY: 'ready',
  EMPTY: 'empty',
  DENIED: 'denied',
  ERROR: 'error',
};

const INITIAL = {
  status: NEARBY_STATUS.IDLE,
  region: null,
  landmarks: [],
  placeLabel: null,
  errorMessage: '',
};

/**
 * 현재 위치가 속한 시·군·구를 찾아 인증 가능한 랜드마크 목록을 만든다.
 *
 * 좌표는 단말기 안에서만 쓰고 서버로 보내지 않는다(위치기반서비스 신고 면제의 근거).
 * 서버에는 지역 식별자만 담긴 조회 요청(fetchNationwideMap / fetchRegionDetail)만 나간다.
 */
export default function useNearbyLandmarks() {
  const [state, setState] = useState(INITIAL);
  // 재시도를 연달아 눌렀을 때 앞선 탐색의 결과가 뒤늦게 반영되지 않도록 실행 번호로 구분한다
  const runIdRef = useRef(0);

  const scan = useCallback(async () => {
    const runId = runIdRef.current + 1;
    runIdRef.current = runId;
    const apply = (next) => {
      if (runIdRef.current === runId) setState(next);
    };

    apply({ ...INITIAL, status: NEARBY_STATUS.SCANNING });
    try {
      const { status: permission } = await Location.requestForegroundPermissionsAsync();
      if (permission !== 'granted') {
        apply({ ...INITIAL, status: NEARBY_STATUS.DENIED });
        return;
      }
      const coords = await readCurrentCoords();
      if (!coords) {
        apply({ ...INITIAL, status: NEARBY_STATUS.ERROR });
        return;
      }
      const place = await readPlace(coords);
      const placeLabel = formatPlaceLabel(place, resolveRegionName(place));

      const { regions } = (await fetchNationwideMap()) ?? {};
      const region = matchRegionByPlace(place, regions);
      if (!region) {
        apply({ ...INITIAL, status: NEARBY_STATUS.EMPTY, placeLabel });
        return;
      }

      const landmarks = await readRegionLandmarks(region, coords);
      apply({
        ...INITIAL,
        status: landmarks.length > 0 ? NEARBY_STATUS.READY : NEARBY_STATUS.EMPTY,
        region,
        landmarks,
        placeLabel: placeLabel ?? region.regionName,
      });
    } catch (error) {
      console.error('주변 랜드마크를 찾지 못했습니다:', error);
      apply({
        ...INITIAL,
        status: NEARBY_STATUS.ERROR,
        errorMessage: error?.message ?? '',
      });
    }
  }, []);

  const reset = useCallback(() => {
    runIdRef.current += 1;
    setState(INITIAL);
  }, []);

  return { ...state, scan, reset };
}

/** 실내·에뮬레이터에서 현재 위치가 곧바로 나오지 않는 경우가 있어 마지막 위치로 폴백한다 */
async function readCurrentCoords() {
  let position = null;
  try {
    position = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
  } catch (error) {
    position = await Location.getLastKnownPositionAsync();
  }
  if (!position) return null;
  return { lat: position.coords.latitude, lng: position.coords.longitude };
}

/** 역지오코딩은 기기·권한 상태에 따라 실패할 수 있어, 실패해도 탐색 자체는 계속한다 */
async function readPlace(coords) {
  try {
    const places = await Location.reverseGeocodeAsync({ latitude: coords.lat, longitude: coords.lng });
    return places?.[0] ?? null;
  } catch (error) {
    console.error('현재 위치의 행정구역을 확인하지 못했습니다:', error);
    return null;
  }
}

/** 지역 상세의 랜드마크에 인증 전송에 필요한 지역 코드를 채워 넣는다 */
async function readRegionLandmarks(region, coords) {
  const detail = await fetchRegionDetail(region.regionId);
  const items = detail?.landmarks?.items ?? [];
  const enriched = items.map((item) => ({
    ...item,
    legalRegionCode: item.legalRegionCode ?? detail.legalRegionCode ?? region.legalRegionCode,
    legalDistrictCode: item.legalDistrictCode ?? detail.legalDistrictCode ?? region.legalDistrictCode,
    regionId: region.regionId,
    regionName: detail.regionName ?? region.regionName,
  }));
  return sortLandmarksByProximity(enriched, coords);
}
