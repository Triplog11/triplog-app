import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { StyleSheet, View, FlatList } from 'react-native';
import { fetchNationwideMap } from '../../../api/regions';
import SearchBar from './SearchBar';
import SelectRow, { listContentStyle } from './SelectRow';
import StatusBlock from './StatusBlock';

/** 1단계 — 인증할 지역(시·군·구) 선택. 전국 지도 API의 지역 목록을 검색한다. */
export default function RegionSelectList({ onSelect }) {
  const [regions, setRegions] = useState([]);
  const [status, setStatus] = useState('loading');
  const [errorMessage, setErrorMessage] = useState('');
  const [search, setSearch] = useState('');

  const load = useCallback(async () => {
    setStatus('loading');
    try {
      const data = await fetchNationwideMap();
      setRegions(data?.regions ?? []);
      setStatus('ready');
    } catch (error) {
      console.error('지역 목록을 불러오지 못했어요:', error);
      setErrorMessage(error?.message ?? '지역 목록을 불러오지 못했어요.');
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const filtered = useMemo(() => {
    const keyword = search.trim();
    if (!keyword) return regions;
    return regions.filter((r) => (r.regionName ?? '').includes(keyword));
  }, [regions, search]);

  const emptyMessage = search
    ? `'${search}' 검색 결과가 없어요. 다른 지역으로 찾아보시겠어요?`
    : '아직 등록된 지역이 없어요.';

  return (
    <View style={styles.container}>
      <SearchBar value={search} onChangeText={setSearch} placeholder="지역 이름으로 찾기" />

      {status === 'loading' && <StatusBlock loading />}
      {status === 'error' && <StatusBlock message={errorMessage} actionLabel="다시 시도" onAction={load} />}
      {status === 'ready' && (
        <FlatList
          data={filtered}
          keyExtractor={(item) => String(item.regionId)}
          contentContainerStyle={listContentStyle}
          showsVerticalScrollIndicator={false}
          keyboardShouldPersistTaps="handled"
          ListEmptyComponent={<StatusBlock message={emptyMessage} />}
          renderItem={({ item }) => (
            <SelectRow
              icon="map-outline"
              title={item.regionName}
              subtitle={item.visited ? `달성률 ${item.completionRate ?? '--'}%` : '아직 방문하지 않은 지역'}
              highlighted={item.visited}
              badge={item.completed ? '완료' : null}
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
});
