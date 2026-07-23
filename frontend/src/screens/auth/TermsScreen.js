import React, { useState } from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView, ScrollView } from 'react-native';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';

export default function TermsScreen({ navigation }) {
  const [checkedAll, setCheckedAll] = useState(false);
  const [checkedTerms, setCheckedTerms] = useState(false);
  const [checkedPrivacy, setCheckedPrivacy] = useState(false);

  const handleToggleAll = () => {
    const nextVal = !checkedAll;
    setCheckedAll(nextVal);
    setCheckedTerms(nextVal);
    setCheckedPrivacy(nextVal);
  };

  const handleToggleTerm = () => {
    const nextTerms = !checkedTerms;
    setCheckedTerms(nextTerms);
    if (!nextTerms) {
      setCheckedAll(false);
    } else if (checkedPrivacy) {
      setCheckedAll(true);
    }
  };

  const handleTogglePrivacy = () => {
    const nextPrivacy = !checkedPrivacy;
    setCheckedPrivacy(nextPrivacy);
    if (!nextPrivacy) {
      setCheckedAll(false);
    } else if (checkedTerms) {
      setCheckedAll(true);
    }
  };

  const isNextDisabled = !checkedTerms || !checkedPrivacy;

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color={theme.colors.text}>
          약관 동의
        </CustomText>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.headerSubtitle}>
          트립로그 서비스를 이용하기 위해 동의가 필요합니다.
        </CustomText>
      </View>

      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        {/* 전체동의 카드 */}
        <TouchableOpacity 
          style={[styles.allAgreeBtn, checkedAll && styles.allAgreeBtnActive]} 
          onPress={handleToggleAll}
          activeOpacity={0.8}
        >
          <View style={[styles.circle, checkedAll && styles.circleActive]}>
            {checkedAll && <CustomText variant="Label/Small" color="#FFFFFF">✓</CustomText>}
          </View>
          <CustomText
            variant="UI/Button"
            color={checkedAll ? theme.colors.primaryDark : theme.colors.textSecondary}
            style={styles.allAgreeText}
          >
            전체 약관에 동의합니다
          </CustomText>
        </TouchableOpacity>

        <View style={styles.divider} />

        {/* 이용약관 */}
        <View style={styles.termItem}>
          <TouchableOpacity
            style={styles.termTitleRow}
            onPress={handleToggleTerm}
            activeOpacity={0.7}
          >
            <View style={[styles.circle, checkedTerms && styles.circleActive]}>
              {checkedTerms && <CustomText variant="Label/Small" color="#FFFFFF">✓</CustomText>}
            </View>
            <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.termTitle}>
              [필수] 서비스 이용약관 동의
            </CustomText>
          </TouchableOpacity>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.termDescription}>
            트립로그 서비스 이용규칙 및 회원과 회사 간의 권리 의무 사항을 투명하게 규정합니다. 즐거운 여행 인증 서비스를 즐겨보세요!
          </CustomText>
        </View>

        {/* 개인정보 처리방침 */}
        <View style={styles.termItem}>
          <TouchableOpacity
            style={styles.termTitleRow}
            onPress={handleTogglePrivacy}
            activeOpacity={0.7}
          >
            <View style={[styles.circle, checkedPrivacy && styles.circleActive]}>
              {checkedPrivacy && <CustomText variant="Label/Small" color="#FFFFFF">✓</CustomText>}
            </View>
            <CustomText variant="Heading/H5" color={theme.colors.text} style={styles.termTitle}>
              [필수] 개인정보 수집 및 이용 동의
            </CustomText>
          </TouchableOpacity>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.termDescription}>
            소셜 로그인 연동, 경험치 보관 및 뱃지 발급 등 개인화된 서비스 제공을 위해 최소한의 이메일 및 닉네임 정보를 수집합니다.
          </CustomText>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          style={[styles.nextBtn, isNextDisabled && styles.nextBtnDisabled]}
          disabled={isNextDisabled}
          onPress={() => navigation.navigate('Nickname')}
          activeOpacity={0.9}
        >
          <CustomText
            variant="UI/Button"
            color={isNextDisabled ? theme.colors.textMuted : '#FFFFFF'}
            style={styles.nextText}
          >
            동의하고 다음으로
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
  },
  header: {
    paddingHorizontal: 24,
    paddingTop: 30,
    paddingBottom: 20,
  },
  headerSubtitle: {
    marginTop: 8,
    fontWeight: '500',
  },
  content: {
    paddingHorizontal: 24,
  },
  allAgreeBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 16,
    paddingHorizontal: 16,
    borderRadius: theme.rounded.card,
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    marginBottom: 20,
    gap: 12,
  },
  allAgreeBtnActive: {
    backgroundColor: theme.colors.primarySoft,
    borderColor: theme.colors.primary,
  },
  allAgreeText: {
    fontWeight: 'bold',
  },
  circle: {
    width: 22,
    height: 22,
    borderRadius: 11,
    borderWidth: 2,
    borderColor: theme.colors.borderStrong,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: theme.colors.white,
  },
  circleActive: {
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primary,
  },
  divider: {
    height: 1,
    backgroundColor: theme.colors.surfaceDim,
    marginBottom: 20,
  },
  termItem: {
    marginBottom: 20,
    backgroundColor: theme.colors.white,
    padding: 18,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  termTitleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  termTitle: {
    fontWeight: 'bold',
  },
  termDescription: {
    marginTop: 10,
    lineHeight: 18,
    fontWeight: '400',
  },
  footer: {
    padding: 24,
  },
  nextBtn: {
    height: 56,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.cta,
    justifyContent: 'center',
    alignItems: 'center',
  },
  nextBtnDisabled: {
    backgroundColor: theme.colors.surfaceDim,
    shadowOpacity: 0,
    elevation: 0,
  },
  nextText: {
    fontWeight: 'bold',
  },
});
