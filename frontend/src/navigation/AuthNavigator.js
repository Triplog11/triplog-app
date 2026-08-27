import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../screens/auth/LoginScreen';
import TermsScreen from '../screens/auth/TermsScreen';
import NicknameScreen from '../screens/auth/NicknameScreen';
import EmailLoginScreen from '../screens/auth/EmailLoginScreen';
import EmailSignupScreen from '../screens/auth/EmailSignupScreen';

const Stack = createNativeStackNavigator();

export default function AuthNavigator() {
  return (
    <Stack.Navigator 
      screenOptions={{
        headerStyle: { backgroundColor: '#FFFFFF' },
        headerTintColor: '#0F172A',
        headerTitleStyle: { fontFamily: 'Pretendard-Bold', fontWeight: '700' },
        headerShadowVisible: false,
      }}
    >
      <Stack.Screen 
        name="Login" 
        component={LoginScreen} 
        options={{ headerShown: false }} 
      />
      <Stack.Screen 
        name="Terms" 
        component={TermsScreen} 
        options={{ title: '약관 동의' }} 
      />
      <Stack.Screen 
        name="Nickname" 
        component={NicknameScreen} 
        options={{ title: '닉네임 설정' }}
      />
      <Stack.Screen
        name="EmailLogin"
        component={EmailLoginScreen}
        options={{ title: '이메일 로그인' }}
      />
      <Stack.Screen
        name="EmailSignup"
        component={EmailSignupScreen}
        options={{ title: '회원가입' }}
      />
    </Stack.Navigator>
  );
}
