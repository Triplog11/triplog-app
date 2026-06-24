import React, { useState } from 'react';
import { StyleSheet, View, TouchableOpacity, SafeAreaView, ScrollView } from 'react-native';
import CustomText from '../../components/common/CustomText';

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
        <CustomText variant="Heading/H2" color="#0F172A">
          약관 동의
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
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
            color={checkedAll ? '#1D4ED8' : '#475569'} 
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
            <CustomText variant="Heading/H5" color="#1E293B" style={styles.termTitle}>
              [필수] 서비스 이용약관 동의
            </CustomText>
          </TouchableOpacity>
          <CustomText variant="Body/Small" color="#64748B" style={styles.termDescription}>
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
            <CustomText variant="Heading/H5" color="#1E293B" style={styles.termTitle}>
              [필수] 개인정보 수집 및 이용 동의
            </CustomText>
          </TouchableOpacity>
          <CustomText variant="Body/Small" color="#64748B" style={styles.termDescription}>
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
            color={isNextDisabled ? '#94A3B8' : '#FFFFFF'} 
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
    backgroundColor: '#FFFFFF', // 순백색 배경
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
    borderRadius: 14,
    backgroundColor: '#F8FAFC',
    borderWidth: 1,
    borderColor: '#E2E8F0',
    marginBottom: 20,
    gap: 12,
  },
  allAgreeBtnActive: {
    backgroundColor: '#EFF6FF',
    borderColor: '#BFDBFE',
  },
  allAgreeText: {
    fontWeight: 'bold',
  },
  circle: {
    width: 22,
    height: 22,
    borderRadius: 11,
    borderWidth: 2,
    borderColor: '#CBD5E1',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#FFFFFF',
  },
  circleActive: {
    borderColor: '#3B82F6',
    backgroundColor: '#3B82F6',
  },
  divider: {
    height: 1,
    backgroundColor: '#F1F5F9',
    marginBottom: 20,
  },
  termItem: {
    marginBottom: 20,
    backgroundColor: '#FFFFFF',
    padding: 18,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.02,
    shadowRadius: 8,
    elevation: 1,
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
    backgroundColor: '#3B82F6',
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#3B82F6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 3,
  },
  nextBtnDisabled: {
    backgroundColor: '#F1F5F9',
    shadowOpacity: 0,
    elevation: 0,
  },
  nextText: {
    fontWeight: 'bold',
  },
});
