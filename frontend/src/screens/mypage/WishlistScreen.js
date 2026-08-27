import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, FlatList, TouchableOpacity, ActivityIndicator, Image } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchBookmarks, deleteBookmark, BOOKMARK_TYPE } from '../../api/bookmarks';
import { EmptyStateAssets } from '../../assets';

/** 찜한 랜드마크 — 북마크 API 연동 */
export default function WishlistScreen({ navigation }) {
  const [bookmarks, setBookmarks] = useState([]);
  const [status, setStatus] = useState('loading'); // loading | ready | error

  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const result = await fetchBookmarks({
        bookmarkType: BOOKMARK_TYPE.LANDMARK,
        page: 0,
        size: 50,
      });
      setBookmarks(result?.bookmarks ?? []);
      setStatus('ready');
    } catch (error) {
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleUnbookmark = useCallback(async (bookmarkId) => {
    try {
      const result = await deleteBookmark(bookmarkId);
      if (result?.isDeleted) {
        setBookmarks((prev) => prev.filter((item) => item.bookmarkId !== bookmarkId));
      }
    } catch (error) {
      // 해제 실패 시 목록은 그대로 유지 — 다음 탭에서 재시도할 수 있어요.
    }
  }, []);

  if (status === 'loading') {
    return (
      <SafeAreaView style={styles.container} edges={['bottom']}>
        <View style={styles.center}>
          <ActivityIndicator size="large" color={theme.colors.primary} />
        </View>
      </SafeAreaView>
    );
  }

  if (status === 'error') {
    return (
      <SafeAreaView style={styles.container} edges={['bottom']}>
        <View style={styles.center}>
          <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.centerText}>
            찜한 곳을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
          </CustomText>
          <TouchableOpacity style={styles.retryPill} onPress={load} activeOpacity={0.85}>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      <FlatList
        data={bookmarks}
        keyExtractor={(item) => String(item.bookmarkId)}
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
        ListEmptyComponent={
          <View style={styles.empty}>
            <Image
              source={EmptyStateAssets.collection}
              style={styles.emptyImage}
              resizeMode="contain"
            />
            <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
              아직 찜한 곳이 없어요. 마음에 드는 랜드마크를 찜해 보세요!
            </CustomText>
          </View>
        }
        renderItem={({ item }) => (
          <View style={styles.row}>
            <View style={styles.thumb}>
              <Ionicons name="location" size={20} color={theme.colors.primary} />
            </View>
            <View style={styles.info}>
              <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
                {item.landmarkName}
              </CustomText>
              {!!item.regionName && (
                <CustomText variant="Caption" color={theme.colors.textSecondary}>
                  {item.regionName}
                </CustomText>
              )}
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
            <TouchableOpacity
              style={styles.heartBtn}
              onPress={() => handleUnbookmark(item.bookmarkId)}
              activeOpacity={0.7}
              accessibilityLabel="찜 해제"
            >
              <Ionicons name="heart" size={22} color={theme.colors.primary} />
            </TouchableOpacity>
          </View>
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: theme.spacing.lg,
    gap: theme.spacing.base,
  },
  centerText: {
    textAlign: 'center',
  },
  retryPill: {
    borderRadius: theme.rounded.pill,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 8,
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
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
  info: {
    flex: 1,
    gap: 2,
  },
  bold: {
    fontWeight: 'bold',
  },
  verifyBtn: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 12,
    paddingVertical: 7,
  },
  heartBtn: {
    width: 36,
    height: 36,
    justifyContent: 'center',
    alignItems: 'center',
  },
  empty: {
    paddingTop: 60,
    paddingHorizontal: 30,
    alignItems: 'center',
    gap: 16,
  },
  emptyImage: {
    width: 140,
    height: 94,
  },
  emptyText: {
    textAlign: 'center',
  },
});
