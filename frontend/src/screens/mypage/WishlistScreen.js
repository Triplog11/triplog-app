import React from 'react';
import { StyleSheet, View, FlatList, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { WISHLIST } from '../../data/activity';
import { GRADE_CONFIG } from '../../data/collection';

/** 찜한 랜드마크 (찜 API 연동 전 목데이터) */
export default function WishlistScreen({ navigation }) {
  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={WISHLIST}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={
          <View style={styles.empty}>
            <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
              아직 찜한 랜드마크가 없어요. 도감에서 가고 싶은 곳을 담아 보세요!
            </CustomText>
          </View>
        }
        renderItem={({ item }) => {
          const grade = GRADE_CONFIG[item.grade];
          return (
            <View style={styles.row}>
              <View style={[styles.thumb, { backgroundColor: grade.soft }]}>
                <Ionicons name="heart" size={20} color={grade.color} />
              </View>
              <View style={styles.info}>
                <View style={styles.titleRow}>
                  <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
                    {item.name}
                  </CustomText>
                  <View style={[styles.gradePill, { backgroundColor: grade.soft }]}>
                    <CustomText variant="Caption" color={grade.color} style={styles.gradeText}>
                      {item.grade}
                    </CustomText>
                  </View>
                </View>
                <CustomText variant="Caption" color={theme.colors.textSecondary}>
                  {item.region}
                </CustomText>
              </View>
              <TouchableOpacity
                style={styles.verifyBtn}
                onPress={() => navigation.navigate('Record')}
                activeOpacity={0.85}
              >
                <CustomText variant="Caption" color="#FFFFFF" style={styles.bold}>
                  인증하러
                </CustomText>
              </TouchableOpacity>
            </View>
          );
        }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  list: {
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.sm,
  },
  thumb: {
    width: 46,
    height: 46,
    borderRadius: theme.rounded.md,
    justifyContent: 'center',
    alignItems: 'center',
  },
  info: {
    flex: 1,
    gap: 2,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  bold: {
    fontWeight: 'bold',
  },
  gradePill: {
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 7,
    paddingVertical: 1,
  },
  gradeText: {
    fontWeight: 'bold',
    fontSize: 10,
  },
  verifyBtn: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 12,
    paddingVertical: 7,
  },
  empty: {
    paddingTop: 80,
    paddingHorizontal: 30,
  },
  emptyText: {
    textAlign: 'center',
  },
});
