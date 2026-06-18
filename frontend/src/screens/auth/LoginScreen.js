import React from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';

export default function LoginScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.brandContainer}>
        {/* 세련된 로고 디자인 */}
        <CustomText variant="Display/Medium" color="#3B82F6" style={styles.logoText}>
          TRIP LOG
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.subtitle}>
          여행 기록 및 방문 인증 게이미피케이션
        </CustomText>
      </View>

      <View style={styles.buttonContainer}>
        {/* 카카오 로그인 */}
        <TouchableOpacity 
          style={[styles.socialButton, styles.kakaoButton]} 
          onPress={() => navigation.navigate('Terms')}
          activeOpacity={0.85}
        >
          <CustomText variant="UI/Button" color="#3A1D1D" style={styles.buttonText}>
            카카오로 시작하기
          </CustomText>
        </TouchableOpacity>

        {/* 네이버 로그인 */}
        <TouchableOpacity 
          style={[styles.socialButton, styles.naverButton]} 
          onPress={() => navigation.navigate('Terms')}
          activeOpacity={0.85}
        >
          <CustomText variant="UI/Button" color="#FFFFFF" style={styles.buttonText}>
            네이버로 시작하기
          </CustomText>
        </TouchableOpacity>

        {/* 구글 로그인 */}
        <TouchableOpacity 
          style={[styles.socialButton, styles.googleButton]} 
          onPress={() => navigation.navigate('Terms')}
          activeOpacity={0.85}
        >
          <CustomText variant="UI/Button" color="#1E293B" style={styles.buttonText}>
            구글로 시작하기
          </CustomText>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF', // 순백색 배경
    justifyContent: 'space-between',
    paddingVertical: 50,
  },
  brandContainer: {
    alignItems: 'center',
    marginTop: 120,
  },
  logoText: {
    fontWeight: '900',
    letterSpacing: 3,
  },
  subtitle: {
    marginTop: 12,
    fontWeight: '500',
  },
  buttonContainer: {
    paddingHorizontal: 24,
    gap: 14,
    marginBottom: 40,
  },
  socialButton: {
    height: 56,
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.05,
    shadowRadius: 12,
    elevation: 3,
  },
  kakaoButton: {
    backgroundColor: '#FEE500',
  },
  naverButton: {
    backgroundColor: '#03C75A',
  },
  googleButton: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  buttonText: {
    fontWeight: 'bold',
  },
});
