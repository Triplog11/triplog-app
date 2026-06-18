import React, { useState } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView, Alert, ActivityIndicator } from 'react-native';

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
      { text: '확인', onPress: () => navigation.navigate('Home') }
    ]);
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>방문 인증 수행 📸</Text>
        <Text style={styles.subtitle}>{placeName} 인증을 완료하고 보상을 획득하세요.</Text>
      </View>

      <View style={styles.content}>
        {/* Step 1: GPS 인증 */}
        <View style={styles.stepCard}>
          <Text style={styles.stepTitle}>Step 1. GPS 위치 검증</Text>
          {verifying ? (
            <ActivityIndicator size="small" color="#38BDF8" style={styles.spinner} />
          ) : gpsVerified ? (
            <Text style={styles.verifiedText}>✅ GPS 위치 확인 완료</Text>
          ) : (
            <TouchableOpacity style={styles.actionBtn} onPress={handleGPSVerify}>
              <Text style={styles.actionBtnText}>현재 위치 확인하기 📍</Text>
            </TouchableOpacity>
          )}
        </View>

        {/* Step 2: 평점 만족도 입력 */}
        <View style={[styles.stepCard, !gpsVerified && styles.disabledCard]}>
          <Text style={styles.stepTitle}>Step 2. 만족도 평점</Text>
          <View style={styles.starRow}>
            {[1, 2, 3, 4, 5].map((star) => (
              <TouchableOpacity 
                key={star} 
                disabled={!gpsVerified}
                onPress={() => setScore(star)}
              >
                <Text style={[styles.starText, score >= star && styles.starActive]}>★</Text>
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
        >
          <Text style={styles.submitBtnText}>최종 인증 및 포인트 획득</Text>
        </TouchableOpacity>
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
  content: {
    flex: 1,
    paddingHorizontal: 24,
    gap: 16,
    justifyContent: 'center',
  },
  stepCard: {
    backgroundColor: '#1E293B',
    padding: 24,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#334155',
  },
  disabledCard: {
    opacity: 0.4,
  },
  stepTitle: {
    color: '#F8FAFC',
    fontWeight: 'bold',
    fontSize: 16,
    marginBottom: 16,
  },
  actionBtn: {
    height: 48,
    backgroundColor: '#38BDF8',
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
  },
  actionBtnText: {
    color: '#0F172A',
    fontWeight: 'bold',
    fontSize: 14,
  },
  spinner: {
    marginVertical: 12,
  },
  verifiedText: {
    color: '#4ADE80',
    fontWeight: 'bold',
    fontSize: 15,
    textAlign: 'center',
    marginVertical: 10,
  },
  starRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 16,
  },
  starText: {
    fontSize: 36,
    color: '#334155',
  },
  starActive: {
    color: '#F59E0B',
  },
  footer: {
    padding: 24,
  },
  submitBtn: {
    height: 56,
    backgroundColor: '#10B981',
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  submitBtnDisabled: {
    backgroundColor: '#334155',
  },
  submitBtnText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 16,
  },
});
