import React from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, SafeAreaView, Alert } from 'react-native';
import { useAuth } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';

export default function MyPageScreen({ navigation }) {
  const { logout } = useAuth(); // AuthContext의 로그아웃 액션 호출

  const handleLogout = () => {
    Alert.alert('로그아웃', '정말 로그아웃 하시겠습니까?', [
      { text: '취소', style: 'cancel' },
      { text: '로그아웃', onPress: () => logout() }
    ]);
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent} showsVerticalScrollIndicator={false}>
        
        {/* 프로필 요약 카드 */}
        <View style={styles.profileCard}>
          <View style={styles.avatar}>
            <CustomText variant="Heading/H3" color="#3B82F6">JS</CustomText>
          </View>
          <CustomText variant="Heading/H3" color="#0F172A" style={styles.username}>
            김준수
          </CustomText>
          <CustomText variant="Body/Small" color="#64748B" style={styles.userEmail}>
            junsu@example.com
          </CustomText>
          <TouchableOpacity style={styles.editBtn} activeOpacity={0.8}>
            <CustomText variant="UI/Button/Small" color="#475569">
              프로필 수정
            </CustomText>
          </TouchableOpacity>
        </View>

        {/* 메뉴 리스트 컨테이너 */}
        <View style={styles.menuContainer}>
          <TouchableOpacity 
            style={styles.menuItem} 
            onPress={() => navigation.navigate('BadgeList')}
            activeOpacity={0.7}
          >
            <CustomText variant="Body/Medium" color="#334155" style={styles.menuItemText}>
              🏆   전체 배지 보관함
            </CustomText>
            <CustomText variant="Caption" color="#94A3B8" style={styles.arrow}>❯</CustomText>
          </TouchableOpacity>

          <TouchableOpacity style={styles.menuItem} activeOpacity={0.7}>
            <CustomText variant="Body/Medium" color="#334155" style={styles.menuItemText}>
              ⚙️   푸시 알림 설정
            </CustomText>
            <CustomText variant="Caption" color="#94A3B8" style={styles.arrow}>❯</CustomText>
          </TouchableOpacity>

          <TouchableOpacity style={styles.menuItem} activeOpacity={0.7}>
            <CustomText variant="Body/Medium" color="#334155" style={styles.menuItemText}>
              📖   서비스 이용 가이드
            </CustomText>
            <CustomText variant="Caption" color="#94A3B8" style={styles.arrow}>❯</CustomText>
          </TouchableOpacity>

          <TouchableOpacity 
            style={[styles.menuItem, styles.logoutItem]} 
            onPress={handleLogout}
            activeOpacity={0.7}
          >
            <CustomText variant="Body/Medium" color="#EF4444" style={styles.logoutText}>
              🚪   로그아웃
            </CustomText>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F8FAFC', // Slate-50 공통 배경
  },
  scrollContent: {
    paddingHorizontal: 24,
    paddingTop: 20,
    paddingBottom: 40,
  },
  profileCard: {
    backgroundColor: '#FFFFFF',
    padding: 24,
    borderRadius: 24,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.03,
    shadowRadius: 16,
    elevation: 2,
    marginTop: 10,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: '#EFF6FF', // Light blue
    borderWidth: 1.5,
    borderColor: '#BFDBFE',
    marginBottom: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  username: {
    fontWeight: 'bold',
  },
  userEmail: {
    marginTop: 4,
    marginBottom: 16,
    fontWeight: '500',
  },
  editBtn: {
    paddingHorizontal: 20,
    paddingVertical: 8,
    borderRadius: 12,
    backgroundColor: '#F1F5F9',
  },
  menuContainer: {
    marginTop: 24,
    backgroundColor: '#FFFFFF',
    borderRadius: 24,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: '#F1F5F9',
    shadowColor: '#0F172A',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.03,
    shadowRadius: 16,
    elevation: 2,
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 18,
    paddingHorizontal: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#F1F5F9',
  },
  menuItemText: {
    fontWeight: '500',
  },
  arrow: {
    fontWeight: 'bold',
  },
  logoutItem: {
    borderBottomWidth: 0,
  },
  logoutText: {
    fontWeight: 'bold',
  },
});
