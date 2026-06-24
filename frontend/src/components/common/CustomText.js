import React from 'react';
import { Text as RNText } from 'react-native';
import { TYPOGRAPHY } from '../../constants/typography';

/**
 * 트립로그 공통 텍스트 컴포넌트 (CustomText)
 * 
 * @param {string} variant - 타이포그래피 스타일 프리셋 키 (예: 'Heading/H1', 'Body/Medium')
 * @param {string} color - 텍스트 색상 (기본값: '#000000')
 * @param {object|array} style - 추가로 커스터마이징할 React Native 스타일 객체
 * @param {React.ReactNode} children - 렌더링할 텍스트 내용
 */
export default function CustomText({
  variant = 'Body/Medium',
  color = '#333333', // 기본 텍스트 색상 (Semantic-Text-default 에 준함)
  style,
  children,
  ...props
}) {
  // 매칭되는 타이포그래피 스타일 선택 (없을 시 기본 'Body/Medium' 적용)
  const textStyle = TYPOGRAPHY[variant] || TYPOGRAPHY['Body/Medium'];

  return (
    <RNText
      style={[
        textStyle,
        { color },
        style, // 외부 추가 스타일 병합
      ]}
      {...props}
    >
      {children}
    </RNText>
  );
}
