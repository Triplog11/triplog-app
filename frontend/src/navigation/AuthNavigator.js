import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from '../screens/auth/LoginScreen';
import TermsScreen from '../screens/auth/TermsScreen';
import NicknameScreen from '../screens/auth/NicknameScreen';

const Stack = createNativeStackNavigator();

export default function AuthNavigator() {
  return (
    <Stack.Navigator 
      screenOptions={{
        headerStyle: { backgroundColor: '#0F172A' },
        headerTintColor: '#F8FAFC',
        headerTitleStyle: { fontWeight: 'bold' },
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
    </Stack.Navigator>
  );
}
