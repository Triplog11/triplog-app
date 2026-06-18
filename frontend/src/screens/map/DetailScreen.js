import React from 'react';
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, SafeAreaView } from 'react-native';

export default function DetailScreen({ route, navigation }) {
  const { regionName = '수원시' } = route.params || {};

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>{regionName} 상세 정보 🏛️</Text>
        <Text style={styles.subtitle}>{regionName}에서 즐길 수 있는 대표 랜드마크입니다.</Text>
      </View>

      <ScrollView contentContainerStyle={styles.scrollContent}>
        <View style={styles.landmarkCard}>
          <Text style={styles.landmarkName}>수원 화성 행궁</Text>
          <Text style={styles.landmarkDesc}>
            조선 시대 성곽 문화의 꽃, 정조대왕의 원대한 꿈이 담긴 별궁입니다.
          </Text>
          <TouchableOpacity 
            style={styles.visitBtn}
            onPress={() => navigation.navigate('VisitCert', { placeName: '수원 화성 행궁' })}
          >
            <Text style={styles.visitBtnText}>방문 인증하러 가기 📸</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.landmarkCard}>
          <Text style={styles.landmarkName}>광교 호수공원</Text>
          <Text style={styles.landmarkDesc}>
            도심 속 아름다운 수변 경관과 함께 쾌적한 산책을 즐길 수 있는 가족 휴양지입니다.
          </Text>
          <TouchableOpacity 
            style={styles.visitBtn}
            onPress={() => navigation.navigate('VisitCert', { placeName: '광교 호수공원' })}
          >
            <Text style={styles.visitBtnText}>방문 인증하러 가기 📸</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
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
  scrollContent: {
    paddingHorizontal: 24,
    gap: 16,
  },
  landmarkCard: {
    backgroundColor: '#1E293B',
    padding: 20,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#334155',
  },
  landmarkName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#F8FAFC',
    marginBottom: 8,
  },
  landmarkDesc: {
    fontSize: 14,
    color: '#94A3B8',
    lineHeight: 20,
    marginBottom: 16,
  },
  visitBtn: {
    height: 48,
    backgroundColor: '#38BDF8',
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  visitBtnText: {
    color: '#0F172A',
    fontWeight: 'bold',
    fontSize: 14,
  },
});
