import React, {
  useState,
  useMemo,
  useRef,
  useEffect,
  forwardRef,
  useImperativeHandle,
} from 'react';
import { StyleSheet, View, Text, ActivityIndicator } from 'react-native';
import Svg, { Path, Circle, Rect, Text as SvgText } from 'react-native-svg';
import { Feather } from '@expo/vector-icons';
import { Gesture, GestureDetector } from 'react-native-gesture-handler';
import Animated, {
  FadeIn,
  useSharedValue,
  useAnimatedStyle,
  useAnimatedProps,
  withTiming,
  withRepeat,
  runOnJS,
  Easing,
} from 'react-native-reanimated';
import PressableScale from '../common/PressableScale';
import theme from '../../theme/theme';
import { MAP_PATHS } from './mapPaths';
import { KOREA_PROVINCES } from './koreaProvinces';
import { NEIGHBOR_COUNTRIES } from './neighborCountries';
import { getPathBounds } from './svgBounds';
import { projectToNationalSvg } from './geoProjection';

const AnimatedCircle = Animated.createAnimatedComponent(Circle);
const EASE_STANDARD = Easing.bezier(...theme.motion.easeStandard);

const MIN_SCALE = 0.45; // 축소하면 주변국까지 보이는 광역 뷰
const MAX_SCALE = 4;
const COMPASS_SCALE = 1.6; // 나침반 모드 기본 배율 (내 위치 중심)
const TRANSITION_MS = 620;
const BEAM_SIZE = 132; // 방향 빔 오버레이 한 변 (마커 중심 기준)

// 전국 지도 viewBox 파싱 (실측 17개 시·도)
const [, , VB_W, VB_H] = KOREA_PROVINCES.viewBox.split(' ').map(Number);

// 배경(바다 + 주변국) 레이어의 확장 viewBox — 남한 지도와 같은 좌표계
const BG_VB = { x: -600, y: -500, w: VB_W + 1200, h: VB_H + 850 };

const SHORT_NAMES = {
  서울특별시: '서울',
  인천광역시: '인천',
  경기도: '경기',
  강원특별자치도: '강원',
  충청북도: '충북',
  충청남도: '충남',
  대전광역시: '대전',
  세종특별자치시: '세종',
  전라북도: '전북',
  전라남도: '전남',
  광주광역시: '광주',
  경상북도: '경북',
  대구광역시: '대구',
  경상남도: '경남',
  울산광역시: '울산',
  부산광역시: '부산',
  제주특별자치도: '제주',
};

// 지역명 라벨 위치(도형 bbox 중심) — 큰 도는 크게, 광역시는 작게(확대 시 읽힘)
const REGION_LABELS = KOREA_PROVINCES.regions.map((region) => {
  const b = getPathBounds(region.d);
  const area = (b.maxX - b.minX) * (b.maxY - b.minY);
  return {
    name: region.name,
    label: SHORT_NAMES[region.name] || region.name,
    x: b.cx,
    y: b.cy,
    fontSize: area > 3000 ? 17 : 9,
  };
});

/** 17개 시·도 지역별 컬러 — visited는 진한 톤, base는 미방문 연한 톤 */
const REGION_COLORS = {
  서울특별시: { visited: '#FF6B81', base: '#FFD6DC' },
  인천광역시: { visited: '#4FC3F7', base: '#D3EFFC' },
  경기도: { visited: '#F48FB1', base: '#FBDCE7' },
  강원특별자치도: { visited: '#9CCC65', base: '#E2F0D3' },
  충청북도: { visited: '#FFC94D', base: '#FFEFC9' },
  충청남도: { visited: '#FF8FA3', base: '#FFDCE3' },
  대전광역시: { visited: '#FFAB40', base: '#FFE7C7' },
  세종특별자치시: { visited: '#81C784', base: '#DFF2E0' },
  전라북도: { visited: '#FFD166', base: '#FFF0CC' },
  전라남도: { visited: '#FFB37D', base: '#FFE3CC' },
  광주광역시: { visited: '#BA68C8', base: '#EFDFF4' },
  경상북도: { visited: '#B39DDB', base: '#E8E1F5' },
  대구광역시: { visited: '#FF8A65', base: '#FFE0D6' },
  경상남도: { visited: '#7FB8F0', base: '#D9E9FA' },
  울산광역시: { visited: '#FFCA28', base: '#FFF1C6' },
  부산광역시: { visited: '#EF5350', base: '#FBD9D8' },
  제주특별자치도: { visited: '#FF9E6D', base: '#FFE3D4' },
};

