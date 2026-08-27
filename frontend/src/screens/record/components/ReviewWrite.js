import React, { useState } from 'react';
import { StyleSheet, View, TextInput, TouchableOpacity, ScrollView } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import CustomText from '../../../components/common/CustomText';
import theme from '../../../theme/theme';

const MAX_LENGTH = 300;

/** 인증 후 방문 후기 작성 (선택) */
export default function ReviewWrite({ landmark, onSkip, onSubmit }) {
  const [rating, setRating] = useState(0);
  const [text, setText] = useState('');

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
        <CustomText variant="Heading/H4" color={theme.colors.text} style={styles.title}>
          이번 방문은 어땠나요?
        </CustomText>
        <CustomText variant="Body/Small" color={theme.colors.textSecondary} style={styles.subtitle}>
          {landmark.name} · 기록은 나중에 남겨도 괜찮아요
        </CustomText>

        {/* 별점 */}
        <View style={styles.starRow}>
          {[1, 2, 3, 4, 5].map((star) => (
            <TouchableOpacity key={star} onPress={() => setRating(star)} hitSlop={6} activeOpacity={0.7}>
              <Ionicons
                name={rating >= star ? 'star' : 'star-outline'}
                size={34}
                color={rating >= star ? theme.colors.warning : theme.colors.textMuted}
              />
            </TouchableOpacity>
          ))}
        </View>

        {/* 후기 */}
        <View style={styles.inputBox}>
          <TextInput
            style={styles.input}
            placeholder="어떤 점이 좋았는지 자유롭게 적어 주세요."
            placeholderTextColor={theme.colors.textMuted}
            value={text}
            onChangeText={setText}
            multiline
            maxLength={MAX_LENGTH}
            textAlignVertical="top"
          />
          <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.counter}>
            {text.length}/{MAX_LENGTH}
          </CustomText>
        </View>

        {/* 사진 첨부 (준비 중) */}
        <View style={styles.photoRow}>
          <View style={styles.photoSlot}>
            <Ionicons name="camera-outline" size={22} color={theme.colors.textMuted} />
            <CustomText variant="Caption" color={theme.colors.textMuted}>사진 첨부</CustomText>
          </View>
          <CustomText variant="Caption" color={theme.colors.textMuted} style={styles.photoHint}>
            사진 업로드는 곧 지원될 예정이에요.
          </CustomText>
        </View>
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          style={styles.submitBtn}
          onPress={() => onSubmit({ rating, text: text.trim() })}
          activeOpacity={0.9}
        >
          <CustomText variant="UI/Button" color="#FFFFFF" style={styles.bold}>
            기록 남기기
          </CustomText>
        </TouchableOpacity>
        <TouchableOpacity style={styles.skipBtn} onPress={onSkip} activeOpacity={0.8}>
          <CustomText variant="UI/Button" color={theme.colors.textSecondary} style={styles.bold}>
            나중에 하기
          </CustomText>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scroll: {
    padding: theme.spacing.lg,
  },
  title: {
    fontWeight: 'bold',
  },
  subtitle: {
    marginTop: 6,
  },
  bold: {
    fontWeight: 'bold',
  },
  starRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 10,
    marginVertical: theme.spacing.xl,
  },
  inputBox: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderColor: theme.colors.border,
    padding: theme.spacing.base,
  },
  input: {
    minHeight: 120,
    color: theme.colors.text,
    fontSize: 14,
    lineHeight: 21,
    fontFamily: 'Pretendard-Regular',
    padding: 0,
  },
  counter: {
    alignSelf: 'flex-end',
    marginTop: 6,
  },
  photoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: theme.spacing.base,
    marginTop: theme.spacing.base,
  },
  photoSlot: {
    width: 76,
    height: 76,
    borderRadius: theme.rounded.card,
    borderWidth: 1,
    borderStyle: 'dashed',
    borderColor: theme.colors.border,
    justifyContent: 'center',
    alignItems: 'center',
    gap: 3,
  },
  photoHint: {
    flex: 1,
  },
  footer: {
    padding: theme.spacing.lg,
    gap: theme.spacing.sm,
  },
  submitBtn: {
    height: 56,
    borderRadius: theme.rounded.cta,
    backgroundColor: theme.colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  skipBtn: {
    height: 48,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
