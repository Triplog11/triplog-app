import React from 'react';
import { StyleSheet, View, ScrollView, TouchableOpacity, SafeAreaView, Alert } from 'react-native';
import { useAuth } from '../../context/AuthContext';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';

export default function MyPageScreen({ navigation }) {
  const { logout } = useAuth();

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
            <CustomText variant="Heading/H3" color={theme.colors.primary}>JS</CustomText>
          </View>
          <CustomText variant="Heading/H3" color={theme.colors.textPrimary} style={styles.username}>
            김준수
          </CustomText>
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.userEmail}>
            junsu@example.com
          </CustomText>
          <TouchableOpacity style={styles.editBtn} activeOpacity={0.8}>
            <CustomText variant="UI/Button/Small" color={theme.colors.primary}>
              프로필 수정
            </CustomText>
          </TouchableOpacity>
        </View>

        {/* 메뉴 리스트 */}
        <View style={styles.menuContainer}>
          <TouchableOpacity
            style={styles.menuItem}
            onPress={() => navigation.navigate('BadgeList')}
            activeOpacity={0.7}
          >
            <CustomText variant="Body/Medium" color={theme.colors.textBody} style={styles.menuItemText}>
              🏆   전체 배지 보관함
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.textTertiary} style={styles.arrow}>❯</CustomText>
          </TouchableOpacity>

          <TouchableOpacity style={styles.menuItem} activeOpacity={0.7}>
            <CustomText variant="Body/Medium" color={theme.colors.textBody} style={styles.menuItemText}>
              ⚙️   푸시 알림 설정
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.textTertiary} style={styles.arrow}>❯</CustomText>
          </TouchableOpacity>

          <TouchableOpacity style={styles.menuItem} activeOpacity={0.7}>
            <CustomText variant="Body/Medium" color={theme.colors.textBody} style={styles.menuItemText}>
              📖   서비스 이용 가이드
            </CustomText>
            <CustomText variant="Caption" color={theme.colors.textTertiary} style={styles.arrow}>❯</CustomText>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.menuItem, styles.logoutItem]}
            onPress={handleLogout}
            activeOpacity={0.7}
          >
            <CustomText variant="Body/Medium" color={theme.colors.error} style={styles.logoutText}>
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
    backgroundColor: theme.colors.surface,
  },
  scrollContent: {
    paddingHorizontal: theme.spacing.lg,
    paddingTop: theme.spacing.lg,
    paddingBottom: 40,
  },
  profileCard: {
    backgroundColor: theme.colors.canvas,
    padding: theme.spacing.lg,
    borderRadius: theme.rounded.lg,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: theme.colors.border,
    shadowColor: theme.colors.textPrimary,
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.03,
    shadowRadius: 16,
    elevation: 2,
    marginTop: theme.spacing.sm,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: theme.colors.blueTint,
    borderWidth: 1.5,
    borderColor: theme.colors.primary,
    marginBottom: theme.spacing.base,
    justifyContent: 'center',
    alignItems: 'center',
  },
  username: {
    fontWeight: 'bold',
  },
  userEmail: {
    marginTop: 4,
    marginBottom: theme.spacing.base,
    fontWeight: '500',
  },
  editBtn: {
    paddingHorizontal: theme.spacing.lg,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.rounded.pill,
    backgroundColor: theme.colors.blueTint,
    borderWidth: 1,
    borderColor: theme.colors.primary,
  },
  menuContainer: {
    marginTop: theme.spacing.lg,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.lg,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: theme.colors.border,
    shadowColor: theme.colors.textPrimary,
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
    paddingHorizontal: theme.spacing.lg,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.border,
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