/** 상세 지도(mapPaths) 키 매핑 */
const PROVINCE_MAP_KEYS = {
  서울특별시: '서울특별시',
  부산광역시: '부산광역시',
  인천광역시: '인천광역시',
  대구광역시: '대구광역시',
  광주광역시: '광주광역시',
  대전광역시: '대전광역시',
  울산광역시: '울산광역시',
  세종특별자치시: '세종특별자치시',
  강원특별자치도: '강원도',
  강원도: '강원도',
  충청북도: '충청북도',
  충청남도: '충청남도',
  전라북도: '전라북도',
  전라남도: '전라남도',
  경상북도: '경상북도',
  경상남도: '경상남도',
  제주특별자치도: '제주특별자치도',
  경기도: 'gyeonggi',
};

function isVisited(regions, regionName) {
  const region = regions.find((r) => r.name === regionName);
  return region ? region.collected > 0 : false;
}

/** 현재 위치 마커 — 파란 점 + 1.5초 주기로 은은히 맥동하는 헤일로 */
function UserLocationMarker({ cx, cy, unit }) {
  const pulse = useSharedValue(0);

  useEffect(() => {
    pulse.value = withRepeat(withTiming(1, { duration: 1500 }), -1, true);
  }, [pulse]);

  const haloProps = useAnimatedProps(() => ({
    opacity: 0.35 - pulse.value * 0.2,
    r: unit * 2.2 + pulse.value * unit * 0.8,
  }));

  return (
    <>
      <AnimatedCircle animatedProps={haloProps} cx={cx} cy={cy} fill={theme.colors.locationBlue} />
      <Circle
        cx={cx}
        cy={cy}
        r={unit}
        fill={theme.colors.locationBlue}
        stroke={theme.colors.white}
        strokeWidth={unit * 0.4}
      />
    </>
  );
}

/** 전국 지도 — 17개 시·도 실측 지형, 지역별 색상, 탭 시 상세 지도로 드릴다운 */
function NationalMap({ regions, onSelectProvince, userCenter, highlightRegion }) {
  return (
    <Svg
      width="100%"
      height="100%"
      viewBox={KOREA_PROVINCES.viewBox}
      preserveAspectRatio="xMidYMid meet"
    >
      {KOREA_PROVINCES.regions.map((region) => {
        const colors = REGION_COLORS[region.name] || {
          visited: theme.colors.mapLandActive,
          base: theme.colors.mapLand,
        };
        const visited = isVisited(regions, region.name);
        const highlighted = highlightRegion === region.name;
        return (
          <Path
            key={region.name}
            d={region.d}
            fill={visited ? colors.visited : colors.base}
            fillRule="evenodd"
            stroke={highlighted ? theme.colors.primary : theme.colors.white}
            strokeWidth={highlighted ? 3 : 1.2}
            opacity={highlightRegion && !highlighted ? 0.45 : 1}
            onPress={() => onSelectProvince(region.name)}
          />
        );
      })}
      {REGION_LABELS.map((label) => (
        <SvgText
          key={`label-${label.name}`}
          x={label.x}
          y={label.y}
          fontSize={label.fontSize}
          fontWeight="700"
          fill={theme.colors.textBody}
          opacity={0.75}
          textAnchor="middle"
          pointerEvents="none"
        >
          {label.label}
        </SvgText>
      ))}
      {userCenter && <UserLocationMarker cx={userCenter.cx} cy={userCenter.cy} unit={9} />}
    </Svg>
  );
}

