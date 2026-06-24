import React from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';

export default function DetailScreen({ route, navigation }) {
  const { regionName = '수원시' } = route.params || {};

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color="#0F172A">
          {regionName} 상세 정보 🏛️
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
          {regionName}에서 즐길 수 있는 대표 랜드마크입니다.
        </CustomText>
      </View>

      <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
        {/* 랜드마크 카드 1 */}
        <View style={styles.landmarkCard}>
          <CustomText variant="Heading/H4" color="#0F172A" style={styles.landmarkName}>
            수원 화성 행궁
          </CustomText>
          <CustomText variant="Body/Small" color="#64748B" style={styles.landmarkDesc}>
            조선 시대 성곽 문화의 꽃, 정조대왕의 원대한 꿈이 담긴 별궁입니다. 고풍스러운 한옥 정취를 감상해 보세요.
          </CustomText>
          <TouchableOpacity 
            style={styles.visitBtn}
            onPress={() => navigation.navigate('VisitCert', { placeName: '수원 화성 행궁' })}
            activeOpacity={0.85}
          >
            <CustomText variant="UI/Button" color="#FFFFFF" style={styles.visitBtnText}>
              방문 인증하러 가기 📸
            </CustomText>
          </TouchableOpacity>
        </View>

        {/* 랜드마크 카드 2 */}
        <View style={styles.landmarkCard}>
          <CustomText variant="Heading/H4" color="#0F172A" style={styles.landmarkName}>
            광교 호수공원
          </CustomText>
          <CustomText variant="Body/Small" color="#64748B" style={styles.landmarkDesc}>
            도심 속 아름다운 수변 경관과 함께 쾌적한 산책을 즐길 수 있는 가족 휴양지입니다. 야경 명소로 매우 유명합니다.
          </CustomText>
          <TouchableOpacity 
            style={styles.visitBtn}
            onPress={() => navigation.navigate('VisitCert', { placeName: '광교 호수공원' })}
            activeOpacity={0.85}
          >
            <CustomText variant="UI/Button" color="#FFFFFF" style={styles.visitBtnText}>
              방문 인증하러 가기 📸
            </CustomText>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8FAFC', // Slate-50 배경
  },
  header: {
    paddingHorizontal: 24,
    paddingTop: 30,
    paddingBottom: 20,
  },
  headerSubtitle: {
    marginTop: 6,
    fontWeight: '500',
  },
  scrollContent: {
    paddingHorizontal: 24,
    gap: 18,
    paddingBottom: 40,
  },
  landmarkCard: {
    backgroundColor: '#FFFFFF',
    padding: 22,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.03,
    shadowRadius: 12,
    elevation: 2,
  },
  landmarkName: {
    fontWeight: 'bold',
    marginBottom: 8,
  },
  landmarkDesc: {
    lineHeight: 20,
    marginBottom: 18,
    fontWeight: '400',
  },
  visitBtn: {
    height: 52,
    backgroundColor: '#3B82F6', // 브랜드 블루
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#3B82F6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 2,
  },
  visitBtnText: {
    fontWeight: 'bold',
  },
});
