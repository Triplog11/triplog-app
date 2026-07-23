import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import theme from '../theme/theme';
import TripLogTabBar from '../components/navigation/TripLogTabBar';

// Screens
import HomeScreen from '../screens/home/HomeScreen';
import HistoryScreen from '../screens/home/HistoryScreen';
import RegionDetailScreen from '../screens/home/RegionDetailScreen';
import CollectionScreen from '../screens/collection/CollectionScreen';
import RegionCollectionScreen from '../screens/collection/RegionCollectionScreen';
import VisitCertScreen from '../screens/record/VisitCertScreen';
import CommunityScreen from '../screens/social/CommunityScreen';
import RankingScreen from '../screens/social/RankingScreen';
import MyPageScreen from '../screens/mypage/MyPageScreen';
import BadgeListScreen from '../screens/mypage/BadgeListScreen';
import ProfileEditScreen from '../screens/mypage/ProfileEditScreen';
import NotificationScreen from '../screens/mypage/NotificationScreen';
import TravelLogScreen from '../screens/mypage/TravelLogScreen';
import VerifyHistoryScreen from '../screens/mypage/VerifyHistoryScreen';
import WishlistScreen from '../screens/mypage/WishlistScreen';

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

// 각 탭별 스택 네비게이터 정의
function HomeStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="HomeMain" component={HomeScreen} options={{ headerShown: false }} />
      <Stack.Screen name="RegionDetail" component={RegionDetailScreen} options={{ headerShown: false }} />
      <Stack.Screen name="History" component={HistoryScreen} options={{ title: '인증 히스토리' }} />
    </Stack.Navigator>
  );
}

function CollectionStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="CollectionMain" component={CollectionScreen} options={{ headerShown: false }} />
      <Stack.Screen name="RegionCollection" component={RegionCollectionScreen} options={{ headerShown: false }} />
    </Stack.Navigator>
  );
}

function RecordStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="VisitCertMain" component={VisitCertScreen} options={{ headerShown: false }} />
    </Stack.Navigator>
  );
}

function RankingStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="RankingMain" component={RankingScreen} options={{ headerShown: false }} />
      <Stack.Screen name="Community" component={CommunityScreen} options={{ title: '여행 피드 커뮤니티' }} />
    </Stack.Navigator>
  );
}

function MyPageStack() {
  return (
    <Stack.Navigator screenOptions={stackOptions}>
      <Stack.Screen name="MyPageMain" component={MyPageScreen} options={{ headerShown: false }} />
      <Stack.Screen name="BadgeList" component={BadgeListScreen} options={{ title: '뱃지 보관함' }} />
      <Stack.Screen name="ProfileEdit" component={ProfileEditScreen} options={{ title: '프로필 수정' }} />
      <Stack.Screen name="Notification" component={NotificationScreen} options={{ title: '알림' }} />
      <Stack.Screen name="TravelLog" component={TravelLogScreen} options={{ title: '여행 기록' }} />
      <Stack.Screen name="VerifyHistory" component={VerifyHistoryScreen} options={{ title: '인증 내역' }} />
      <Stack.Screen name="Wishlist" component={WishlistScreen} options={{ title: '찜한 랜드마크' }} />
    </Stack.Navigator>
  );
}

const stackOptions = {
  headerStyle: { backgroundColor: '#FFFFFF' },
  headerTintColor: '#0F172A',
  headerTitleStyle: { fontFamily: 'Pretendard-Bold', fontWeight: '700' },
  headerShadowVisible: false,
};

export default function TabNavigator() {
  return (
    <Tab.Navigator
      tabBar={(props) => <TripLogTabBar {...props} />}
      screenOptions={{
        headerShown: false,
        sceneStyle: { backgroundColor: theme.colors.surface },
      }}
    >
      <Tab.Screen name="Home" component={HomeStack} />
      <Tab.Screen name="Collection" component={CollectionStack} />
      <Tab.Screen name="Record" component={RecordStack} />
      <Tab.Screen name="Ranking" component={RankingStack} />
      <Tab.Screen name="MyPage" component={MyPageStack} />
    </Tab.Navigator>
  );
}
