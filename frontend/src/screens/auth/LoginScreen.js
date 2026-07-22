import React, { useState } from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView, Alert, Image } from 'react-native';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { useAuth, AUTH_STATUS } from '../../context/AuthContext';
import { getNaverAuthCode } from '../../services/oauth/naver';
import { getGoogleAuthCode } from '../../services/oauth/google';

export default function LoginScreen({ navigation }) {
  const { signInWithProvider } = useAuth();
  const [loadingProvider, setLoadingProvider] = useState(null);

  const handleSocialLogin = async (providerName, getAuthCode) => {
    if (loadingProvider) return;
    setLoadingProvider(providerName);
    try {
      const authResult = await getAuthCode();
      if (!authResult) return; // 사용자가 로그인 창을 닫음

      const nextStatus = await signInWithProvider(authResult);
      if (nextStatus === AUTH_STATUS.NEEDS_ADDITIONAL_INFO) {
        navigation.navigate('Terms');
      }
      // LOGGED_IN이면 AppNavigator가 자동으로 메인 화면으로 전환
    } catch (error) {
      Alert.alert('로그인', error.message);
    } finally {
      setLoadingProvider(null);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.brandContainer}>
        <Image
          source={require('../../../assets/logo.png')}
          style={styles.logoImage}
          resizeMode="contain"
        />
        <CustomText variant="Display/Medium" color={theme.colors.textPrimary} style={styles.logoText}>
          TRIP LOG
        </CustomText>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.subtitle}>
          여행 기록 및 방문 인증 게이미피케이션
        </CustomText>
      </View>

      <View style={styles.buttonContainer}>
        {/* 네이버 로그인 */}
        <TouchableOpacity
          style={[styles.socialButton, styles.naverButton, loadingProvider && styles.socialButtonDisabled]}
          onPress={() => handleSocialLogin('NAVER', getNaverAuthCode)}
          disabled={!!loadingProvider}
          activeOpacity={0.85}
        >
          <CustomText variant="UI/Button" color="#FFFFFF" style={styles.buttonText}>
            네이버로 시작하기
          </CustomText>
        </TouchableOpacity>

        {/* 구글 로그인 */}
        <TouchableOpacity
          style={[styles.socialButton, styles.googleButton, loadingProvider && styles.socialButtonDisabled]}
          onPress={() => handleSocialLogin('GOOGLE', getGoogleAuthCode)}
          disabled={!!loadingProvider}
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
  logoImage: {
    width: 112,
    height: 112,
    marginBottom: theme.spacing.base,
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
  socialButtonDisabled: {
    opacity: 0.5,
  },
  socialButton: {
    height: 56,
    borderRadius: theme.rounded.pill,
    justifyContent: 'center',
    alignItems: 'center',
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
