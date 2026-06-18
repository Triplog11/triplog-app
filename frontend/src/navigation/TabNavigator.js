import React from 'react';
import { StyleSheet, Platform } from 'react-native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Feather } from '@expo/vector-icons';

// Screens
import HomeScreen from '../screens/home/HomeScreen';
import HistoryScreen from '../screens/home/HistoryScreen';
import MapScreen from '../screens/map/MapScreen';
import DetailScreen from '../screens/map/DetailScreen';
import VisitCertScreen from '../screens/record/VisitCertScreen';
import CommunityScreen from '../screens/social/CommunityScreen';
import RankingScreen from '../screens/social/RankingScreen';
import MyPageScreen from '../screens/mypage/MyPageScreen';
import BadgeListScreen from '../screens/mypage/BadgeListScreen';

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

// 각 탭별 스택 네비게이터 정의
function HomeStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="HomeMain" component={HomeScreen} options={{ headerShown: false }} />
      <Stack.Screen name="History" component={HistoryScreen} options={{ title: '인증 히스토리' }} />
    </Stack.Navigator>
  );
}

function MapStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="MapMain" component={MapScreen} options={{ headerShown: false }} />
      <Stack.Screen name="Detail" component={DetailScreen} options={{ title: '지역 상세' }} />
      <Stack.Screen name="VisitCert" component={VisitCertScreen} options={{ title: '방문 인증' }} />
    </Stack.Navigator>
  );
}

function SocialStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="CommunityMain" component={CommunityScreen} options={{ headerShown: false }} />
      <Stack.Screen name="Ranking" component={RankingScreen} options={{ title: '유저 리그 랭킹' }} />
    </Stack.Navigator>
  );
}

function MyPageStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="MyPageMain" component={MyPageScreen} options={{ headerShown: false }} />
      <Stack.Screen name="BadgeList" component={BadgeListScreen} options={{ title: '획득 배지 보관함' }} />
    </Stack.Navigator>
  );
}

const stackOptions = {
  headerStyle: { backgroundColor: '#0F172A' },
  headerTintColor: '#F8FAFC',
  headerTitleStyle: { fontWeight: 'bold' },
  headerShadowVisible: false,
};

export default function TabNavigator() {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: '#0F172A',
          borderTopWidth: 0.5,
          borderTopColor: '#1E293B',
          height: Platform.OS === 'ios' ? 88 : 68,
          paddingBottom: Platform.OS === 'ios' ? 28 : 12,
          paddingTop: 10,
        },
        tabBarActiveTintColor: '#38BDF8',
        tabBarInactiveTintColor: '#64748B',
        tabBarLabelStyle: {
          fontSize: 11,
          fontWeight: '600',
          marginTop: 4,
        },
      }}
    >
      <Tab.Screen 
        name="Home" 
        component={HomeStack} 
        options={{
          tabBarLabel: '홈',
          tabBarIcon: ({ color, size }) => (
            <Feather name="home" size={22} color={color} />
          )
        }}
      />
      <Tab.Screen 
        name="Map" 
        component={MapStack} 
        options={{
          tabBarLabel: '지도',
          tabBarIcon: ({ color, size }) => (
            <Feather name="map" size={22} color={color} />
          )
        }}
      />
      <Tab.Screen 
        name="Social" 
        component={SocialStack} 
        options={{
          tabBarLabel: '커뮤니티',
          tabBarIcon: ({ color, size }) => (
            <Feather name="message-square" size={22} color={color} />
          )
        }}
      />
      <Tab.Screen 
        name="MyPage" 
        component={MyPageStack}
        options={{
          tabBarLabel: '마이페이지',
          tabBarIcon: ({ color, size }) => (
            <Feather name="user" size={22} color={color} />
          )
        }}
      />
    </Tab.Navigator>
  );
}

const styles = StyleSheet.create({
  tabIcon: {
    fontSize: 18,
  },
});
