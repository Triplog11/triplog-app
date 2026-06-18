import React, { useState } from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView, Alert, ActivityIndicator } from 'react-native';
import CustomText from '../../components/common/CustomText';

export default function VisitCertScreen({ route, navigation }) {
  const { placeName = '선택된 장소' } = route.params || {};
  const [gpsVerified, setGpsVerified] = useState(false);
  const [verifying, setVerifying] = useState(false);
  const [score, setScore] = useState(0);

  const handleGPSVerify = () => {
    setVerifying(true);
    setTimeout(() => {
      setVerifying(false);
      setGpsVerified(true);
      Alert.alert('인증 성공', '현재 위치가 일치하여 GPS 인증에 성공했습니다! 📍');
    }, 2000);
  };

  const handleCompleteCert = () => {
    if (!gpsVerified) {
      Alert.alert('경고', 'GPS 인증을 먼저 완료해 주세요.');
      return;
    }
    Alert.alert('축하합니다!', `${placeName} 인증 완료! 🏆 +150 XP를 획득했습니다.`, [
      { text: '확인', onPress: () => navigation.navigate('HomeMain') } // Home 메인 탭으로 롤백
    ]);
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color="#0F172A">
          방문 인증 수행 📸
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
          {placeName} 인증을 완료하고 보상을 획득하세요.
        </CustomText>
      </View>

      <View style={styles.content}>
        {/* Step 1: GPS 인증 */}
        <View style={styles.stepCard}>
          <CustomText variant="Heading/H5" color="#0F172A" style={styles.stepTitle}>
            Step 1. GPS 위치 검증
          </CustomText>
          {verifying ? (
            <ActivityIndicator size="small" color="#3B82F6" style={styles.spinner} />
          ) : gpsVerified ? (
            <CustomText variant="Body/Medium" color="#10B981" style={styles.verifiedText}>
              ✓ GPS 위치 확인 완료
            </CustomText>
          ) : (
            <TouchableOpacity 
              style={styles.actionBtn} 
              onPress={handleGPSVerify}
              activeOpacity={0.8}
            >
              <CustomText variant="UI/Button" color="#FFFFFF" style={styles.actionBtnText}>
                현재 위치 확인하기 📍
              </CustomText>
            </TouchableOpacity>
          )}
        </View>

        {/* Step 2: 평점 만족도 입력 */}
        <View style={[styles.stepCard, !gpsVerified && styles.disabledCard]}>
          <CustomText variant="Heading/H5" color="#0F172A" style={styles.stepTitle}>
            Step 2. 만족도 평점
          </CustomText>
          <View style={styles.starRow}>
            {[1, 2, 3, 4, 5].map((star) => (
              <TouchableOpacity 
                key={star} 
                disabled={!gpsVerified}
                onPress={() => setScore(star)}
                activeOpacity={0.7}
              >
                <CustomText 
                  variant="Display/Small" 
                  color={score >= star ? '#F59E0B' : '#E2E8F0'}
                  style={styles.starText}
                >
                  ★
                </CustomText>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>

      <View style={styles.footer}>
        <TouchableOpacity 
          style={[styles.submitBtn, (!gpsVerified || score === 0) && styles.submitBtnDisabled]}
          disabled={!gpsVerified || score === 0}
          onPress={handleCompleteCert}
          activeOpacity={0.9}
        >
          <CustomText 
            variant="UI/Button" 
            color={(!gpsVerified || score === 0) ? '#94A3B8' : '#FFFFFF'} 
            style={styles.submitBtnText}
          >
            최종 인증 및 포인트 획득
          </CustomText>
        </TouchableOpacity>
      </View>
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
  },
  headerSubtitle: {
    marginTop: 6,
    fontWeight: '500',
  },
  content: {
    flex: 1,
    paddingHorizontal: 24,
    gap: 20,
    justifyContent: 'center',
    paddingBottom: 40,
  },
  stepCard: {
    backgroundColor: '#FFFFFF',
    padding: 24,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.03,
    shadowRadius: 12,
    elevation: 2,
  },
  disabledCard: {
    opacity: 0.45,
  },
  stepTitle: {
    fontWeight: 'bold',
    marginBottom: 16,
  },
  actionBtn: {
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
  actionBtnText: {
    fontWeight: 'bold',
  },
  spinner: {
    marginVertical: 14,
  },
  verifiedText: {
    fontWeight: 'bold',
    textAlign: 'center',
    marginVertical: 12,
  },
  starRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 12,
  },
  starText: {
    lineHeight: 40,
  },
  footer: {
    padding: 24,
  },
  submitBtn: {
    height: 56,
    backgroundColor: '#10B981', // 인증 성공 초록색
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#10B981',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 3,
  },
  submitBtnDisabled: {
    backgroundColor: '#F1F5F9',
    shadowOpacity: 0,
    elevation: 0,
  },
  submitBtnText: {
    fontWeight: 'bold',
  },
});
