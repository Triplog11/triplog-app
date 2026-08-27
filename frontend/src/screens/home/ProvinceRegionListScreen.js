import React, { useState, useEffect, useCallback } from 'react';
import { StyleSheet, View, FlatList, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../components/common/CustomText';
import theme from '../../theme/theme';
import { fetchNationwideMap } from '../../api/regions';
import { filterProvinceRegions } from '../../utils/provinces';

/** completionRate가 0~1 비율인지 0~100 퍼센트인지 불확실 → 방어적 정규화 */
function toPercent(rate) {
  if (rate == null) return 0;
  return Math.round(rate <= 1 ? rate * 100 : rate);
}

/**
 * 홈 지도 "탐험하기" → 해당 시·도의 시·군·구 목록 (실 API).
 * 행을 누르면 도감의 지역 상세(랜드마크 카드)로 이어진다.
 */
export default function ProvinceRegionListScreen({ route, navigation }) {
  const provinceName = route.params?.regionName ?? '지역';
  const [regions, setRegions] = useState([]);
  const [status, setStatus] = useState('loading'); // loading | ready | error

  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const result = await fetchNationwideMap();
      setRegions(filterProvinceRegions(result?.regions ?? [], provinceName));
      setStatus('ready');
    } catch (error) {
      setStatus('error');
    }
  }, [provinceName]);

  useEffect(() => {
    load();
  }, [load]);

  const visitedCount = regions.filter((r) => r.visited).length;

  const openRegion = (item) => {
    navigation.navigate('Collection', {
      screen: 'RegionCollection',
      params: { regionId: item.regionId, regionName: item.regionName },
    });
  };

  const renderRegion = ({ item }) => {
    const percent = toPercent(item.completionRate);
    const complete = item.completed || percent === 100;
    return (
      <TouchableOpacity style={styles.row} onPress={() => openRegion(item)} activeOpacity={0.85}>
        <View style={[styles.rowIcon, item.visited && styles.rowIconVisited]}>
          <Ionicons
            name={complete ? 'trophy' : 'location-outline'}
            size={18}
            color={item.visited ? theme.colors.primary : theme.colors.textMuted}
          />
        </View>
        <View style={styles.rowInfo}>
          <CustomText variant="Label/Medium" color={theme.colors.text} style={styles.bold} numberOfLines={1}>
            {item.regionName}
          </CustomText>
          <CustomText variant="Caption" color={theme.colors.textSecondary}>
            {item.visited ? '방문한 지역' : '아직 방문 전'}
          </CustomText>
        </View>
        <View style={styles.rowRight}>
          <CustomText
            variant="Label/Medium"
            color={complete ? theme.colors.success : percent > 0 ? theme.colors.primary : theme.colors.textMuted}
            style={styles.bold}
          >
            {percent}%
          </CustomText>
          <Ionicons name="chevron-forward" size={14} color={theme.colors.textMuted} />
        </View>
      </TouchableOpacity>
    );
  };

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      {/* 헤더 */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} hitSlop={8} style={styles.backBtn}>
          <Ionicons name="chevron-back" size={22} color={theme.colors.text} />
        </TouchableOpacity>
        <View style={styles.headerText}>
          <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.bold}>
            {provinceName}
          </CustomText>
          {status === 'ready' && (
            <CustomText variant="Caption" color={theme.colors.textSecondary}>
              {regions.length}개 시·군·구 · 방문 {visitedCount}곳
            </CustomText>
          )}
        </View>
      </View>

      {status === 'loading' && (
        <View style={styles.center}>
          <ActivityIndicator size="large" color={theme.colors.primary} />
        </View>
      )}

      {status === 'error' && (
        <View style={styles.center}>
          <CustomText variant="Body/Medium" color={theme.colors.textSecondary} style={styles.centerText}>
            지역 현황을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
          </CustomText>
          <TouchableOpacity style={styles.retryPill} onPress={load} activeOpacity={0.85}>
            <CustomText variant="Label/Medium" color={theme.colors.primary} style={styles.bold}>
              다시 시도
            </CustomText>
          </TouchableOpacity>
        </View>
      )}

      {status === 'ready' && (
        <FlatList
          data={regions}
          keyExtractor={(item) => String(item.regionId)}
          renderItem={renderRegion}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
          ItemSeparatorComponent={() => <View style={styles.gap} />}
          ListEmptyComponent={
            <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.centerText}>
              이 지역의 시·군·구 정보가 아직 없어요. 곧 채워질 예정이에요!
            </CustomText>
          }
        />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.surface,
  },
  bold: {
    fontWeight: 'bold',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: theme.spacing.sm,
  },
  backBtn: {
    width: 36,
    height: 36,
    borderRadius: 18,
    justifyContent: 'center',
    alignItems: 'center',
  },
  headerText: {
    flex: 1,
    gap: 2,
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
    paddingVertical: theme.spacing.lg,
  },
  retryPill: {
    borderRadius: theme.rounded.pill ?? 9999,
    borderWidth: 1,
    borderColor: theme.colors.primary,
    backgroundColor: theme.colors.primarySoft,
    paddingHorizontal: theme.spacing.base,
    paddingVertical: 8,
  },
  list: {
    padding: theme.spacing.lg,
    paddingTop: theme.spacing.sm,
    paddingBottom: 40,
  },
  gap: {
    height: theme.spacing.sm,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.sm,
    backgroundColor: theme.colors.canvas,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  rowIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: theme.colors.surfaceDim,
    justifyContent: 'center',
    alignItems: 'center',
  },
  rowIconVisited: {
    backgroundColor: theme.colors.primarySoft,
  },
  rowInfo: {
    flex: 1,
    gap: 2,
  },
  rowRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
});