/** 바다 + 주변국(클릭 불가 장식) 배경 — 전국 지도와 동일 좌표계의 확장 영역 */
function SeaBackground({ stageSize }) {
  if (!stageSize.w || !stageSize.h) return null;
  const s0 = Math.min(stageSize.w / VB_W, stageSize.h / VB_H);
  const ox = (stageSize.w - VB_W * s0) / 2;
  const oy = (stageSize.h - VB_H * s0) / 2;

  return (
    <View
      pointerEvents="none"
      style={{
        position: 'absolute',
        left: ox + BG_VB.x * s0,
        top: oy + BG_VB.y * s0,
        width: BG_VB.w * s0,
        height: BG_VB.h * s0,
      }}
    >
      <Svg
        width="100%"
        height="100%"
        viewBox={`${BG_VB.x} ${BG_VB.y} ${BG_VB.w} ${BG_VB.h}`}
        preserveAspectRatio="xMidYMid meet"
      >
        <Rect
          x={BG_VB.x}
          y={BG_VB.y}
          width={BG_VB.w}
          height={BG_VB.h}
          fill={theme.colors.mapSea}
        />
        {NEIGHBOR_COUNTRIES.map((country) => (
          <Path
            key={country.name}
            d={country.d}
            fill={theme.colors.mapNeighbor}
            stroke={theme.colors.white}
            strokeWidth={1.5}
          />
        ))}
      </Svg>
    </View>
  );
}

/** 시·도 상세 지도 — 해당 지역의 팔레트 색으로 채움/라인 렌더 */
function ProvinceMap({ regionName }) {
  const key = PROVINCE_MAP_KEYS[regionName];
  const mapData = key ? MAP_PATHS[key] : null;
  const colors = REGION_COLORS[regionName] || {
    visited: theme.colors.mapLandActive,
    base: theme.colors.mapLand,
  };

  if (!mapData) {
    return <Text style={styles.emptyText}>지도를 준비 중이에요.</Text>;
  }

  return (
    <Svg width="100%" height="100%" viewBox={mapData.viewBox} preserveAspectRatio="xMidYMid meet">
      {mapData.shapes.map((shape, index) => (
        <Path
          key={`p-${index}`}
          d={shape.d}
          fill={shape.outline ? 'none' : colors.base}
          stroke={shape.outline ? colors.visited : theme.colors.white}
          strokeWidth={shape.outline ? 1.4 : 1}
        />
      ))}
    </Svg>
  );
}

/** 상세 뷰 하단 정보 카드 — 지역명 + 시·군·구 방문 진행률 + 탐험 CTA (regions: {name, collected, total}) */
function ProvinceInfoCard({ regionName, regions, onExplore }) {
  const region = regions.find((r) => r.name === regionName);
  const collected = region?.collected ?? 0;
  const total = region?.total ?? 0;
  const pct = total > 0 ? Math.min(100, Math.round((collected / total) * 100)) : 0;
  const accent = REGION_COLORS[regionName]?.visited || theme.colors.primary;

  return (
    <Animated.View entering={FadeIn.duration(theme.motion.standard)} style={styles.infoCard}>
      <View style={styles.infoCardTop}>
        <View style={styles.infoCardTitleCol}>
          <Text style={styles.infoCardName}>{regionName}</Text>
          <Text style={styles.infoCardMeta}>
            시·군·구 {collected}/{total}곳 방문 · {pct}%
          </Text>
        </View>
        <View style={[styles.infoCardBadge, { backgroundColor: accent }]}>
          <Text style={styles.infoCardBadgeText}>{SHORT_NAMES[regionName] || regionName}</Text>
        </View>
      </View>
      <View style={styles.progressTrack}>
        <View style={[styles.progressFill, { width: `${pct}%` }]} />
      </View>
      <PressableScale
        style={styles.exploreBtn}
        onPress={onExplore}
        accessibilityLabel={`${regionName} 탐험하기`}
      >
        <Feather name="map" size={16} color={theme.colors.white} />
        <Text style={styles.exploreBtnText}>탐험하기</Text>
      </PressableScale>
    </Animated.View>
  );
}

/** 상세 모드 상단 툴바 — ← 전국 지도 (지역 정보는 하단 카드가 담당) */
function MapToolbar({ onBack }) {
  return (
    <Animated.View entering={FadeIn.duration(theme.motion.standard)} style={styles.toolbar}>
      <PressableScale style={styles.backPill} onPress={onBack} accessibilityLabel="전국 지도로">
        <Feather name="chevron-left" size={16} color={theme.colors.textSecondary} />
        <Text style={styles.backPillText}>전국 지도</Text>
      </PressableScale>
    </Animated.View>
  );
}

/**
 * 전국 ↔ 시·도 드릴다운 지도 (17개 시·도 전 지역)
 * - 핀치 줌(0.6~4x) + 팬 + 두 손가락 회전, 더블탭으로 초기화
 * - 지역 탭 → 하이라이트 + "OO로 이동 중..." 오버레이 → 크로스페이드 드릴다운
 * - 나침반 모드: 지도 회전 + 마커 위 방향 빔이 함께 회전 (View 오버레이라 항상 헤딩 추종)
 * - ref: showProvince / showNational / setHeading / resetHeading
 */
