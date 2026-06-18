import React from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView, Dimensions } from 'react-native';

const { width } = Dimensions.get('window');

export default function MapScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>전국 탐험 지도 🗺️</Text>
        <Text style={styles.subtitle}>인증을 마쳐서 맵의 어두운 부분을 해제해 보세요.</Text>
      </View>

      <View style={styles.mapContainer}>
        {/* 가상의 지도 시각화 그래픽 데코 */}
        <View style={styles.dummyMapGraphic}>
          <Text style={styles.graphicText}>MAP AREA (경기도 일대)</Text>
          
          {/* 가상 마커 1 */}
          <TouchableOpacity 
            style={[styles.marker, { top: 80, left: 100 }]}
            onPress={() => navigation.navigate('Detail', { regionName: '수원시' })}
          >
            <Text style={styles.markerText}>📍 수원</Text>
          </TouchableOpacity>

          {/* 가상 마커 2 */}
          <TouchableOpacity 
            style={[styles.marker, { top: 150, left: 200 }]}
            onPress={() => navigation.navigate('Detail', { regionName: '광주시' })}
          >
            <Text style={styles.markerText}>📍 광주</Text>
          </TouchableOpacity>

          {/* 가상 마커 3 */}
          <TouchableOpacity 
            style={[styles.marker, { top: 220, left: 140 }]}
            onPress={() => navigation.navigate('Detail', { regionName: '용인시' })}
          >
            <Text style={styles.markerText}>📍 용인</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0F172A',
  },
  header: {
    padding: 24,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#F8FAFC',
  },
  subtitle: {
    fontSize: 14,
    color: '#94A3B8',
    marginTop: 6,
  },
  mapContainer: {
    flex: 1,
    marginHorizontal: 24,
    marginBottom: 24,
    backgroundColor: '#1E293B',
    borderRadius: 24,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#334155',
  },
  dummyMapGraphic: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#111827',
  },
  graphicText: {
    color: '#334155',
    fontWeight: 'bold',
    fontSize: 16,
  },
  marker: {
    position: 'absolute',
    backgroundColor: '#1E293B',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    borderWidth: 1.5,
    borderColor: '#38BDF8',
    shadowColor: '#38BDF8',
    shadowOffset: { width: 0, height: 0 },
    shadowOpacity: 0.5,
    shadowRadius: 6,
    elevation: 5,
  },
  markerText: {
    color: '#F8FAFC',
    fontSize: 12,
    fontWeight: 'bold',
  },
});
