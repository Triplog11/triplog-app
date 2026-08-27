import React, { useState, useMemo } from 'react';
import {
  StyleSheet,
  View,
  TouchableOpacity,
  Modal,
  FlatList,
  TextInput,
  ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from './CustomText';
import theme from '../../theme/theme';
import { PROVINCE_CODES } from '../../utils/provinces';
import { getDistrictsByProvince } from '../../utils/koreaDistricts';
import { fetchProvinceMap } from '../../api/regions';

const PROVINCE_LIST = Object.keys(PROVINCE_CODES);

/**
 * 시/도 및 시군구 이름 파싱 헬퍼
 */
export function formatAddressFromSelection(provinceName, rawRegionName) {
  const isMetropolitan =
    provinceName.includes('특별시') ||
    provinceName.includes('광역시') ||
    provinceName.includes('특별자치시');

  const cleanRegion = (rawRegionName || '').replace(provinceName, '').trim();

  if (isMetropolitan) {
    return {
      addressDoGun: provinceName,
      addressSi: provinceName,
      addressGu: cleanRegion || provinceName,
    };
  }

  // 도 단위 (경기도, 강원도 등)
  const parts = cleanRegion.split(/\s+/);
  if (parts.length >= 2) {
    return {
      addressDoGun: provinceName,
      addressSi: parts[0],
      addressGu: parts.slice(1).join(' '),
    };
  }

  return {
    addressDoGun: provinceName,
    addressSi: parts[0] || provinceName,
    addressGu: parts[0] || provinceName,
  };
}

/**
 * 공통 주소 선택 바텀시트 컴포넌트
 */
export default function RegionPicker({
  value,
  onSelect,
  placeholder = '거주 지역을 선택해 주세요',
  disabled = false,
}) {
  const [modalVisible, setModalVisible] = useState(false);
  const [step, setStep] = useState('province'); // 'province' | 'district'
  const [selectedProvince, setSelectedProvince] = useState(null);
  const [districts, setDistricts] = useState([]);
  const [loadingDistricts, setLoadingDistricts] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState('');

  const displayValue = useMemo(() => {
    if (!value?.addressSi && !value?.addressGu) return '';
    const doGun = value.addressDoGun && value.addressDoGun !== value.addressSi ? `${value.addressDoGun} ` : '';
    const si = value.addressSi ? `${value.addressSi} ` : '';
    const gu = value.addressGu ?? '';
    return `${doGun}${si}${gu}`.trim();
  }, [value]);

  const handleOpen = () => {
    if (disabled) return;
    setStep('province');
    setSelectedProvince(null);
    setDistricts([]);
    setSearchKeyword('');
    setModalVisible(true);
  };

  const handleSelectProvince = async (province) => {
    setSelectedProvince(province);
    setSearchKeyword('');
    setStep('district');
    
    // 로컬 데이터셋으로 즉시 시군구 목록 로드 (비로그인 상태 401 방지)
    const localItems = getDistrictsByProvince(province);
    setDistricts(localItems);

    try {
      const code = PROVINCE_CODES[province]?.[0];
      if (code) {
        const res = await fetchProvinceMap(code);
        if (res?.regions && res.regions.length > 0) {
          setDistricts(res.regions);
        }
      }
    } catch (err) {
      // 비로그인 상태(401) 또는 네트워크 실패 시 로컬 데이터셋 유지
    }
  };

  const handleSelectDistrict = (district) => {
    const formatted = formatAddressFromSelection(selectedProvince, district.regionName);
    onSelect(formatted);
    setModalVisible(false);
  };

  const filteredProvinces = useMemo(() => {
    if (!searchKeyword.trim()) return PROVINCE_LIST;
    return PROVINCE_LIST.filter((p) => p.includes(searchKeyword.trim()));
  }, [searchKeyword]);

  const filteredDistricts = useMemo(() => {
    if (!searchKeyword.trim()) return districts;
    return districts.filter((d) =>
      (d.regionName ?? '').includes(searchKeyword.trim()),
    );
  }, [districts, searchKeyword]);

  return (
    <View>
      <TouchableOpacity
        style={styles.trigger}
        onPress={handleOpen}
        activeOpacity={0.8}
        disabled={disabled}
      >
        <CustomText
          variant="Body/Medium"
          color={displayValue ? theme.colors.text : theme.colors.textMuted}
          style={styles.triggerText}
        >
          {displayValue || placeholder}
        </CustomText>
        <Ionicons name="chevron-down" size={18} color={theme.colors.textSecondary} />
      </TouchableOpacity>

      <Modal
        visible={modalVisible}
        transparent
        animationType="slide"
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={styles.backdrop}>
          <View style={styles.sheet}>
            <View style={styles.header}>
              <View style={styles.titleRow}>
                {step === 'district' ? (
                  <TouchableOpacity
                    style={styles.backBtn}
                    onPress={() => {
                      setStep('province');
                      setSearchKeyword('');
                    }}
                    hitSlop={8}
                  >
                    <Ionicons name="arrow-back" size={20} color={theme.colors.text} />
                  </TouchableOpacity>
                ) : null}
                <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
                  {step === 'province' ? '시/도 선택' : `${selectedProvince} 시/군/구`}
                </CustomText>
              </View>
              <TouchableOpacity
                onPress={() => setModalVisible(false)}
                hitSlop={8}
                style={styles.closeBtn}
              >
                <Ionicons name="close" size={22} color={theme.colors.textSecondary} />
              </TouchableOpacity>
            </View>

            <View style={styles.searchBar}>
              <Ionicons name="search" size={18} color={theme.colors.textMuted} />
              <TextInput
                style={styles.searchInput}
                placeholder={step === 'province' ? '시/도 검색' : '시/군/구 검색'}
                placeholderTextColor={theme.colors.textMuted}
                value={searchKeyword}
                onChangeText={setSearchKeyword}
                autoCorrect={false}
              />
              {searchKeyword.length > 0 ? (
                <TouchableOpacity onPress={() => setSearchKeyword('')} hitSlop={6}>
                  <Ionicons name="close-circle" size={16} color={theme.colors.textMuted} />
                </TouchableOpacity>
              ) : null}
            </View>

            {step === 'province' ? (
              <FlatList
                data={filteredProvinces}
                keyExtractor={(item) => item}
                contentContainerStyle={styles.listContent}
                renderItem={({ item }) => (
                  <TouchableOpacity
                    style={styles.itemRow}
                    onPress={() => handleSelectProvince(item)}
                    activeOpacity={0.7}
                  >
                    <CustomText variant="Body/Medium" color={theme.colors.text}>
                      {item}
                    </CustomText>
                    <Ionicons name="chevron-forward" size={16} color={theme.colors.textMuted} />
                  </TouchableOpacity>
                )}
              />
            ) : loadingDistricts ? (
              <View style={styles.centerLoading}>
                <ActivityIndicator size="large" color={theme.colors.primary} />
              </View>
            ) : (
              <FlatList
                data={filteredDistricts}
                keyExtractor={(item) => String(item.regionId ?? item.regionName)}
                contentContainerStyle={styles.listContent}
                ListEmptyComponent={
                  <View style={styles.emptyBox}>
                    <CustomText variant="Body/Small" color={theme.colors.textSecondary}>
                      해당하는 지역이 없습니다.
                    </CustomText>
                  </View>
                }
                renderItem={({ item }) => (
                  <TouchableOpacity
                    style={styles.itemRow}
                    onPress={() => handleSelectDistrict(item)}
                    activeOpacity={0.7}
                  >
                    <CustomText variant="Body/Medium" color={theme.colors.text}>
                      {item.regionName}
                    </CustomText>
                    <Ionicons name="checkmark" size={16} color={theme.colors.primary} />
                  </TouchableOpacity>
                )}
              />
            )}
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  trigger: {
    height: 56,
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
    paddingHorizontal: 16,
    borderWidth: 1,
    borderColor: theme.colors.border,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  triggerText: {
    flex: 1,
  },
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.45)',
    justifyContent: 'flex-end',
  },
  sheet: {
    backgroundColor: theme.colors.canvas,
    borderTopLeftRadius: theme.rounded.xl,
    borderTopRightRadius: theme.rounded.xl,
    maxHeight: '80%',
    minHeight: 450,
    paddingBottom: 24,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    paddingVertical: 18,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.border,
  },
  titleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  title: {
    fontWeight: 'bold',
  },
  backBtn: {
    paddingRight: 4,
  },
  closeBtn: {
    padding: 2,
  },
  searchBar: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.pill,
    paddingHorizontal: 14,
    marginHorizontal: 20,
    marginVertical: 12,
    height: 44,
    borderWidth: 1,
    borderColor: theme.colors.border,
    gap: 8,
  },
  searchInput: {
    flex: 1,
    color: theme.colors.text,
    fontSize: 14,
    fontFamily: 'Pretendard-Regular',
    padding: 0,
  },
  listContent: {
    paddingHorizontal: 20,
    paddingBottom: 20,
  },
  itemRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 14,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.surfaceDim,
  },
  centerLoading: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: 200,
  },
  emptyBox: {
    paddingVertical: 32,
    alignItems: 'center',
  },
});
