/**
 * TripLog Design System Theme
 * Source of Truth: ./DESIGN.md (oh-my-design, bootstrapped from baemin)
 * TripLog Mint #2AC1BC 단일 액센트 — 카드 기반 구성, 5단계 그림자, 기능 UI는 절제된 모션
 */
export const theme = {
  colors: {
    // Brand
    primary: '#2AC1BC',
    primaryDark: '#20A8A4',   // pressed
    primaryLight: '#20C997',
    primarySoft: '#EAF9F8',   // ghost pressed rgba(42,193,188,0.08) 근사

    // Accents (semantic)
    accent: '#12B886',
    accentMint: '#20C997',
    error: '#FF6B6B',
    warning: '#FFB347',
    info: '#74C0FC',
    locationBlue: '#339AF0',  // 현재 위치 마커 (info 계열 강조 톤)

    // Surfaces
    white: '#FFFFFF',
    canvas: '#FFFFFF',
    surface: '#F8F9FA',
    surfaceDim: '#F1F3F5',

    // Text (순수 검정 금지 — #212529 사용)
    text: '#212529',
    textBody: '#495057',
    textSecondary: '#868E96',
    textMuted: '#ADB5BD',

    // Borders
    border: '#DEE2E6',
    borderStrong: '#343A40',

    // Map
    mapLand: '#D7EDEC',
    mapLandActive: '#2AC1BC',
    mapStroke: 'rgba(255,255,255,0.8)',
    mapOutline: '#8AD5D2',
    mapSea: '#D9EAF6',
    mapNeighbor: '#E7EBEE',

    // Bottom nav (baemin bottom-tab-bar)
    navActive: '#2AC1BC',
    navInactive: '#868E96',

    // 레거시 별칭 (구 화면 호환 — 리디자인 시 제거)
    blueTint: '#EAF9F8',
    blueWash: 'rgba(42,193,188,0.07)',
    success: '#12B886',
    textPrimary: '#212529',
    textTertiary: '#ADB5BD',
  },

  typography: {
    fontFamily: {
      light: 'Pretendard-Light',
      regular: 'Pretendard-Regular',
      bold: 'Pretendard-Bold',
    },
    // DESIGN.md type scale
    size: {
      caption: 12,
      bodySmall: 13,
      body: 14,
      bodyLarge: 16,
      title: 18,
      heading: 20,
      headingLarge: 24,
      displayLarge: 36,
      displayHero: 42,
      // 하위 호환 별칭 (기존 컴포넌트 사용 키)
      xs: 12,
      sm: 14,
      base: 15,
      md: 18,
      lg: 20,
      xl: 24,
    },
  },

  spacing: {
    xs: 4,
    sm: 8,
    md: 12,
    base: 16,
    lg: 20,
    xl: 24,
    xxl: 32,
    section: 40,
    max: 48,
    // 하위 호환 별칭
    '2xl': 24,
    '3xl': 32,
  },

  rounded: {
    sm: 4,
    md: 8,
    lg: 12,
    search: 20,
    full: 9999,
    pill: 9999, // 레거시 별칭 (구 화면 호환)
  },

  // 5단계 그림자 (Natural → Crisp)
  shadow: {
    natural: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.04,
      shadowRadius: 3,
      elevation: 1,
    },
    deep: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.08,
      shadowRadius: 8,
      elevation: 3,
    },
    sharp: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.1,
      shadowRadius: 12,
      elevation: 6,
    },
    outlined: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.12,
      shadowRadius: 16,
      elevation: 8,
    },
    crisp: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 8 },
      shadowOpacity: 0.16,
      shadowRadius: 24,
      elevation: 12,
    },
  },

  motion: {
    fast: 150,
    standard: 250,
    slow: 400,
    page: 300,
    normal: 250, // 하위 호환 별칭
    // ease-bounce는 뱃지 획득/찜 토글 두 곳에만 허용 (DESIGN.md §15)
    spring: { damping: 14, stiffness: 220, mass: 0.7 },
    // reanimated Easing.bezier 인자
    easeStandard: [0.4, 0, 0.2, 1],
    easeEnter: [0, 0, 0.2, 1],
    easeExit: [0.4, 0, 1, 1],
  },
};

export default theme;
