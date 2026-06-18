import React, { useState } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, SafeAreaView, Alert } from 'react-native';
import { useAuth } from '../../context/AuthContext';

export default function NicknameScreen() {
  const [nickname, setNickname] = useState('');
  const [checked, setChecked] = useState(false);
  const { login } = useAuth(); // 분리된 AuthContext 훅 사용

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
    login(); // 로그인 완료 처리
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>닉네임 설정</Text>
        <Text style={styles.headerSubtitle}>트립로그에서 사용할 멋진 이름을 정해주세요.</Text>
      </View>

      <View style={styles.content}>
        <Text style={styles.label}>닉네임</Text>
        <View style={styles.inputContainer}>
          <TextInput 
            style={styles.input}
            placeholder="한글, 영문, 숫자 조합 2~10자"
            placeholderTextColor="#64748B"
            value={nickname}
            onChangeText={(text) => {
              setNickname(text);
              setChecked(false);
            }}
          />
          <TouchableOpacity style={styles.checkBtn} onPress={handleCheckDuplicate}>
            <Text style={styles.checkBtnText}>중복확인</Text>
          </TouchableOpacity>
        </View>
        {checked && <Text style={styles.successText}>사용 가능한 닉네임입니다.</Text>}
      </View>

      <View style={styles.footer}>
        <TouchableOpacity 
          style={[styles.completeBtn, !checked && styles.completeBtnDisabled]} 
          disabled={!checked}
          onPress={handleComplete}
        >
          <Text style={styles.completeText}>가입 완료</Text>
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
    flex: 1,
    justifyContent: 'center',
  },
  label: {
    color: '#94A3B8',
    fontSize: 14,
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
    backgroundColor: '#1E293B',
    borderRadius: 16,
    paddingHorizontal: 16,
    color: '#F8FAFC',
    fontSize: 16,
  },
  checkBtn: {
    width: 100,
    backgroundColor: '#38BDF8',
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  checkBtnText: {
    color: '#0F172A',
    fontWeight: 'bold',
    fontSize: 14,
  },
  successText: {
    color: '#4ADE80',
    fontSize: 13,
    marginTop: 10,
  },
  footer: {
    padding: 24,
  },
  completeBtn: {
    height: 56,
    backgroundColor: '#38BDF8',
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  completeBtnDisabled: {
    backgroundColor: '#334155',
  },
  completeText: {
    color: '#0F172A',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
