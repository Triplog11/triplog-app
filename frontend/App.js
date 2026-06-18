import React, { useEffect, useState } from 'react';
import { View, ScrollView, StyleSheet } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import * as Font from 'expo-font';
import * as SplashScreen from 'expo-splash-screen';
import AppNavigator from './src/navigation/AppNavigator';
import CustomText from './src/components/common/CustomText';

// 타이포그래피 디자인 시스템 테스트 뷰 활성화 플래그 (검증 후 false로 변경)
const SHOW_DESIGN_SYSTEM_TEST = false;

// 폰트 로드 완료 전 스플래시 자동 숨김 방지
SplashScreen.preventAutoHideAsync();

export default function App() {
  const [appIsReady, setAppIsReady] = useState(false);

  useEffect(() => {
    async function prepare() {
      try {
        await Font.loadAsync({
          'Pretendard-Light': require('./assets/fonts/Pretendard-Light.otf'),
          'Pretendard-Regular': require('./assets/fonts/Pretendard-Regular.otf'),
          'Pretendard-Bold': require('./assets/fonts/Pretendard-Bold.otf'),
        });
      } catch (e) {
        console.warn(e);
      } finally {
        setAppIsReady(true);
      }
    }

    prepare();
  }, []);

  const onLayoutRootView = async () => {
    if (appIsReady) {
      // 폰트 로드 완료 후 스플래시 숨김
      await SplashScreen.hideAsync();
    }
  };

  if (!appIsReady) {
    return null;
  }

  // 18종 타이포그래피 테스트 스크린
  const renderTestScreen = () => (
    <ScrollView contentContainerStyle={styles.testContainer}>
      <CustomText variant="Heading/H1" style={styles.testTitle}>
        Typography System Test
      </CustomText>
      
      <View style={styles.testSection}>
        <CustomText variant="Caption" color="#888888">Display Styles (L, M, S)</CustomText>
        <CustomText variant="Display/Large">디스플레이 라지</CustomText>
        <CustomText variant="Display/Medium">디스플레이 미디엄</CustomText>
        <CustomText variant="Display/Small">디스플레이 스몰</CustomText>
      </View>

      <View style={styles.testSection}>
        <CustomText variant="Caption" color="#888888">Heading Styles (H1 ~ H5)</CustomText>
        <CustomText variant="Heading/H1">제목 H1</CustomText>
        <CustomText variant="Heading/H2">제목 H2</CustomText>
        <CustomText variant="Heading/H3">제목 H3</CustomText>
        <CustomText variant="Heading/H4">제목 H4 (Regular)</CustomText>
        <CustomText variant="Heading/H5">제목 H5 (Regular)</CustomText>
      </View>

      <View style={styles.testSection}>
        <CustomText variant="Caption" color="#888888">Body Styles (L, M, S)</CustomText>
        <CustomText variant="Body/Large">본문 라지 — 가독성 확인용 본문입니다.</CustomText>
        <CustomText variant="Body/Medium">본문 미디엄 — 가독성 확인용 본문입니다.</CustomText>
        <CustomText variant="Body/Small">본문 스몰 — 가독성 확인용 본문입니다.</CustomText>
      </View>

      <View style={styles.testSection}>
        <CustomText variant="Caption" color="#888888">Label & UI Styles</CustomText>
        <CustomText variant="Label/Large">라벨 라지 (Wide 자간)</CustomText>
        <CustomText variant="Label/Medium">라벨 미디엄 (Wide 자간)</CustomText>
        <CustomText variant="Label/Small">라벨 스몰 (Wide 자간)</CustomText>
        <CustomText variant="UI/Button">UI 버튼</CustomText>
        <CustomText variant="UI/Button/Small">버튼 스몰 (Wide 자간)</CustomText>
      </View>

      <View style={styles.testSection}>
        <CustomText variant="Caption" color="#888888">Caption & Overline</CustomText>
        <CustomText variant="Caption">캡션 텍스트 (Light 폰트)</CustomText>
        <CustomText variant="Overline">OVERLINE TEXT (오버라인)</CustomText>
      </View>
    </ScrollView>
  );

  return (
    <View style={{ flex: 1 }} onLayout={onLayoutRootView}>
      {SHOW_DESIGN_SYSTEM_TEST ? renderTestScreen() : <AppNavigator />}
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  testContainer: {
    paddingTop: 60,
    paddingHorizontal: 20,
    paddingBottom: 40,
    backgroundColor: '#f8f9fa',
  },
  testTitle: {
    marginBottom: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#dddddd',
    paddingBottom: 10,
  },
  testSection: {
    marginBottom: 20,
    padding: 15,
    backgroundColor: '#ffffff',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#eeeeee',
    gap: 10,
  },
});
