import React, { useState } from 'react';
import { StyleSheet, View, Text, TouchableOpacity, Dimensions } from 'react-native';
import Svg, { Path } from 'react-native-svg';
import theme from '../../../theme/theme';
import { MAP_PATHS } from './mapPaths';

const { width } = Dimensions.get('window');

// 9개 전국 지도 조각 매핑
const NATIONAL_SHAPE_REGIONS = [
  '전라남도',
  '전라북도',
  '서울특별시',
  '강원특별자치도',
  '경기도',
  '경상남도',
  '경상북도',
  '충청북도',
  '제주특별자치도',
];

const PROVINCE_SVG_KEYS = {
  '서울특별시': '서울특별시',
  '부산광역시': '부산광역시',
  '인천광역시': '인천광역시',
  '대구광역시': '대구광역시',
  '광주광역시': '광주광역시',
  '대전광역시': '대전광역시',
  '울산광역시': '울산광역시',
  '세종특별자치시': '세종특별자치시',
  '강원특별자치도': '강원도',
  '강원도': '강원도',
  '충청북도': '충청북도',
  '충청남도': '충청남도',
  '전라북도': '전라북도',
  '전라남도': '전라남도',
  '경상북도': '경상북도',
  '경상남도': '경상남도',
  '제주특별자치도': '제주특별자치도',
  '경기도': 'gyeonggi',
};

// 틸그린 디자인 시스템 테마 색상 적용
const MAP_UNVISITED = '#D4E6E4';
const MAP_INPROGRESS = '#7BBDB8';
const MAP_COMPLETED = '#3A8D84';

export default function KoreaMap({ regions, onRegionPress }) {
  const [selectedProvince, setSelectedProvince] = useState(null);

  // 방문 상태에 따른 색상 헬퍼
  const getFillColor = (regionName, isProvince = false) => {
    if (isProvince) {
      // 대전, 서울 등 주요 도시/도의 전체 평균 상태로 색칠
      const subRegions = regions.filter(r => r.province === regionName || r.name === regionName);
      if (subRegions.length === 0) return MAP_UNVISITED;
      
      const completedCount = subRegions.filter(r => r.completed).length;
      const startedCount = subRegions.filter(r => r.progress > 0).length;
      
      if (completedCount === subRegions.length) return MAP_COMPLETED;
      if (startedCount > 0) return MAP_INPROGRESS;
      return MAP_UNVISITED;
    }

    // 개별 자치구/시군구 단위
    const match = regions.find(r => r.name === regionName);
    if (!match) return MAP_UNVISITED;
    if (match.completed) return MAP_COMPLETED;
    if (match.progress > 0) return MAP_INPROGRESS;
    return MAP_UNVISITED;
  };

  const getStrokeColor = (regionName, isProvince = false) => {
    if (isProvince) return '#B8CCC6';
    const match = regions.find(r => r.name === regionName);
    if (match && match.progress > 0) return theme.colors.primary;
    return '#E8ECEB';
  };

  const handleNationalPress = (index) => {
    const regionName = NATIONAL_SHAPE_REGIONS[index];
    if (regionName) {
      setSelectedProvince(regionName);
    }
  };

  // 전국 지도 렌더링
  const renderNationalMap = () => {
    const mapData = MAP_PATHS['korea'];
    if (!mapData) return null;

    return (
      <View style={styles.svgWrapper}>
        <Text style={styles.mapTitle}>대한민국 전체 지도 🇰🇷</Text>
        <Text style={styles.mapInstruction}>지역을 터치하면 세부 지도로 확대됩니다</Text>
        
        <Svg 
          width={width - 48} 
          height={380} 
          viewBox={mapData.viewBox}
          style={styles.svg}
        >
          {mapData.paths.map((d, index) => {
            const regionName = NATIONAL_SHAPE_REGIONS[index] || '미지정';
            const fillColor = getFillColor(regionName, true);
            
            return (
              <Path
                key={`national-${index}`}
                d={d}
                fill={fillColor}
                stroke="#FFFFFF"
                strokeWidth="1.5"
                onPress={() => handleNationalPress(index)}
              />
            );
          })}
        </Svg>
      </View>
    );
  };

  // 시도 상세 지도 렌더링
  const renderProvinceMap = () => {
    const svgKey = PROVINCE_SVG_KEYS[selectedProvince];
    const mapData = MAP_PATHS[svgKey];
    if (!mapData) {
      return (
        <View style={styles.svgWrapper}>
          <Text style={styles.mapTitle}>지도를 준비 중입니다</Text>
          <TouchableOpacity style={styles.backBtn} onPress={() => setSelectedProvince(null)}>
            <Text style={styles.backBtnText}>전국 지도로 돌아가기</Text>
          </TouchableOpacity>
        </View>
      );
    }

    return (
      <View style={styles.svgWrapper}>
        <View style={styles.zoomHeader}>
          <TouchableOpacity style={styles.backBtn} onPress={() => setSelectedProvince(null)}>
            <Text style={styles.backBtnText}>← 전국 지도</Text>
          </TouchableOpacity>
          <Text style={styles.mapTitle}>{selectedProvince} 상세 지도</Text>
        </View>
        <Text style={styles.mapInstruction}>각 구역을 탭하면 랜드마크 명세로 이동합니다</Text>

        <Svg 
          width={width - 48} 
          height={320} 
          viewBox={mapData.viewBox}
          style={styles.svg}
        >
          {mapData.paths.map((d, index) => {
            // 자치구 이름 매핑 (예시)
            const subRegionName = `${selectedProvince} 구역 ${index + 1}`;
            
            return (
              <Path
                key={`province-${index}`}
                d={d}
                fill={getFillColor(subRegionName)}
                stroke="#FFFFFF"
                strokeWidth="1"
                onPress={() => {
                  if (onRegionPress) {
                    onRegionPress(subRegionName);
                  }
                }}
              />
            );
          })}
        </Svg>
      </View>
    );
  };

  return (
    <View style={styles.mapContainer}>
      {!selectedProvince ? renderNationalMap() : renderProvinceMap()}
    </View>
  );
}

const styles = StyleSheet.create({
  mapContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  svgWrapper: {
    backgroundColor: '#FFFFFF',
    borderRadius: theme.rounded.lg,
    padding: 16,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#E8ECEB',
    width: '100%',
  },
  svg: {
    alignSelf: 'center',
  },
  mapTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#1F2937',
  },
  mapInstruction: {
    fontSize: 11,
    color: '#6B7280',
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
    backgroundColor: '#EFF6FF',
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
