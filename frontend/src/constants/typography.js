/**
 * Typography Design System Constants for TripLog
 * 피그마 가이드라인 기반 타이포그래피 변수 및 스타일 프리셋 정의
 */

// 1. 프리미티브 변수 정의
export const FONT_FAMILY = {
  light: 'Pretendard-Light',
  regular: 'Pretendard-Regular',
  bold: 'Pretendard-Bold',
};

export const FONT_SIZE = {
  '2xs': 10,
  xs: 11,
  sm: 12,
  md: 14,
  base: 16,
  lg: 18,
  xl: 20,
  '2xl': 24,
  '3xl': 28,
  '4xl': 32,
  '5xl': 36,
  '6xl': 40,
  '7xl': 48,
  '8xl': 56,
  '9xl': 64,
  '10xl': 72,
};

export const FONT_WEIGHT = {
  light: '300',
  regular: '400',
  bold: '700',
};

// React Native는 % 단위를 지원하지 않으므로, 곱해서 절대값으로 계산하기 위한 multiplier 정의
export const LINE_HEIGHT_MULT = {
  tight: 1.2,
  snug: 1.4,
  relaxed: 1.6,
};

export const LETTER_SPACING_MULT = {
  narrow: -0.005, // -0.5%
  default: 0,     // 0%
  wide: 0.025,    // 2.5%
};

// 헬퍼 함수: 자간/행간을 React Native 스타일에 맞춰 계산
const createTextStyle = (sizeKey, weightKey, lineHeightKey, letterSpacingKey) => {
  const fontSize = FONT_SIZE[sizeKey];
  const fontFamily = FONT_FAMILY[weightKey];
  const fontWeight = FONT_WEIGHT[weightKey];
  
  // React Native는 absolute px만 지원하므로 곱해줍니다. (소수점 정리)
  const lineHeight = Math.round(fontSize * LINE_HEIGHT_MULT[lineHeightKey]);
  const letterSpacing = Number((fontSize * LETTER_SPACING_MULT[letterSpacingKey]).toFixed(2));

  return {
    fontFamily,
    fontSize,
    fontWeight,
    lineHeight,
    letterSpacing,
  };
};

// 2. 18가지 공통 텍스트 스타일 프리셋 정의
export const TYPOGRAPHY = {
  // Display
  'Display/Large': createTextStyle('10xl', 'bold', 'tight', 'narrow'),
  'Display/Medium': createTextStyle('9xl', 'bold', 'tight', 'narrow'),
  'Display/Small': createTextStyle('8xl', 'bold', 'tight', 'narrow'),

  // Heading
  'Heading/H1': createTextStyle('5xl', 'bold', 'tight', 'narrow'),
  'Heading/H2': createTextStyle('4xl', 'bold', 'tight', 'narrow'),
  'Heading/H3': createTextStyle('3xl', 'bold', 'tight', 'default'),
  'Heading/H4': createTextStyle('2xl', 'regular', 'tight', 'default'),
  'Heading/H5': createTextStyle('xl', 'regular', 'snug', 'default'),

  // Body
  'Body/Large': createTextStyle('lg', 'regular', 'relaxed', 'default'),
  'Body/Medium': createTextStyle('base', 'regular', 'relaxed', 'default'),
  'Body/Small': createTextStyle('md', 'regular', 'relaxed', 'default'),

  // Label
  'Label/Large': createTextStyle('md', 'regular', 'snug', 'wide'),
  'Label/Medium': createTextStyle('sm', 'regular', 'snug', 'wide'),
  'Label/Small': createTextStyle('xs', 'regular', 'snug', 'wide'),

  // UI
  'UI/Button': createTextStyle('base', 'regular', 'snug', 'default'),
  'UI/Button/Small': createTextStyle('sm', 'regular', 'snug', 'wide'),

  // Caption & Overline
  'Caption': createTextStyle('2xs', 'light', 'snug', 'wide'),
  'Overline': createTextStyle('xs', 'regular', 'snug', 'wide'),
};
