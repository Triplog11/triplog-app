import React from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView } from 'react-native';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';

export default function LoginScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.brandContainer}>
        <View style={styles.logoMark}>
          <CustomText variant="Display/Large" color={theme.colors.primary} style={styles.logoIcon}>✈</CustomText>
        </View>
        <CustomText variant="Display/Medium" color={theme.colors.textPrimary} style={styles.logoText}>
          TRIP LOG
        </CustomText>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.subtitle}>
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
          <CustomText variant="UI/Button" color={theme.colors.textBody} style={styles.buttonText}>
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
    backgroundColor: theme.colors.canvas,
    justifyContent: 'space-between',
    paddingVertical: 50,
  },
  brandContainer: {
    alignItems: 'center',
    marginTop: 100,
  },
  logoMark: {
    width: 72,
    height: 72,
    borderRadius: theme.rounded.full,
    backgroundColor: theme.colors.blueTint,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: theme.spacing.base,
    borderWidth: 1.5,
    borderColor: theme.colors.primary,
  },
  logoIcon: {
    lineHeight: 72,
  },
  logoText: {
    fontWeight: '900',
    letterSpacing: 4,
    color: theme.colors.textPrimary,
  },
  subtitle: {
    marginTop: 12,
    fontWeight: '500',
  },
  buttonContainer: {
    paddingHorizontal: theme.spacing.lg,
    gap: 12,
    marginBottom: 40,
  },
  socialButton: {
    height: 56,
    borderRadius: theme.rounded.pill,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: theme.colors.textPrimary,
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
    backgroundColor: theme.colors.canvas,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  buttonText: {
    fontWeight: 'bold',
  },
});
