import React from 'react';
import { StyleSheet, View, Pressable } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

const STAR_SIZE = 36;

/**
 * 반개 단위 별점 (1.0 ~ 5.0). 별의 왼쪽 절반을 누르면 .5, 오른쪽 절반을 누르면 정수.
 * @param value 0(미선택) 또는 0.5 단위 값
 */
export default function StarRating({ value, onChange }) {
  return (
    <View style={styles.wrapper}>
      <View style={styles.row}>
        {[1, 2, 3, 4, 5].map((star) => {
          const icon = value >= star ? 'star' : value >= star - 0.5 ? 'star-half' : 'star-outline';
          const active = value >= star - 0.5;
          return (
            <View key={star} style={styles.star}>
              <Ionicons
                name={icon}
                size={STAR_SIZE}
                color={active ? theme.colors.warning : theme.colors.textMuted}
              />
              <View style={styles.hitArea}>
                <Pressable style={styles.half} onPress={() => onChange(star - 0.5)} hitSlop={{ top: 8, bottom: 8 }} />
                <Pressable style={styles.half} onPress={() => onChange(star)} hitSlop={{ top: 8, bottom: 8 }} />
              </View>
            </View>
          );
        })}
      </View>
      <CustomText variant="Caption" color={theme.colors.textSecondary} style={styles.label}>
        {value > 0 ? `${value.toFixed(1)}점` : '별점을 선택해 주세요'}
      </CustomText>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    alignItems: 'center',
    gap: theme.spacing.sm,
  },
  row: {
    flexDirection: 'row',
    gap: 8,
  },
  star: {
    width: STAR_SIZE,
    height: STAR_SIZE,
  },
  hitArea: {
    ...StyleSheet.absoluteFillObject,
    flexDirection: 'row',
  },
  half: {
    flex: 1,
  },
  label: {
    textAlign: 'center',
  },
});
