import React, { useState } from 'react';
import { StyleSheet, View, TouchableOpacity, Alert, Image, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { useAuth, AUTH_STATUS } from '../../context/AuthContext';
import { getNaverAuthCode } from '../../services/oauth/naver';
import { getGoogleAuthCode } from '../../services/oauth/google';

/** 네이버 브랜드 가이드 지정색 — 소셜 버튼에만 사용 (서비스 팔레트 아님) */
const NAVER_GREEN = '#03C75A';

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

  const busy = !!loadingProvider;

  return (
    <SafeAreaView style={styles.container} edges={['top', 'bottom']}>
      {/* 브랜드 */}
      <View style={styles.brand}>
        <View style={styles.logoTile}>
          <Image
            source={require('../../../assets/logo-glyph.png')}
            style={styles.logoGlyph}
            resizeMode="contain"
          />
        </View>
        <CustomText variant="Display/Medium" color={theme.colors.text} style={styles.wordmark}>
          TRIP LOG
        </CustomText>
        <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.tagline}>
          발 닿은 곳이 기록이 되고, 기록이 도감이 돼요
        </CustomText>
      </View>

      {/* 로그인 */}
      <View style={styles.footer}>
        <TouchableOpacity
          style={[styles.socialBtn, styles.naverBtn, busy && styles.btnBusy]}
          onPress={() => handleSocialLogin('NAVER', getNaverAuthCode)}
          disabled={busy}
          activeOpacity={0.9}
        >
          {loadingProvider === 'NAVER' ? (
            <ActivityIndicator size="small" color="#FFFFFF" />
          ) : (
            <>
              <View style={styles.iconSlot}>
                <CustomText variant="UI/Button" color="#FFFFFF" style={styles.naverMark}>
                  N
                </CustomText>
              </View>
              <CustomText variant="UI/Button" color="#FFFFFF" style={styles.btnLabel}>
                네이버로 시작하기
              </CustomText>
            </>
          )}
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.socialBtn, styles.googleBtn, busy && styles.btnBusy]}
          onPress={() => handleSocialLogin('GOOGLE', getGoogleAuthCode)}
          disabled={busy}
          activeOpacity={0.9}
        >
          {loadingProvider === 'GOOGLE' ? (
            <ActivityIndicator size="small" color={theme.colors.textSecondary} />
          ) : (
            <>
              <View style={styles.iconSlot}>
                <Ionicons name="logo-google" size={18} color={theme.colors.textBody} />
              </View>
              <CustomText variant="UI/Button" color={theme.colors.textBody} style={styles.btnLabel}>
                구글로 시작하기
              </CustomText>
            </>
          )}
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.socialBtn, styles.emailBtn, busy && styles.btnBusy]}
          onPress={() => navigation.navigate('EmailLogin')}
          disabled={busy}
          activeOpacity={0.8}
        >
          <View style={styles.iconSlot}>
            <Ionicons name="mail-outline" size={18} color={theme.colors.primary} />
          </View>
          <CustomText variant="UI/Button" color={theme.colors.primary} style={styles.btnLabel}>
            이메일로 계속하기
          </CustomText>
        </TouchableOpacity>

        <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.notice}>
          로그인하면 이용약관과 개인정보 처리방침에 동의하게 됩니다.
        </CustomText>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.canvas,
    justifyContent: 'space-between',
  },
  brand: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: theme.spacing.xl,
  },
  logoTile: {
    width: 104,
    height: 104,
    borderRadius: 30,
    backgroundColor: theme.colors.logoTeal,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: theme.spacing.lg,
  },
  logoGlyph: {
    width: 52,
    height: 62,
  },
  wordmark: {
    fontWeight: '900',
    letterSpacing: 4,
  },
  tagline: {
    marginTop: theme.spacing.md,
    textAlign: 'center',
  },
  footer: {
    paddingHorizontal: theme.spacing.lg,
    paddingBottom: theme.spacing.xl,
    gap: theme.spacing.md,
  },
  socialBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    height: 56,
    borderRadius: theme.rounded.cta,
  },
  naverBtn: {
    backgroundColor: NAVER_GREEN,
  },
  googleBtn: {
    backgroundColor: theme.colors.canvas,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  emailBtn: {
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: theme.colors.primary,
  },
  btnBusy: {
    opacity: 0.6,
  },
  iconSlot: {
    width: 22,
    alignItems: 'center',
    marginRight: theme.spacing.sm,
  },
  naverMark: {
    fontWeight: '900',
  },
  btnLabel: {
    fontWeight: 'bold',
  },
  notice: {
    textAlign: 'center',
    marginTop: theme.spacing.xs,
  },
});