const KoreaMap = forwardRef(function KoreaMap(
  { regions, onExplore, userRegion, userCoords, compassActive, onUserGesture, onProvinceChange },
  ref
) {
  const [province, setProvince] = useState(null);
  const [transitionTo, setTransitionTo] = useState(null);
  const [stageSize, setStageSize] = useState({ w: 0, h: 0 });
  const transitionTimer = useRef(null);
  const lastHeading = useRef(0);
  const headingShared = useSharedValue(0); // 방향 빔 각도 (연속값, 도 단위)

  const scale = useSharedValue(1);
  const savedScale = useSharedValue(1);
  const tx = useSharedValue(0);
  const ty = useSharedValue(0);
  const savedTx = useSharedValue(0);
  const savedTy = useSharedValue(0);
  const rotation = useSharedValue(0); // 라디안
  const savedRotation = useSharedValue(0);
  const stageW = useSharedValue(0);
  const stageH = useSharedValue(0);
  // 배경(바다) 레이어의 스테이지 좌표 경계 — 팬/줌 한계 계산용
  const minScale = useSharedValue(MIN_SCALE);
  const bgL = useSharedValue(0);
  const bgR = useSharedValue(0);
  const bgT = useSharedValue(0);
  const bgB = useSharedValue(0);

  useEffect(() => () => clearTimeout(transitionTimer.current), []);

  // 현재 위치: 위경도 투영 우선, 없으면 사용자의 시·도 도형 중심 폴백
  const userCenter = useMemo(() => {
    if (userCoords) {
      const projected = projectToNationalSvg(userCoords);
      if (projected) return projected;
    }
    if (!userRegion) return null;
    const region = KOREA_PROVINCES.regions.find((r) => r.name === userRegion);
    if (!region) return null;
    const b = getPathBounds(region.d);
    return { cx: b.cx, cy: b.cy };
  }, [userCoords, userRegion]);

  // 마커의 스테이지(뷰) 좌표 — 방향 빔 배치 + 나침반 중심 고정용 (aspect-fit 보정)
  const markerStagePos = useMemo(() => {
    if (!userCenter || !stageSize.w || !stageSize.h) return null;
    const s = Math.min(stageSize.w / VB_W, stageSize.h / VB_H);
    return {
      x: (stageSize.w - VB_W * s) / 2 + userCenter.cx * s,
      y: (stageSize.h - VB_H * s) / 2 + userCenter.cy * s,
    };
  }, [userCenter, stageSize]);

  // 스테이지 중심 기준 마커 오프셋 (나침반 회전 피벗 계산에 사용)
  const markerOffset = useRef(null);
  useEffect(() => {
    markerOffset.current =
      markerStagePos && stageSize.w
        ? { mx: markerStagePos.x - stageSize.w / 2, my: markerStagePos.y - stageSize.h / 2 }
        : null;
  }, [markerStagePos, stageSize]);

  /**
   * 나침반 모드: 회전 각도에 맞춰 내 위치가 항상 화면 중앙에 오도록 이동량 계산.
   * RN transform(translate→rotate→scale, 뷰 중심 기준)에서 마커 m이 중앙에 오려면
   * T = -R(θ)·S·m 이어야 하므로 헤딩이 바뀔 때마다 T를 갱신한다.
   */
  const applyCompassTransform = (rotRad, duration = 150, easing = Easing.linear) => {
    const m = markerOffset.current;
    if (!m) return;
    const s = COMPASS_SCALE;
    const targetX = -(Math.cos(rotRad) * m.mx - Math.sin(rotRad) * m.my) * s;
    const targetY = -(Math.sin(rotRad) * m.mx + Math.cos(rotRad) * m.my) * s;
    const cfg = { duration, easing };
    tx.value = withTiming(targetX, cfg);
    ty.value = withTiming(targetY, cfg);
    savedTx.value = targetX;
    savedTy.value = targetY;
  };

  const resetTransform = (animated = true) => {
    const cfg = { duration: theme.motion.standard, easing: EASE_STANDARD };
    scale.value = animated ? withTiming(1, cfg) : 1;
    tx.value = animated ? withTiming(0, cfg) : 0;
    ty.value = animated ? withTiming(0, cfg) : 0;
    rotation.value = animated ? withTiming(0, cfg) : 0;
    savedScale.value = 1;
    savedTx.value = 0;
    savedTy.value = 0;
    savedRotation.value = 0;
    lastHeading.current = 0;
  };

  const changeView = (nextProvince) => {
    resetTransform(false);
    setProvince(nextProvince);
    onProvinceChange?.(nextProvince);
  };

  // 지역 탭 → 하이라이트 + 이동 중 오버레이 → 드릴다운
  const selectProvince = (name) => {
    if (transitionTo) return;
    setTransitionTo(name);
    scale.value = withTiming(1.05, { duration: TRANSITION_MS, easing: EASE_STANDARD });
    transitionTimer.current = setTimeout(() => {
      changeView(name);
      setTransitionTo(null);
    }, TRANSITION_MS);
  };

  useImperativeHandle(ref, () => ({
    showProvince: (name) => {
      if (PROVINCE_MAP_KEYS[name] && name !== province) selectProvince(name);
    },
    showNational: () => changeView(null),
    /** 나침반 모드 진입: 전국 지도로 전환 + 내 위치를 화면 중앙으로 + 줌인 */
    enterCompass: () => {
      if (province) changeView(null);
      scale.value = withTiming(COMPASS_SCALE, {
        duration: theme.motion.page,
        easing: EASE_STANDARD,
      });
      savedScale.value = COMPASS_SCALE;
      applyCompassTransform(
        (lastHeading.current * Math.PI) / 180,
        theme.motion.page,
        EASE_STANDARD
      );
    },
    /** 나침반 모드: 기기 헤딩(도)을 받아 내 위치를 축으로 지도를 회전 */
    setHeading: (deg) => {
      const target = -deg;
      const prev = lastHeading.current;
      const delta = ((target - prev + 540) % 360) - 180; // 최단 경로 회전 (0/360 랩 방지)
      const next = prev + delta;
      lastHeading.current = next;
      const nextRad = (next * Math.PI) / 180;
      rotation.value = withTiming(nextRad, { duration: 150, easing: Easing.linear });
      savedRotation.value = nextRad;
      // 회전에 맞춰 내 위치가 계속 화면 중앙에 머물도록 이동량 갱신
      applyCompassTransform(nextRad);
      // 빔은 지도 좌표계에서 +heading 방향 → 지도 회전(-heading)과 합쳐 화면상 항상 위를 가리킴
      headingShared.value = withTiming(-next, { duration: 150, easing: Easing.linear });
    },
    /** 나침반 종료: 북쪽·기본 배율·중앙 정렬로 복귀 */
    resetHeading: () => {
      resetTransform(true);
      headingShared.value = withTiming(0, { duration: theme.motion.page });
    },
  }));

  // 나침반 모드 중 지도를 만지면 즉시 해제되도록 부모에 알림
  const notifyUserGesture = () => onUserGesture?.();

  /**
   * 팬 경계 — 배경(바다)의 가장자리가 화면 안쪽으로 들어오지 않도록 클램프.
   * 축 기준 배경이 화면보다 작아지면(=더 볼 게 없으면) 해당 축 이동을 중앙에 고정.
   */
  const clampTx = (v) => {
    'worklet';
    const k = scale.value;
    const c = stageW.value / 2;
    const hi = -c - k * (bgL.value - c);
    const lo = stageW.value - c - k * (bgR.value - c);
    if (lo > hi) return (lo + hi) / 2;
    return Math.min(hi, Math.max(lo, v));
  };
  const clampTy = (v) => {
    'worklet';
    const k = scale.value;
    const c = stageH.value / 2;
    const hi = -c - k * (bgT.value - c);
    const lo = stageH.value - c - k * (bgB.value - c);
    if (lo > hi) return (lo + hi) / 2;
    return Math.min(hi, Math.max(lo, v));
  };

  const pinch = Gesture.Pinch()
    .onStart(() => {
      'worklet';
      runOnJS(notifyUserGesture)();
    })
    .onUpdate((e) => {
      'worklet';
      // y축(또는 x축)으로 배경이 화면을 못 채우는 지점 아래로는 축소 금지
      scale.value = Math.min(MAX_SCALE, Math.max(minScale.value, savedScale.value * e.scale));
    })
    .onEnd(() => {
      'worklet';
      savedScale.value = scale.value;
      const cx = clampTx(tx.value);
      const cy = clampTy(ty.value);
      tx.value = withTiming(cx, { duration: 150 });
      ty.value = withTiming(cy, { duration: 150 });
      savedTx.value = cx;
      savedTy.value = cy;
    });

  const pan = Gesture.Pan()
    .onStart(() => {
      'worklet';
      runOnJS(notifyUserGesture)();
    })
    .onUpdate((e) => {
      'worklet';
      tx.value = clampTx(savedTx.value + e.translationX);
      ty.value = clampTy(savedTy.value + e.translationY);
    })
    .onEnd(() => {
      'worklet';
      savedTx.value = tx.value;
      savedTy.value = ty.value;
    });

  const rotate = Gesture.Rotation()
    .onStart(() => {
      'worklet';
      runOnJS(notifyUserGesture)();
    })
    .onUpdate((e) => {
      'worklet';
      rotation.value = savedRotation.value + e.rotation;
    })
    .onEnd(() => {
      'worklet';
      savedRotation.value = rotation.value;
    });

  const doubleTap = Gesture.Tap()
    .numberOfTaps(2)
    .maxDelay(180)
    .onEnd(() => {
      'worklet';
      const cfg = { duration: theme.motion.standard };
      scale.value = withTiming(1, cfg);
      tx.value = withTiming(0, cfg);
      ty.value = withTiming(0, cfg);
      rotation.value = withTiming(0, cfg);
      savedScale.value = 1;
      savedTx.value = 0;
      savedTy.value = 0;
      savedRotation.value = 0;
      runOnJS(notifyUserGesture)();
    });

  const gestures = Gesture.Simultaneous(pinch, pan, rotate, doubleTap);

  const zoomStyle = useAnimatedStyle(() => ({
    transform: [
      { translateX: tx.value },
      { translateY: ty.value },
      { rotate: `${rotation.value}rad` },
      { scale: scale.value },
    ],
  }));

  // 방향 빔 — View 오버레이라 reanimated 회전이 항상 헤딩을 추종한다
  const beamStyle = useAnimatedStyle(() => ({
    transform: [{ rotate: `${headingShared.value}deg` }],
  }));

  const beamR = BEAM_SIZE / 2 - 6;
  const beamDx = beamR * Math.sin((28 * Math.PI) / 180);
  const beamDy = beamR * Math.cos((28 * Math.PI) / 180);
  const beamCenter = BEAM_SIZE / 2;
  const beamPath = `M ${beamCenter} ${beamCenter} L ${beamCenter - beamDx} ${
    beamCenter - beamDy
  } A ${beamR} ${beamR} 0 0 1 ${beamCenter + beamDx} ${beamCenter - beamDy} Z`;

  const showBeam = compassActive && !province && markerStagePos;

  return (
    <View style={styles.container}>
      {province && !transitionTo && <MapToolbar onBack={() => changeView(null)} />}
      <GestureDetector gesture={gestures}>
        <Animated.View
          style={styles.stageWrap}
          onLayout={(e) => {
            const { width, height } = e.nativeEvent.layout;
            stageW.value = width;
            stageH.value = height;
            setStageSize({ w: width, h: height });
            // 배경 경계와 최소 축소 배율 계산 (배경이 화면을 딱 채우는 지점까지만 축소 허용)
            const s0 = Math.min(width / VB_W, height / VB_H);
            const ox = (width - VB_W * s0) / 2;
            const oy = (height - VB_H * s0) / 2;
            bgL.value = ox + BG_VB.x * s0;
            bgR.value = ox + (BG_VB.x + BG_VB.w) * s0;
            bgT.value = oy + BG_VB.y * s0;
            bgB.value = oy + (BG_VB.y + BG_VB.h) * s0;
            minScale.value = Math.max(
              0.35,
              height / (BG_VB.h * s0),
              width / (BG_VB.w * s0)
            );
          }}
        >
          <Animated.View
            key={province || 'national'}
            entering={FadeIn.duration(theme.motion.standard)}
            style={[styles.stage, zoomStyle]}
          >
            {province ? (
              <ProvinceMap regionName={province} />
            ) : (
              <>
                <SeaBackground stageSize={stageSize} />
                <NationalMap
                  regions={regions}
                  onSelectProvince={selectProvince}
                  userCenter={userCenter}
                  highlightRegion={transitionTo}
                />
              </>
            )}
            {showBeam && (
              <Animated.View
                pointerEvents="none"
                style={[
                  styles.beam,
                  {
                    left: markerStagePos.x - BEAM_SIZE / 2,
                    top: markerStagePos.y - BEAM_SIZE / 2,
                  },
                  beamStyle,
                ]}
              >
                <Svg width={BEAM_SIZE} height={BEAM_SIZE}>
                  <Path d={beamPath} fill={theme.colors.locationBlue} opacity={0.26} />
                </Svg>
              </Animated.View>
            )}
          </Animated.View>
        </Animated.View>
      </GestureDetector>

      {province && !transitionTo && (
        <ProvinceInfoCard
          regionName={province}
          regions={regions}
          onExplore={() => onExplore?.(province)}
        />
      )}

      {transitionTo && (
        <Animated.View
          entering={FadeIn.duration(theme.motion.fast)}
          style={styles.transitionOverlay}
          pointerEvents="none"
        >
          <View style={styles.transitionPill}>
            <ActivityIndicator size="small" color={theme.colors.primary} />
            <Text style={styles.transitionText}>{transitionTo}로 이동 중...</Text>
          </View>
        </Animated.View>
      )}
    </View>
  );
});

