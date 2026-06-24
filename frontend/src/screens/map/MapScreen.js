import React from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';

export default function MapScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color="#0F172A">
          전국 탐험 지도 🗺️
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
          인증을 마쳐서 맵의 어두운 부분을 해제해 보세요.
        </CustomText>
      </View>

      <View style={styles.mapContainer}>
        {/* 모던하고 밝은 느낌의 가상 지도 캔버스 데코 */}
        <View style={styles.dummyMapGraphic}>
          <View style={styles.gridOverlay}>
            <CustomText variant="Overline" color="#B2C8DF" style={styles.graphicText}>
              MAP AREA (경기도 일대)
            </CustomText>
          </View>
          
          {/* 가상 마커 1 (수원) */}
          <TouchableOpacity 
            style={[styles.marker, { top: '22%', left: '25%' }]}
            onPress={() => navigation.navigate('Detail', { regionName: '수원시' })}
            activeOpacity={0.85}
          >
            <CustomText variant="Label/Medium" color="#0F172A" style={styles.markerText}>
              📍 수원
            </CustomText>
          </TouchableOpacity>

          {/* 가상 마커 2 (광주) */}
          <TouchableOpacity 
            style={[styles.marker, { top: '45%', left: '60%' }]}
            onPress={() => navigation.navigate('Detail', { regionName: '광주시' })}
            activeOpacity={0.85}
          >
            <CustomText variant="Label/Medium" color="#0F172A" style={styles.markerText}>
              📍 광주
            </CustomText>
          </TouchableOpacity>

          {/* 가상 마커 3 (용인) */}
          <TouchableOpacity 
            style={[styles.marker, { top: '65%', left: '40%' }]}
            onPress={() => navigation.navigate('Detail', { regionName: '용인시' })}
            activeOpacity={0.85}
          >
            <CustomText variant="Label/Medium" color="#0F172A" style={styles.markerText}>
              📍 용인
            </CustomText>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8FAFC', // Slate-50 공통 배경
  },
  header: {
    paddingHorizontal: 24,
    paddingTop: 30,
    paddingBottom: 16,
  },
  headerSubtitle: {
    marginTop: 6,
    fontWeight: '500',
  },
  mapContainer: {
    flex: 1,
    marginHorizontal: 24,
    marginBottom: 30,
    backgroundColor: '#FFFFFF',
    borderRadius: 24,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#E2E8F0',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.04,
    shadowRadius: 20,
    elevation: 3,
  },
  dummyMapGraphic: {
    flex: 1,
    backgroundColor: '#F1F5F9', // 연한 회색/하늘색 톤의 맵 캔버스
    position: 'relative',
  },
  gridOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#E2E8F0',
    borderStyle: 'dashed',
  },
  graphicText: {
    fontWeight: 'bold',
    letterSpacing: 1,
  },
  marker: {
    position: 'absolute',
    backgroundColor: '#FFFFFF', // 순백색 마커 카드
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#3B82F6', // 브랜드 블루 보더
    shadowColor: '#3B82F6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.12,
    shadowRadius: 8,
    elevation: 4,
  },
  markerText: {
    fontWeight: 'bold',
  },
});
