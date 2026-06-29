import React, { useState } from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import Svg, { G, Path, Rect } from 'react-native-svg';
import theme from '../../../theme/theme';

export default function KoreaMap({ regions, onRegionPress }) {
  const [isDaejeonZoomed, setIsDaejeonZoomed] = useState(false);

  // 더미 데이터 상태 매핑 헬퍼
  const getFillColor = (regionName, isProvince = false) => {
    if (isProvince) {
      // 대전의 경우, 하위 구들의 상태를 체크해 복합적인 색상을 반환
      if (regionName === '대전') {
        const daejeonSub = regions.filter(r => r.province === '대전광역시');
        const completedAll = daejeonSub.every(r => r.completed);
        const startedSome = daejeonSub.some(r => r.progress > 0);
        if (completedAll) return theme.colors.success;
        if (startedSome) return theme.colors.blueTint;
        return theme.colors.surface;
      }
      // 기타 타 지역(아직 미구현 데이터)은 기본 미방문 색상
      return theme.colors.surface;
    }

    // 시/군/구 단위 체크 (예: 유성구, 중구 등)
    const match = regions.find(r => r.name === regionName);
    if (!match) return theme.colors.surface;
    if (match.completed) return theme.colors.primary; // 완료는 트립로그 블루로 칠함
    if (match.progress > 0) return theme.colors.blueTint; // 진행 중은 연한 블루
    return theme.colors.surface; // 미방문은 회색
  };

  const getStrokeColor = (regionName, isProvince = false) => {
    if (isProvince) {
      return '#CBD5E1';
    }
    const match = regions.find(r => r.name === regionName);
    if (match && match.progress > 0) return theme.colors.primary;
    return '#94A3B8';
  };

  return (
    <View style={styles.mapContainer}>
      {!isDaejeonZoomed ? (
        // 1단계: 전국 시/도 지도
        <View style={styles.svgWrapper}>
          <Text style={styles.mapTitle}>대한민국 전체 지도 🇰🇷</Text>
          <Text style={styles.mapInstruction}>'대전' 지역을 누르면 구별 도감 지도로 확대됩니다</Text>
          <Svg width="300" height="420" viewBox="0 0 300 420">
            {/* 강원도 */}
            <Path
              d="M170 30 L250 50 L240 120 L180 110 L150 70 Z"
              fill={getFillColor('강원', true)}
              stroke={getStrokeColor('강원', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('강원')}
            />
            {/* 경기도 */}
            <Path
              d="M100 60 L150 70 L180 110 L140 140 L100 120 L90 80 Z"
              fill={getFillColor('경기', true)}
              stroke={getStrokeColor('경기', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('경기')}
            />
            {/* 서울 (경기 내부에 노란색 포인트) */}
            <Path
              d="M125 85 L145 85 L140 98 L120 98 Z"
              fill="#F59E0B"
              stroke="#B45309"
              strokeWidth="1.5"
              onPress={() => onRegionPress('서울')}
            />
            {/* 충청북도 */}
            <Path
              d="M180 110 L210 130 L190 190 L140 170 L150 140 Z"
              fill={getFillColor('충북', true)}
              stroke={getStrokeColor('충북', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('충북')}
            />
            {/* 충청남도 */}
            <Path
              d="M75 130 L140 140 L150 170 L110 200 L70 170 Z"
              fill={getFillColor('충남', true)}
              stroke={getStrokeColor('충남', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('충남')}
            />
            {/* 대전광역시 (충남북 경계 사이 - 게임 속 시그니처 랜드마크처럼 처리) */}
            <Path
              d="M135 170 L155 170 L150 185 L130 185 Z"
              fill={getFillColor('대전', true)}
              stroke={theme.colors.primary}
              strokeWidth="2.5"
              onPress={() => setIsDaejeonZoomed(true)}
            />
            {/* 경상북도 */}
            <Path
              d="M210 130 L270 150 L280 230 L220 250 L190 190 Z"
              fill={getFillColor('경북', true)}
              stroke={getStrokeColor('경북', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('경북')}
            />
            {/* 전라북도 */}
            <Path
              d="M90 200 L150 200 L170 240 L110 260 L80 240 Z"
              fill={getFillColor('전북', true)}
              stroke={getStrokeColor('전북', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('전북')}
            />
            {/* 경상남도 */}
            <Path
              d="M170 240 L220 250 L260 260 L240 300 L170 280 Z"
              fill={getFillColor('경남', true)}
              stroke={getStrokeColor('경남', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('경남')}
            />
            {/* 전라남도 */}
            <Path
              d="M70 260 L110 260 L140 310 L150 330 L60 330 Z"
              fill={getFillColor('전남', true)}
              stroke={getStrokeColor('전남', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('전남')}
            />
            {/* 제주도 */}
            <Path
              d="M90 360 L150 360 L140 380 L80 380 Z"
              fill={getFillColor('제주', true)}
              stroke={getStrokeColor('제주', true)}
              strokeWidth="2"
              onPress={() => onRegionPress('제주')}
            />
          </Svg>
        </View>
      ) : (
        // 2단계: 대전광역시 상세 구별 지도 (Zoom-in 모드)
        <View style={styles.svgWrapper}>
          <View style={styles.zoomHeader}>
            <TouchableOpacity style={styles.backBtn} onPress={() => setIsDaejeonZoomed(false)}>
              <Text style={styles.backBtnText}>← 전국 지도</Text>
            </TouchableOpacity>
            <Text style={styles.mapTitle}>대전광역시 도감 지도 🏙️</Text>
          </View>
          <Text style={styles.mapInstruction}>각 구를 탭하여 해당 구의 랜드마크 명세로 이동하세요</Text>
          
          <Svg width="300" height="360" viewBox="0 0 300 360">
            {/* 대덕구 (북쪽) */}
            <Path
              d="M140 30 L200 40 L190 130 L130 130 L120 70 Z"
              fill={getFillColor('대덕구')}
              stroke={getStrokeColor('대덕구')}
              strokeWidth="2"
              onPress={() => onRegionPress('대덕구')}
            />
            {/* 유성구 (서쪽) */}
            <Path
              d="M50 80 L130 80 L130 160 L100 240 L40 180 L30 120 Z"
              fill={getFillColor('유성구')}
              stroke={getStrokeColor('유성구')}
              strokeWidth="2"
              onPress={() => onRegionPress('유성구')}
            />
            {/* 서구 (남서쪽) */}
            <Path
              d="M100 240 L140 180 L150 250 L120 320 L70 310 Z"
              fill={getFillColor('서구')}
              stroke={getStrokeColor('서구')}
              strokeWidth="2"
              onPress={() => onRegionPress('서구')}
            />
            {/* 중구 (남쪽) */}
            <Path
              d="M140 180 L180 180 L200 290 L160 320 L150 250 Z"
              fill={getFillColor('중구')}
              stroke={getStrokeColor('중구')}
              strokeWidth="2"
              onPress={() => onRegionPress('중구')}
            />
            {/* 동구 (동쪽) */}
            <Path
              d="M190 130 L250 140 L260 250 L200 290 L180 180 L130 160 Z"
              fill={getFillColor('동구')}
              stroke={getStrokeColor('동구')}
              strokeWidth="2"
              onPress={() => onRegionPress('동구')}
            />
          </Svg>
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  mapContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: 16,
  },
  svgWrapper: {
    backgroundColor: '#FFFFFF',
    borderRadius: theme.rounded.lg,
    padding: 16,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: theme.colors.border,
    width: '100%',
  },
  mapTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: theme.colors.textPrimary,
  },
  mapInstruction: {
    fontSize: 11,
    color: theme.colors.textSecondary,
    marginTop: 4,
    marginBottom: 16,
  },
  zoomHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    width: '100%',
    justifyContent: 'center',
    position: 'relative',
    marginBottom: 8,
  },
  backBtn: {
    position: 'absolute',
    left: 0,
    backgroundColor: theme.colors.blueWash,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: theme.rounded.sm,
  },
  backBtnText: {
    fontSize: 12,
    fontWeight: '600',
    color: theme.colors.primary,
  },
});
