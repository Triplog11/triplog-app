import React, { useState, useEffect, useCallback } from 'react';
import {
  StyleSheet,
  View,
  FlatList,
  SafeAreaView,
  Image,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { fetchBadges, setRepresentativeBadge } from '../../api/badges';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { getBadgeFallback } from '../../utils/badgeAssets';
import InlineToast from './components/InlineToast';

const PAGE_SIZE = 20;
const FILTERS = [
  { key: 'all', label: '전체', isAcquired: undefined },
  { key: 'acquired', label: '획득', isAcquired: true },
  { key: 'locked', label: '미획득', isAcquired: false },
];

/** 뱃지 보관함 — GET /badges 실연동 (페이징 + 획득 여부 필터) */
export default function BadgeListScreen({ navigation }) {
  const [filterKey, setFilterKey] = useState('all');
  const [badges, setBadges] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState(null);
  const [updatingId, setUpdatingId] = useState(null);
  const [toast, setToast] = useState(null);

  const filter = FILTERS.find((f) => f.key === filterKey);
  const dismissToast = useCallback(() => setToast(null), []);

  /** 대표 뱃지 설정 — 획득 뱃지만 가능, 낙관적 갱신 후 실패 시 롤백 */
  const handleSetRepresentative = async (badge) => {
    if (badge.representative || updatingId != null) return;
    const previous = badges;
    setUpdatingId(badge.badgeId);
    setBadges((prev) => prev.map((b) => ({ ...b, representative: b.badgeId === badge.badgeId })));
    try {
      await setRepresentativeBadge(badge.badgeId);
      setToast(`'${badge.badgeName}' 뱃지를 대표로 설정했어요`);
    } catch (error) {
      setBadges(previous);
      setToast(error?.message ?? '대표 뱃지 설정에 실패했어요. 다시 시도해 주세요.');
    } finally {
      setUpdatingId(null);
    }
  };

  const load = useCallback(
    async (nextPage, replace) => {
      if (nextPage === 0) setLoading(true);
      setErrorMessage(null);
      try {
        const result = await fetchBadges({
          isAcquired: filter.isAcquired,
          page: nextPage,
          size: PAGE_SIZE,
        });
        const items = result?.items ?? [];
        setBadges((prev) => (replace ? items : [...prev, ...items]));
        setPage(nextPage);
        setTotalPages(result?.totalPages ?? 0);
      } catch (error) {
        // 마지막 페이지 초과(404)는 목록 끝으로 처리
        if (error.status === 404 && nextPage > 0) {
          setTotalPages(nextPage);
        } else {
          setErrorMessage(error.message);
        }
      } finally {
        setLoading(false);
      }
    },
    [filter.isAcquired],
  );

  useEffect(() => {
    setBadges([]);
    load(0, true);
  }, [load]);

  const handleEndReached = () => {
    if (!loading && page + 1 < totalPages) {
      load(page + 1, false);
    }
  };

  const renderBadge = ({ item }) => {
    const acquired = filter.key === 'acquired' ? true : !!item.acquired;
    const representative = !!item.representative;
    return (
      <TouchableOpacity
        style={[styles.badgeCard, !acquired && styles.lockedCard, representative && styles.representativeCard]}
        onPress={acquired ? () => handleSetRepresentative(item) : undefined}
        onLongPress={acquired ? () => handleSetRepresentative(item) : undefined}
        delayLongPress={350}
        activeOpacity={acquired ? 0.8 : 1}
        disabled={!acquired}
      >
        {representative && (
          <View style={styles.repPill}>
            <CustomText variant="Caption" color={theme.colors.white} style={styles.statusText}>
              대표
            </CustomText>
          </View>
        )}
        <View style={styles.iconWrapper}>
          <Image
            source={item.badgeUrl ? { uri: item.badgeUrl } : getBadgeFallback(item.badgeName)}
            style={[styles.badgeImage, !acquired && styles.lockedImage]}
            resizeMode="contain"
          />
        </View>
        <CustomText
          variant="Heading/H5"
          color={acquired ? theme.colors.text : theme.colors.textMuted}
          style={styles.badgeName}
        >
          {item.badgeName}
        </CustomText>
        {item.badgeTarget != null && (
          <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.badgeDesc}>
            {item.badgeTarget}{item.badgeValue != null ? ` ${item.badgeValue}` : ''}
          </CustomText>
        )}
        <View style={[styles.statusTag, acquired ? styles.earnedTag : styles.lockedTag]}>
          <CustomText
            variant="Label/Small"
            color={acquired ? theme.colors.success : theme.colors.textSecondary}
            style={styles.statusText}
          >
            {acquired ? '획득 완료' : '잠김'}
          </CustomText>
        </View>
        {acquired && !representative && (
          <TouchableOpacity
            style={styles.repBtn}
            onPress={() => handleSetRepresentative(item)}
            activeOpacity={0.7}
            disabled={updatingId != null}
            hitSlop={6}
          >
            <CustomText variant="Caption" color={theme.colors.primary} style={styles.statusText}>
              대표 뱃지로 설정
            </CustomText>
          </TouchableOpacity>
        )}
      </TouchableOpacity>
    );
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.filterRow}>
        {FILTERS.map((f) => {
          const active = f.key === filterKey;
          return (
            <TouchableOpacity
              key={f.key}
              style={[styles.filterChip, active && styles.filterChipActive]}
              onPress={() => setFilterKey(f.key)}
              activeOpacity={0.8}
            >
              <CustomText
                variant="Label/Medium"
                color={active ? '#FFFFFF' : theme.colors.textSecondary}
                style={styles.filterLabel}
              >
                {f.label}
              </CustomText>
            </TouchableOpacity>
          );
        })}
      </View>

      {loading && badges.length === 0 ? (
        <View style={styles.centerBox}>
          <ActivityIndicator size="large" color={theme.colors.primary} />
        </View>
      ) : errorMessage && badges.length === 0 ? (
        <View style={styles.centerBox}>
          <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
            {errorMessage}
          </CustomText>
          <TouchableOpacity style={styles.retryBtn} onPress={() => load(0, true)} activeOpacity={0.8}>
            <CustomText variant="UI/Button/Small" color="#FFFFFF" style={styles.retryText}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        </View>
      ) : badges.length === 0 ? (
        <View style={styles.centerBox}>
          <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.emptyText}>
            아직 획득한 뱃지가 없어요. 첫 번째 모험을 시작해 보세요!
          </CustomText>
          <TouchableOpacity
            style={styles.exploreBtn}
            onPress={() => navigation.navigate('Home')}
            activeOpacity={0.85}
          >
            <CustomText variant="UI/Button/Small" color="#FFFFFF" style={styles.exploreText}>
              탐험하러 가기
            </CustomText>
          </TouchableOpacity>
        </View>
      ) : (
        <FlatList
          data={badges}
          keyExtractor={(item) => String(item.badgeId)}
          numColumns={2}
          contentContainerStyle={styles.listContainer}
          columnWrapperStyle={styles.columnWrapper}
          showsVerticalScrollIndicator={false}
          onEndReached={handleEndReached}
          onEndReachedThreshold={0.4}
          renderItem={renderBadge}
          ListFooterComponent={
            page + 1 < totalPages ? (
              <ActivityIndicator size="small" color={theme.colors.primary} style={styles.footerLoader} />
            ) : null
          }
        />
      )}
      <InlineToast message={toast} onDismiss={dismissToast} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  filterRow: {
    flexDirection: 'row',
    gap: theme.spacing.sm,
    paddingHorizontal: 24,
    paddingTop: theme.spacing.base,
    paddingBottom: theme.spacing.sm,
  },
  filterChip: {
    paddingHorizontal: 16,
    height: 36,
    borderRadius: theme.rounded.pill,
    backgroundColor: theme.colors.canvas,
    borderWidth: 1,
    borderColor: theme.colors.border,
    justifyContent: 'center',
  },
  filterChipActive: {
    backgroundColor: theme.colors.primary,
    borderColor: theme.colors.primary,
  },
  filterLabel: {
    fontWeight: '600',
  },
  centerBox: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 40,
    gap: theme.spacing.base,
  },
  emptyText: {
    textAlign: 'center',
  },
  retryBtn: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 24,
    height: 40,
    justifyContent: 'center',
  },
  exploreBtn: {
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 24,
    height: 40,
    justifyContent: 'center',
    marginTop: theme.spacing.base,
  },
  exploreText: {
    fontWeight: 'bold',
  },
  retryText: {
    fontWeight: 'bold',
  },
  listContainer: {
    paddingHorizontal: 24,
    gap: 16,
    paddingBottom: 40,
    paddingTop: theme.spacing.sm,
  },
  columnWrapper: {
    justifyContent: 'space-between',
  },
  badgeCard: {
    width: '47%',
    backgroundColor: theme.colors.canvas,
    padding: 18,
    borderRadius: theme.rounded.card,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: theme.colors.border,
  },
  lockedCard: {
    opacity: 0.55,
  },
  representativeCard: {
    borderColor: theme.colors.primary,
  },
  repPill: {
    position: 'absolute',
    top: theme.spacing.sm,
    left: theme.spacing.sm,
    backgroundColor: theme.colors.primary,
    borderRadius: theme.rounded.sm,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 2,
  },
  repBtn: {
    marginTop: theme.spacing.sm,
    paddingVertical: 2,
  },
  iconWrapper: {
    width: 64,
    height: 64,
    borderRadius: 32,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 12,
    backgroundColor: theme.colors.surfaceDim,
    overflow: 'hidden',
  },
  badgeImage: {
    width: 48,
    height: 48,
  },
  lockedImage: {
    opacity: 0.4,
  },
  fallbackIcon: {
    fontSize: 30,
  },
  badgeName: {
    fontWeight: 'bold',
    textAlign: 'center',
  },
  badgeDesc: {
    textAlign: 'center',
    marginTop: 6,
  },
  statusTag: {
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 8,
    marginTop: 12,
  },
  earnedTag: {
    backgroundColor: theme.colors.primarySoft,
  },
  lockedTag: {
    backgroundColor: theme.colors.surfaceDim,
  },
  statusText: {
    fontWeight: 'bold',
  },
});
