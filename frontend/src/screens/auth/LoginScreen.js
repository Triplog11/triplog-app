import React from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView } from 'react-native';

export default function LoginScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.brandContainer}>
        <Text style={styles.logoText}>TRIP LOG</Text>
        <Text style={styles.subtitle}>여행 기록 및 방문 인증 게이미피케이션</Text>
      </View>

      <View style={styles.buttonContainer}>
        <TouchableOpacity 
          style={[styles.socialButton, styles.kakaoButton]} 
          onPress={() => navigation.navigate('Terms')}
        >
          <Text style={styles.kakaoText}>카카오로 시작하기</Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={[styles.socialButton, styles.naverButton]} 
          onPress={() => navigation.navigate('Terms')}
        >
          <Text style={styles.naverText}>네이버로 시작하기</Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={[styles.socialButton, styles.googleButton]} 
          onPress={() => navigation.navigate('Terms')}
        >
          <Text style={styles.googleText}>구글로 시작하기</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#1E293B', // Sleek dark slate
    justifyContent: 'space-between',
    paddingVertical: 50,
  },
  brandContainer: {
    alignItems: 'center',
    marginTop: 80,
  },
  logoText: {
    fontSize: 42,
    fontWeight: '900',
    color: '#38BDF8', // Sky blue
    letterSpacing: 2,
  },
  subtitle: {
    fontSize: 14,
    color: '#94A3B8',
    marginTop: 12,
  },
  buttonContainer: {
    paddingHorizontal: 24,
    gap: 12,
    marginBottom: 40,
  },
  socialButton: {
    height: 56,
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 3,
  },
  kakaoButton: {
    backgroundColor: '#FEE500',
  },
  kakaoText: {
    color: '#3A1D1D',
    fontWeight: 'bold',
    fontSize: 16,
  },
  naverButton: {
    backgroundColor: '#03C75A',
  },
  naverText: {
    color: '#FFFFFF',
    fontWeight: 'bold',
    fontSize: 16,
  },
  googleButton: {
    backgroundColor: '#FFFFFF',
    borderWidth: 1,
    borderColor: '#E2E8F0',
  },
  googleText: {
    color: '#1E293B',
    fontWeight: 'bold',
    fontSize: 16,
  },
});