export default KoreaMap;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 8,
    paddingTop: 4,
    paddingBottom: 8,
  },
  stageWrap: {
    flex: 1,
    overflow: 'hidden',
    borderRadius: theme.rounded.lg,
    backgroundColor: theme.colors.mapSea,
  },
  stage: {
    flex: 1,
  },
  beam: {
    position: 'absolute',
    width: BEAM_SIZE,
    height: BEAM_SIZE,
  },
  emptyText: {
    textAlign: 'center',
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.body,
    color: theme.colors.textSecondary,
    padding: 24,
  },
  toolbar: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
    marginBottom: 8,
    paddingHorizontal: 4,
  },
  backPill: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
    paddingVertical: 6,
    paddingHorizontal: 10,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.rounded.full,
    backgroundColor: theme.colors.white,
  },
  backPillText: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.caption,
    fontWeight: '600',
    color: theme.colors.textSecondary,
  },
  infoCard: {
    position: 'absolute',
    left: 20,
    right: 20,
    bottom: 16,
    backgroundColor: theme.colors.white,
    borderRadius: theme.rounded.lg,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: 16,
    ...theme.shadow.outlined,
  },
  infoCardTop: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 12,
    marginBottom: 12,
  },
  infoCardTitleCol: {
    flex: 1,
    minWidth: 0,
  },
  infoCardName: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.heading,
    fontWeight: '700',
    color: theme.colors.text,
  },
  infoCardMeta: {
    fontFamily: theme.typography.fontFamily.regular,
    fontSize: theme.typography.size.bodySmall,
    color: theme.colors.textSecondary,
    marginTop: 4,
  },
  infoCardBadge: {
    paddingVertical: 5,
    paddingHorizontal: 12,
    borderRadius: theme.rounded.full,
  },
  infoCardBadgeText: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.caption,
    fontWeight: '700',
    color: theme.colors.white,
  },
  progressTrack: {
    height: 6,
    borderRadius: theme.rounded.full,
    backgroundColor: theme.colors.surfaceDim,
    overflow: 'hidden',
    marginBottom: 14,
  },
  progressFill: {
    height: '100%',
    borderRadius: theme.rounded.full,
    backgroundColor: theme.colors.primary,
  },
  exploreBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    height: 48,
    borderRadius: theme.rounded.md,
    backgroundColor: theme.colors.primary,
  },
  exploreBtnText: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.bodyLarge,
    fontWeight: '700',
    color: theme.colors.white,
  },
  transitionOverlay: {
    ...StyleSheet.absoluteFillObject,
    alignItems: 'center',
    justifyContent: 'center',
  },
  transitionPill: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    paddingVertical: 12,
    paddingHorizontal: 18,
    backgroundColor: theme.colors.white,
    borderRadius: theme.rounded.full,
    borderWidth: 1,
    borderColor: theme.colors.border,
    ...theme.shadow.sharp,
  },
  transitionText: {
    fontFamily: theme.typography.fontFamily.bold,
    fontSize: theme.typography.size.body,
    fontWeight: '600',
    color: theme.colors.text,
  },
});
