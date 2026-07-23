/**
 * TripLog Design System Theme
 * Source of Truth: frontend/assets/DESIGN.md (prototype-2026, 서팍 최신 핸드오프)
 * Blue #368FFF primary + Teal #0ECEDB 액센트 — 보더 우선 평면 UI, 브랜드 로고는 틸(#00ADBA)
 */
export const theme = {
  colors: {
    // Brand (--primary 계열)
    primary: '#368FFF',
    primaryDark: '#1A85E8',   // pressed / :active
    primaryLight: '#1AADF6',
    primarySoft: '#F1F7FF',   // 행 hover, 소프트 버튼 배경

    // Accents (semantic)
    accent: '#0ECEDB',        // --accent-mint / --highlight-teal (수집 완료 등)
    accentMint: '#0ECEDB',
    accentGold: '#E8B84A',
    accentOrange: '#FF9600',
    logoTeal: '#00ADBA',      // 브랜드 로고 배경색 (스플래시/아이콘)
    error: '#FF6B6B',
    warning: '#FF9600',
    info: '#74C0FC',
    locationBlue: '#339AF0',  // 현재 위치 마커 (info 계열 강조 톤)

    // Surfaces
    white: '#FFFFFF',
    canvas: '#FFFFFF',
    surface: '#F5F6F7',
    surfaceDim: '#EEF0F2',

    // Text (순수 검정 금지 — #1B1D1F 사용)
    text: '#1B1D1F',
    textBody: '#1B1D1F',
    textSecondary: '#7E848A',
    textMuted: '#ADB1B5',

    // Borders
    border: '#E8EAEC',
    borderStrong: '#D5D8DB',

    // Map
    mapLand: '#E7EEF5',
    mapLandActive: '#368FFF',
    mapStroke: 'rgba(255,255,255,0.8)',
    mapOutline: '#A9C4E4',
    mapSea: '#D9EAF6',
    mapNeighbor: '#E7EBEE',

    // Bottom nav (플로팅 pill)
    navActive: '#368FFF',
    navInactive: '#7E848A',

    // 레거시 별칭 (구 화면 호환 — 리디자인 시 제거)
    blueTint: '#F1F7FF',
    blueWash: 'rgba(54,143,255,0.07)',
    success: '#12B886',
    textPrimary: '#1B1D1F',
    textTertiary: '#ADB1B5',
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
    card: 16, // 카드, 메뉴 (--radius-lg)
    xl: 20,   // 시트 상단 (--radius-xl)
    cta: 36,  // 풀폭 CTA 버튼 (--radius-5xl)
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
