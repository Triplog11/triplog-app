import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { AuthProvider, useAuth, AUTH_STATUS } from '../context/AuthContext';
import AuthNavigator from './AuthNavigator';
import TabNavigator from './TabNavigator';

function NavigationWrapper() {
  const { status } = useAuth();

  // 토큰 복원 중에는 스플래시 화면이 유지되도록 아무것도 렌더하지 않는다
  if (status === AUTH_STATUS.BOOTSTRAPPING) {
    return null;
  }

  return (
    <NavigationContainer>
      {status === AUTH_STATUS.LOGGED_IN ? (
        <TabNavigator />
      ) : (
        <AuthNavigator />
      )}
    </NavigationContainer>
  );
}

export default function AppNavigator() {
  return (
    <AuthProvider>
      <NavigationWrapper />
    </AuthProvider>
  );
}
