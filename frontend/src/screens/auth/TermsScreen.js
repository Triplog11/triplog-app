import React, { useState } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView, ScrollView } from 'react-native';

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

  const isNextDisabled = !checkedTerms || !checkedPrivacy;

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>약관 동ের</Text>
        <Text style={styles.headerSubtitle}>트립로그 서비스를 이용하기 위해 동의가 필요합니다.</Text>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        <TouchableOpacity style={styles.allAgreeBtn} onPress={handleToggleAll}>
          <Text style={styles.allAgreeText}>
            {checkedAll ? '☑️ 전체 약관에 동의합니다' : '☐ 전체 약관에 동의합니다'}
          </Text>
        </TouchableOpacity>

        <View style={styles.divider} />

        <View style={styles.termItem}>
          <TouchableOpacity onPress={() => setCheckedTerms(!checkedTerms)}>
            <Text style={styles.termTitle}>
              {checkedTerms ? '☑️ [필수] 서비스 이용약관 동의' : '☐ [필수] 서비스 이용약관 동의'}
            </Text>
          </TouchableOpacity>
          <Text style={styles.termDescription}>
            서비스 이용규칙 및 권리 의무 사항을 명시합니다...
          </Text>
        </View>

        <View style={styles.termItem}>
          <TouchableOpacity onPress={() => setCheckedPrivacy(!checkedPrivacy)}>
            <Text style={styles.termTitle}>
              {checkedPrivacy ? '☑️ [필수] 개인정보 수집 및 이용 동의' : '☐ [필수] 개인정보 수집 및 이용 동의'}
            </Text>
          </TouchableOpacity>
          <Text style={styles.termDescription}>
            로그인, 계정 정보 보관 등을 위해 필요한 조치입니다...
          </Text>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity 
          style={[styles.nextBtn, isNextDisabled && styles.nextBtnDisabled]} 
          disabled={isNextDisabled}
          onPress={() => navigation.navigate('Nickname')}
        >
          <Text style={styles.nextText}>동의하고 다음으로</Text>
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
    marginTop: 20,
  },
  headerTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#F8FAFC',
  },
  headerSubtitle: {
    fontSize: 14,
    color: '#94A3B8',
    marginTop: 8,
  },
  content: {
    paddingHorizontal: 24,
  },
  allAgreeBtn: {
    paddingVertical: 16,
    borderRadius: 12,
    backgroundColor: '#1E293B',
    paddingHorizontal: 16,
    marginBottom: 20,
  },
  allAgreeText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#38BDF8',
  },
  divider: {
    height: 1,
    backgroundColor: '#334155',
    marginBottom: 20,
  },
  termItem: {
    marginBottom: 24,
    backgroundColor: '#1E293B',
    padding: 16,
    borderRadius: 12,
  },
  termTitle: {
    fontSize: 15,
    fontWeight: 'bold',
    color: '#F1F5F9',
  },
  termDescription: {
    fontSize: 12,
    color: '#64748B',
    marginTop: 10,
    lineHeight: 18,
  },
  footer: {
    padding: 24,
  },
  nextBtn: {
    height: 56,
    backgroundColor: '#38BDF8',
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  nextBtnDisabled: {
    backgroundColor: '#334155',
  },
  nextText: {
    color: '#0F172A',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
