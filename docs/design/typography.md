# 트립로그 타이포그래피 시스템 가이드 (한국어)

이 문서는 트립로그 서비스의 텍스트 토큰 및 타이포그래피 스타일 규격을 정의합니다.
모든 UI 텍스트 스타일은 아래 정의된 **Pretendard** 폰트의 4축 프리미티브 변수를 조합하여 구성합니다.

---

## 1. 프리미티브 변수 (Primitive Variables)

### 1-1. Size (FONT_SIZE)

| Token | Value | CSS |
| :--- | :--- | :--- |
| `size/2xs` | 10px | `var(--size-2xs)` |
| `size/xs` | 11px | `var(--size-xs)` |
| `size/sm` | 12px | `var(--size-sm)` |
| `size/md` | 14px | `var(--size-md)` |
| `size/base` | 16px | `var(--size-base)` |
| `size/lg` | 18px | `var(--size-lg)` |
| `size/xl` | 20px | `var(--size-xl)` |
| `size/2xl` | 24px | `var(--size-2xl)` |
| `size/3xl` | 28px | `var(--size-3xl)` |
| `size/4xl` | 32px | `var(--size-4xl)` |
| `size/5xl` | 36px | `var(--size-5xl)` |
| `size/6xl` | 40px | `var(--size-6xl)` |
| `size/7xl` | 48px | `var(--size-7xl)` |
| `size/8xl` | 56px | `var(--size-8xl)` |
| `size/9xl` | 64px | `var(--size-9xl)` |
| `size/10xl` | 72px | `var(--size-10xl)` |

### 1-2. Weight (FONT_WEIGHT)

| Token | Value | CSS |
| :--- | :--- | :--- |
| `weight/light` | 300 (Light) | `var(--weight-light)` |
| `weight/regular` | 400 (Regular) | `var(--weight-regular)` |
| `weight/bold` | 700 (Bold) | `var(--weight-bold)` |

### 1-3. Line Height (LINE_HEIGHT)

| Token | Value | CSS |
| :--- | :--- | :--- |
| `line-height/tight` | 120% (1.2) | `var(--line-height-tight)` |
| `line-height/snug` | 140% (1.4) | `var(--line-height-snug)` |
| `line-height/relaxed` | 160% (1.6) | `var(--line-height-relaxed)` |

### 1-4. Letter Spacing (LETTER_SPACING)

| Token | Value | CSS |
| :--- | :--- | :--- |
| `letter-spacing/narrow` | -0.5% (-0.005) | `var(--letter-spacing-narrow)` |
| `letter-spacing/default` | 0% (0.0) | `var(--letter-spacing-default)` |
| `letter-spacing/wide` | 2.5% (0.025) | `var(--letter-spacing-wide)` |

---

## 2. 공통 텍스트 스타일 프리셋 (18종)

| Style Name (Variant) | Size | Weight | Line Height | Letter Spacing |
| :--- | :--- | :--- | :--- | :--- |
| **KR/Display/Large** | 72px (`10xl`) | Bold | 120% (`tight`) | -0.5% (`narrow`) |
| **KR/Display/Medium** | 64px (`9xl`) | Bold | 120% (`tight`) | -0.5% (`narrow`) |
| **KR/Display/Small** | 56px (`8xl`) | Bold | 120% (`tight`) | -0.5% (`narrow`) |
| **KR/Heading/H1** | 36px (`5xl`) | Bold | 120% (`tight`) | -0.5% (`narrow`) |
| **KR/Heading/H2** | 32px (`4xl`) | Bold | 120% (`tight`) | -0.5% (`narrow`) |
| **KR/Heading/H3** | 28px (`3xl`) | Bold | 120% (`tight`) | 0% (`default`) |
| **KR/Heading/H4** | 24px (`2xl`) | Regular | 120% (`tight`) | 0% (`default`) |
| **KR/Heading/H5** | 20px (`xl`) | Regular | 140% (`snug`) | 0% (`default`) |
| **KR/Body/Large** | 18px (`lg`) | Regular | 160% (`relaxed`) | 0% (`default`) |
| **KR/Body/Medium** | 16px (`base`) | Regular | 160% (`relaxed`) | 0% (`default`) |
| **KR/Body/Small** | 14px (`md`) | Regular | 160% (`relaxed`) | 0% (`default`) |
| **KR/Label/Large** | 14px (`md`) | Regular | 140% (`snug`) | 2.5% (`wide`) |
| **KR/Label/Medium** | 12px (`sm`) | Regular | 140% (`snug`) | 2.5% (`wide`) |
| **KR/Label/Small** | 11px (`xs`) | Regular | 140% (`snug`) | 2.5% (`wide`) |
| **KR/UI/Button** | 16px (`base`) | Regular | 140% (`snug`) | 0% (`default`) |
| **KR/UI/Button/Small** | 12px (`sm`) | Regular | 140% (`snug`) | 2.5% (`wide`) |
| **KR/Caption** | 10px (`2xs`) | Light | 140% (`snug`) | 2.5% (`wide`) |
| **KR/Overline** | 11px (`xs`) | Regular | 140% (`snug`) | 2.5% (`wide`) |
