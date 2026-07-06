import React, { useState } from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView, Alert, ActivityIndicator } from 'react-native';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';

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
      { text: '확인', onPress: () => navigation.navigate('HomeMain') }
    ]);
  };

  const isSubmitEnabled = gpsVerified && score > 0;

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color={theme.colors.textPrimary}>
          방문 인증 수행 📸
        </CustomText>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.headerSubtitle}>
          {placeName} 인증을 완료하고 보상을 획득하세요.
        </CustomText>
      </View>

      <View style={styles.content}>
        {/* Step 1: GPS 인증 */}
        <View style={styles.stepCard}>
          <View style={styles.stepBadge}>
            <CustomText variant="Label/Small" color={theme.colors.primary} style={styles.stepNum}>STEP 1</CustomText>
          </View>
          <CustomText variant="Heading/H5" color={theme.colors.textPrimary} style={styles.stepTitle}>
            GPS 위치 검증
          </CustomText>
          {verifying ? (
            <ActivityIndicator size="small" color={theme.colors.primary} style={styles.spinner} />
          ) : gpsVerified ? (
            <CustomText variant="Body/Medium" color={theme.colors.success} style={styles.verifiedText}>
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
          <View style={styles.stepBadge}>
            <CustomText variant="Label/Small" color={gpsVerified ? theme.colors.primary : theme.colors.textTertiary} style={styles.stepNum}>STEP 2</CustomText>
          </View>
          <CustomText variant="Heading/H5" color={theme.colors.textPrimary} style={styles.stepTitle}>
            만족도 평점
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
                  color={score >= star ? '#F59E0B' : theme.colors.border}
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
          style={[styles.submitBtn, !isSubmitEnabled && styles.submitBtnDisabled]}
          disabled={!isSubmitEnabled}
          onPress={handleCompleteCert}
          activeOpacity={0.9}
        >
          <CustomText
            variant="UI/Button"
            color={!isSubmitEnabled ? theme.colors.textTertiary : '#FFFFFF'}
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
    backgroundColor: theme.colors.surface,
  },
  header: {
    paddingHorizontal: theme.spacing.lg,
    paddingTop: 30,
  },
  headerSubtitle: {
    marginTop: 6,
    fontWeight: '500',
  },
  content: {
    flex: 1,
    paddingHorizontal: theme.spacing.lg,
    gap: theme.spacing.lg,
    justifyContent: 'center',
    paddingBottom: 40,
  },
  stepCard: {
    backgroundColor: theme.colors.canvas,
    padding: theme.spacing.lg,
    borderRadius: theme.rounded.lg,
    borderWidth: 1,
    borderColor: theme.colors.border,
    shadowColor: theme.colors.textPrimary,
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.03,
    shadowRadius: 12,
    elevation: 2,
  },
  disabledCard: {
    opacity: 0.45,
  },
  stepBadge: {
    backgroundColor: theme.colors.blueTint,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 3,
    borderRadius: theme.rounded.full,
    alignSelf: 'flex-start',
    marginBottom: theme.spacing.sm,
  },
  stepNum: {
    fontWeight: '700',
    letterSpacing: 0.5,
  },
  stepTitle: {
    fontWeight: 'bold',
    marginBottom: theme.spacing.base,
  },
  actionBtn: {
    height: 52,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.pill,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: theme.colors.primary,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
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
    padding: theme.spacing.lg,
  },
  submitBtn: {
    height: 56,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.pill,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: theme.colors.primary,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 8,
    elevation: 3,
  },
  submitBtnDisabled: {
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    shadowOpacity: 0,
    elevation: 0,
  },
  submitBtnText: {
    fontWeight: 'bold',
  },
});
