import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { StyleSheet, View, FlatList } from 'react-native';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';
import { fetchRegionDetail } from '../../../api/regions';
import SearchBar from './SearchBar';
import SelectRow, { listContentStyle } from './SelectRow';
import StatusBlock from './StatusBlock';

/** 2단계 — 선택한 지역의 랜드마크 목록. 획득한 카드는 배지로 표시한다. */
export default function LandmarkSelectList({ region, onSelect }) {
  const [landmarks, setLandmarks] = useState([]);
  const [status, setStatus] = useState('loading');
  const [errorMessage, setErrorMessage] = useState('');
  const [search, setSearch] = useState('');

  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const detail = await fetchRegionDetail(region.regionId);
      const items = detail?.landmarks?.items ?? [];
      // 지역 코드는 랜드마크 DTO에 없을 수 있어 지역 상세의 값으로 보강한다
      setLandmarks(
        items.map((item) => ({
          ...item,
          legalRegionCode: item.legalRegionCode ?? detail.legalRegionCode,
          legalDistrictCode: item.legalDistrictCode ?? detail.legalDistrictCode,
          regionId: region.regionId,
          regionName: detail.regionName ?? region.regionName,
        })),
      );
      setStatus('ready');
    } catch (error) {
      console.error('랜드마크 목록을 불러오지 못했어요:', error);
      setErrorMessage(error?.message ?? '랜드마크 목록을 불러오지 못했어요.');
      setStatus('error');
    }
  }, [region.regionId, region.regionName]);

  useEffect(() => {
    load();
  }, [load]);

  const filtered = useMemo(() => {
    const keyword = search.trim();
    if (!keyword) return landmarks;
    return landmarks.filter((l) => (l.landmarkName ?? '').includes(keyword));
  }, [landmarks, search]);

  const emptyMessage = search
    ? `'${search}' 검색 결과가 없어요. 다른 이름으로 찾아보시겠어요?`
    : `${region.regionName}에 등록된 랜드마크가 아직 없어요.`;

  return (
    <View style={styles.container}>
      <SearchBar value={search} onChangeText={setSearch} placeholder="랜드마크 이름으로 찾기" />

      {status === 'loading' && <StatusBlock loading />}
      {status === 'error' && <StatusBlock message={errorMessage} actionLabel="다시 시도" onAction={load} />}
      {status === 'ready' && (
        <FlatList
          data={filtered}
          keyExtractor={(item) => String(item.landmarkId)}
          contentContainerStyle={listContentStyle}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
          ListHeaderComponent={
            <CustomText variant="Caption" color={theme.colors.textSecondary} style={styles.hint}>
              이미 획득한 랜드마크는 다시 인증할 수 있지만 보상은 지급되지 않아요.
            </CustomText>
          }
          ListEmptyComponent={<StatusBlock message={emptyMessage} />}
          renderItem={({ item }) => (
            <SelectRow
              icon="location"
              title={item.landmarkName}
              subtitle={item.regionName}
              highlighted={item.acquired}
              badge={item.acquired ? '획득' : null}
              onPress={() => onSelect(item)}
            />
          )}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  hint: {
    marginBottom: theme.spacing.xs,
  },
});
