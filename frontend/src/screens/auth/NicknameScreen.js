import React, { useState } from 'react';
import { StyleSheet, View, TextInput, TouchableOpacity, SafeAreaView, Alert } from 'react-native';
import { useAuth } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';

export default function NicknameScreen() {
  const [nickname, setNickname] = useState('');
  const [checked, setChecked] = useState(false);
  const { login } = useAuth(); // AuthContext의 로그인 액션 호출

  const handleCheckDuplicate = () => {
    if (!nickname.trim()) {
      Alert.alert('알림', '닉네임을 입력해 주세요.');
      return;
    }
    setChecked(true);
    Alert.alert('성공', '사용 가능한 닉네임입니다!');
  };

  const handleComplete = () => {
    if (!checked) {
      Alert.alert('알림', '닉네임 중복 체크가 필요합니다.');
      return;
    }
    login(); // 가입 완료 후 로그인 탭 네비게이터로 이동
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <CustomText variant="Heading/H2" color="#0F172A">
          닉네임 설정
        </CustomText>
        <CustomText variant="Body/Small" color="#64748B" style={styles.headerSubtitle}>
          트립로그에서 사용할 멋진 이름을 정해주세요.
        </CustomText>
      </View>

      <View style={styles.content}>
        <CustomText variant="Label/Medium" color="#475569" style={styles.label}>
          닉네임
        </CustomText>
        <View style={styles.inputContainer}>
          <TextInput 
            style={styles.input}
            placeholder="한글, 영문, 숫자 조합 2~10자"
            placeholderTextColor="#94A3B8"
            value={nickname}
            onChangeText={(text) => {
              setNickname(text);
              setChecked(false);
            }}
          />
          <TouchableOpacity 
            style={styles.checkBtn} 
            onPress={handleCheckDuplicate}
            activeOpacity={0.8}
          >
            <CustomText variant="UI/Button/Small" color="#FFFFFF" style={styles.checkBtnText}>
              중복확인
            </CustomText>
          </TouchableOpacity>
        </View>
        {checked && (
          <CustomText variant="Body/Small" color="#10B981" style={styles.successText}>
            ✓ 사용 가능한 닉네임입니다.
          </CustomText>
        )}
      </View>

      <View style={styles.footer}>
        <TouchableOpacity 
          style={[styles.completeBtn, !checked && styles.completeBtnDisabled]} 
          disabled={!checked}
          onPress={handleComplete}
          activeOpacity={0.9}
        >
          <CustomText 
            variant="UI/Button" 
            color={checked ? '#FFFFFF' : '#94A3B8'} 
            style={styles.completeText}
          >
            가입 완료
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
    flex: 1,
    justifyContent: 'center',
    paddingBottom: 80, // 입력 영역이 살짝 위쪽으로 오도록 배치
  },
  label: {
    marginBottom: 8,
    fontWeight: '600',
  },
  inputContainer: {
    flexDirection: 'row',
    gap: 8,
  },
  input: {
    flex: 1,
    height: 56,
    backgroundColor: '#F8FAFC', // Slate-50 인풋 배경
    borderRadius: 16,
    paddingHorizontal: 16,
    color: '#0F172A',
    fontSize: 15,
    fontFamily: 'Pretendard-Regular',
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  checkBtn: {
    width: 100,
    backgroundColor: '#3B82F6',
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#3B82F6',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 2,
  },
  checkBtnText: {
    fontWeight: 'bold',
  },
  successText: {
    fontSize: 13,
    marginTop: 10,
    fontWeight: '500',
  },
  footer: {
    padding: 24,
  },
  completeBtn: {
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
  completeBtnDisabled: {
    backgroundColor: '#F1F5F9',
    shadowOpacity: 0,
    elevation: 0,
  },
  completeText: {
    fontWeight: 'bold',
  },
});
