---
omd: 0.1
brand: TripLog
bootstrapped_from: baemin
bootstrapped_at: "2026-07-02"
tokens:
  source: bootstrapped (baemin canonical, delta_set empty)
  colors:
    primary: "#2AC1BC"
    brand: "#2AC1BC"
    canvas: "#ffffff"
    surface: "#F8F9FA"
    surface-subtle: "#F1F3F5"
    foreground: "#212529"
    body: "#495057"
    muted: "#868E96"
    disabled: "#ADB5BD"
    on-primary: "#ffffff"
    accent: "#12B886"
    accent-light: "#20C997"
    error: "#FF6B6B"
    warning: "#FFB347"
    info: "#74C0FC"
    promo: "#FF0000"
    hairline: "#DEE2E6"
    border-strong: "#343A40"
  typography:
    family: { ui: "Pretendard, -apple-system, BlinkMacSystemFont, 'Apple SD Gothic Neo', 'Noto Sans KR', sans-serif", mono: "SF Mono, SFMono-Regular, Menlo, Consolas, monospace" }
    display-hero:  { size: 42, weight: 700, lineHeight: 1.20, use: "스플래시, 레벨업 축하 브랜드 모먼트" }
    display-large: { size: 36, weight: 700, lineHeight: 1.25, use: "캠페인 타이틀, 섹션 히어로" }
    heading-large: { size: 24, weight: 700, lineHeight: 1.33, use: "홈 타이틀, 피처 섹션 제목" }
    heading:       { size: 20, weight: 700, lineHeight: 1.40, use: "카드 헤딩, 지역 카테고리" }
    title:         { size: 18, weight: 600, lineHeight: 1.44, use: "랜드마크 이름, 지역명" }
    body-large:    { size: 16, weight: 400, lineHeight: 1.50, use: "지역 소개, 랜드마크 설명" }
    body:          { size: 14, weight: 400, lineHeight: 1.57, use: "본문, 방문 후기" }
    body-small:    { size: 13, weight: 400, lineHeight: 1.54, use: "보조 정보, 획득 조건" }
    caption:       { size: 12, weight: 400, lineHeight: 1.50, use: "방문 일자, 거리 표시" }
    price:         { size: 20, weight: 700, lineHeight: 1.30, use: "XP·스코어·수집 수 숫자 표시" }
  spacing: { xs: 4, sm: 8, md: 12, base: 16, lg: 20, xl: 24, xxl: 32, section: 40, max: 48 }
  rounded: { sm: 4, md: 8, lg: 12, search: 20, full: 9999 }
  shadow:
    natural: "0px 1px 3px rgba(0,0,0,0.04)"
    deep: "0px 2px 8px rgba(0,0,0,0.08)"
    sharp: "0px 4px 12px rgba(0,0,0,0.10)"
    outlined: "0px 4px 16px rgba(0,0,0,0.12)"
    crisp: "0px 8px 24px rgba(0,0,0,0.16)"
  components:
    button-primary:        { type: button, bg: "#2ac1bc", fg: "#ffffff", radius: "8px", height: "48px", padding: "12px 24px", font: "16px / 700", states: "pressed #20a8a4 · disabled bg #dee2e6 / text #adb5bd", use: "Primary CTA (인증하기, 도감에 추가)" }
    button-ghost:          { type: button, bg: "transparent", fg: "#2ac1bc", border: "1px solid #2ac1bc", radius: "8px", active: "bg rgba(42,193,188,0.08)", use: "보조 액션 (나중에 하기, 목록 보기)" }
    button-neutral:        { type: button, bg: "#f8f9fa", fg: "#212529", radius: "8px", use: "3차 액션, 필터 토글" }
    button-destructive:    { type: button, bg: "#ff6b6b", fg: "#ffffff", radius: "8px", use: "기록 삭제, 인증 취소" }
    card:                  { type: card, bg: "#ffffff", radius: "8px", border: "1px solid #dee2e6", shadow: "0px 2px 8px rgba(0,0,0,0.08)", use: "표준 서피스, 강조 카드는 12px" }
    landmark-card:         { type: card, bg: "#ffffff", radius: "12px", padding: "16px", font: "name 18px / 700 #212529 · meta 13px / 400 #868e96", states: "미획득 사진 grayscale+35% · 획득일 캡션 #868e96", use: "랜드마크 수집 카드" }
    tag:                   { type: badge, bg: "#f1f3f5", fg: "#495057", radius: "9999px", font: "12px / 500", use: "지역 필터 칩 (수도권/광역시/기타)" }
    search-bar:            { type: input, bg: "#f8f9fa", fg: "#212529", radius: "20px", height: "44px", states: "left search icon #868e96 · placeholder #adb5bd", use: "지역·랜드마크 검색 (어디로 떠나볼까요?)" }
    input:                 { type: input, bg: "#ffffff", fg: "#212529", border: "1px solid #dee2e6", radius: "8px", focus: "2px solid #2ac1bc", states: "placeholder #adb5bd · error 2px solid #ff6b6b", use: "후기 작성, 닉네임 입력" }
    bottom-tab-bar:        { type: tab, bg: "#ffffff", border: "1px solid #dee2e6 top", active: "icon+label #2ac1bc", disabled: "#868e96 inactive", use: "홈·도감·인증·랭킹·마이 5탭 — 전원 같은 행" }
    top-app-bar:           { type: tab, bg: "#ffffff", fg: "#212529", font: "18px / 700 centered title", use: "서브 화면 헤더" }
    floating-locate-button: { type: button, bg: "#ffffff", fg: "#2ac1bc", radius: "9999px", height: "44px", border: "1px solid #dee2e6", shadow: "0px 4px 12px rgba(0,0,0,0.10)", use: "지도 위 내 위치 버튼 (탭바와 겹치지 않게 지도 우하단)" }
    badge:                 { type: badge, bg: "#ff6b6b", fg: "#ffffff", radius: "4px", font: "11px / 700", states: "신규 지역 #ff6b6b · 인증 가능 #2ac1bc", use: "신규/인증 가능 상태" }
    toast:                 { type: toast, bg: "#212529", fg: "#ffffff", font: "14px / 400", states: "2.5s auto-dismiss", use: "인증 완료 알림 (도감에 담겼어요)" }
---

# Design System of TripLog (트립로그)

## 1. Visual Theme & Atmosphere

TripLog is a Korean travel logging app that turns visiting the country's regions and landmarks into a collectible game — and its visual system borrows the warmth of Korea's most beloved consumer app language. The interface opens on a clean white canvas (`#ffffff`) with warm dark headings (`#212529`) and a fresh mint green (`#2AC1BC`) as the singular accent. This isn't the institutional blue of booking platforms; it's a warm, playful cyan-green chosen as a deliberate contrast — travel apps conventionally reach for sky blues and sunset oranges, and TripLog is the deliberate exception.

Typography stays functional. The UI runs on Pretendard (the project's bundled implementation of the system-sans role) — professional, legible, unobtrusive. Display personality is reserved for brand moments: level-up celebrations, splash screens, and campaign banners may push weight and size, but the core exploring/certifying UI never does. This creates a layered personality where the app is trustworthy but the brand stays playful.

The design philosophy is "playful warmth." The UX writing speaks like a friendly adventure mate, and the map — not the chrome — is the hero surface. The overall impression is of a well-worn travel diary's charm scaled to a nationwide collection game.

**Key Characteristics:**
- Mint (`#2AC1BC`) as the singular brand accent — warm, fresh, deliberately counter-category
- System-sans (Pretendard) for functional UI; display flourishes for personality moments only
- Card-based layout composition — "all information composed of card-format combinations"
- The map is the star: chrome floats above it, never crowds it
- Conversational UX writing — witty on exploration surfaces, calm on certification
- Five-variant shadow system (Natural through Crisp) for nuanced elevation

## 2. Color Palette & Roles

### Primary
- **TripLog Mint** (`#2AC1BC`): Primary brand color, CTA backgrounds, active tab states, visited-region fills. Warm cyan-green, instantly recognizable.
- **Pure White** (`#ffffff`): Page background, card surfaces. Clean and airy.
- **Dark Charcoal** (`#212529`): Primary heading and text color. Warm, not harsh.

### Accent
- **Primary Teal** (`#12B886`): Confirmation button fills, positive accents, 인증 성공 상태.
- **Teal Light** (`#20C997`): Hover/pressed states on teal elements, lighter accent.
- **Destructive Red** (`#FF6B6B`): Error states, destructive actions, 반경 이탈 표시.
- **Warning Orange** (`#FFB347`): Attention-needed states, 별점, 대기 상태.
- **Info Blue** (`#74C0FC`): General information, 현재 위치 마커, 위치 정확도 표시.
- **Promo Red** (`#FF0000`): High-urgency alerts, 이벤트 카운트다운.

### Neutral Scale
- **Text Primary** (`#212529`): Headings, 랜드마크 이름, strong labels.
- **Text Secondary** (`#495057`): Body text, 지역 소개, 후기 본문.
- **Text Tertiary** (`#868E96`): Captions, 방문 일자, secondary metadata.
- **Text Disabled** (`#ADB5BD`): Placeholder text, disabled labels.
- **Border Light** (`#DEE2E6`): Standard card borders, dividers, input borders.
- **Surface Fill** (`#F8F9FA`): Background fill, secondary canvas, 지도 배경.
- **Surface Subtle** (`#F1F3F5`): Tertiary background, input fills, 미획득 카드.

### Surface & Borders
- **Border Default**: `#DEE2E6`. Card borders, list dividers.
- **Border Strong**: `#343A40`. High-contrast borders for emphasis.
- **Overlay**: `rgba(0,0,0,0.5)`. Modal/sheet backdrops.
- **Overlay Dark**: `rgba(0,0,0,0.7)`. Full-screen image viewers.

## 3. Typography Rules

### Font Family
- **UI Primary**: `Pretendard, -apple-system, BlinkMacSystemFont, "Apple SD Gothic Neo", "Malgun Gothic", "Noto Sans KR", sans-serif`
- **Monospace**: `"SF Mono", SFMono-Regular, Menlo, Consolas, monospace`

TripLog ships Pretendard (Light/Regular/Bold) as the bundled implementation of the system-sans role — the same "functional type for functional UI" discipline as the reference system.

### Hierarchy

| Role | Font | Size | Weight | Line Height | Letter Spacing | Notes |
|------|------|------|--------|-------------|----------------|-------|
| Display Hero | UI | 42px | 700 | 1.20 | normal | 스플래시, 레벨업 축하 모먼트 |
| Display Large | UI | 36px | 700 | 1.25 | normal | 캠페인 타이틀, 섹션 히어로 |
| Heading Large | UI | 24px | 700 | 1.33 | normal | 홈 타이틀, 피처 섹션 제목 |
| Heading | UI | 20px | 700 | 1.40 | normal | 카드 헤딩, 지역 카테고리 |
| Title | UI | 18px | 600 | 1.44 | normal | 랜드마크 이름, 지역명 |
| Body Large | UI | 16px | 400 | 1.50 | normal | 지역 소개, 랜드마크 설명 |
| Body | UI | 14px | 400 | 1.57 | normal | 본문, 방문 후기 |
| Body Small | UI | 13px | 400 | 1.54 | normal | 보조 정보, 획득 조건 |
| Caption | UI | 12px | 400 | 1.50 | normal | 방문 일자, 거리 표시 |
| Score Display | UI | 20px | 700 | 1.30 | normal | XP, 스코어, 수집 수 |

### Principles
- **Dual personality**: Functional type for the exploring/certifying UI (지도, 도감, 인증, 랭킹), display flourishes for the experiential layer (축하 모먼트, 배너, 스플래시). The separation keeps the app professional while the brand stays playful.
- **Bold for clarity**: In a collection game, weight 700 is used liberally for landmark names, scores, and CTAs. Users scan quickly through many cards.
- **Numbers are content**: XP, 수집 수, 달성률 get the Score Display treatment — 700 weight, generous size. Progress is the product.

## 4. Component Patterns

TripLog is a mobile app surface (React Native/Expo) — Mint `#2AC1BC` primary, 8px radius, Pretendard for chrome. Specs below are the app's single component system, adapted from the reference's app surface.

### Actions

**Button — Primary (Brand Mint)**
- Background: `#2AC1BC`, Text: `#ffffff`
- Padding: 12px 24px, Radius: 8px, Height: 48px min
- Font: 16px weight 700
- Pressed: `#20A8A4` (darkened mint); Disabled: `#DEE2E6` bg, `#ADB5BD` text
- Use: Primary CTAs ("인증하기", "도감에 추가하기")

**Button — Ghost (Secondary)**
- Background: transparent, Text: `#2AC1BC`, Border: 1px solid `#2AC1BC`, Radius: 8px
- Pressed: `rgba(42,193,188,0.08)` background
- Use: Secondary actions ("나중에 하기", "목록 보기")

**Button — Neutral**
- Background: `#F8F9FA`, Text: `#212529`, Radius: 8px
- Use: Tertiary actions, filter toggles

**Button — Destructive**
- Background: `#FF6B6B`, Text: `#ffffff`, Radius: 8px
- Use: 기록 삭제, 인증 취소

**Floating Locate Button**
- 44px circle, `#ffffff` fill, mint compass/location icon, 1px `#DEE2E6` border
- Shadow: `0px 4px 12px rgba(0,0,0,0.10)` (Sharp / Level 3)
- Sits at map bottom-right, always clear of the tab bar (min 12px gap above it)
- Tap: recenters/zooms the map to current GPS position (네이버지도 위치 버튼 UX)

### Navigation

**Bottom Tab Bar**
- White bg, 1px `#DEE2E6` top border — all five tabs (홈·도감·인증·랭킹·마이) on the same row
- Active: `#2AC1BC` icon + label; Inactive: `#868E96`

**Top App Bar**
- White bg, centered title 18px weight 700, `#212529`

### Forms

**Search Bar**
- Background: `#F8F9FA`, Radius: 20px, Height: 44px
- Left search icon (`#868E96`), placeholder `#ADB5BD`, text `#212529`
- Full-width with 16px margin ("어디로 떠나볼까요?")

**Text Input**
- Border: 1px solid `#DEE2E6`, Radius: 8px
- Text: `#212529`, Placeholder: `#ADB5BD`
- Focus: 2px solid `#2AC1BC`; Error: 2px solid `#FF6B6B` + 13px/400 red error text below

### Data display

**Card / Container**
- Background: `#ffffff`, Border: 1px solid `#DEE2E6` or no border with shadow
- Radius: 8px (standard), 12px (featured); Shadow: `0px 2px 8px rgba(0,0,0,0.08)` (Deep / Level 2)

**Landmark Card** — key component
- Image: full-width, 1:1 or 4:5, 12px top radius
- Name: 18px weight 700, `#212529`
- 획득일/거리: 13px weight 400, `#868E96`
- Tags: pill (9999px radius), `#F1F3F5` bg, `#495057` text, 12px
- 미획득 상태: photo grayscale + 35% opacity, name `#ADB5BD`
- Internal padding: 16px

**Tag**
- `#F1F3F5` bg, `#495057` text, pill radius, 12px font weight 500

### Overlays

**Bottom Sheet / Modal**
- Rises from bottom, Outlined shadow `0px 4px 16px rgba(0,0,0,0.12)` (Level 4)
- Backdrop overlay `rgba(0,0,0,0.5)`; 인증 실패는 centered modal with 18px/700 `#212529` headline + two CTAs (primary mint, neutral cancel)

### Feedback & Status

**Badge**
- 신규 지역 badge: `#FF6B6B` or `#FFB347` bg, white text, 4px radius, 11px weight 700
- 인증 가능 badge: `#2AC1BC` bg, white text, 4px radius

**Toast**
- `#212529` bg, white 14px weight 400 text, 2.5s auto-dismiss ("도감에 담겼어요")

**Skeleton**
- `#F1F3F5` blocks at exact final card dimensions (photo slot, name row, meta row), shimmer ≤ 1.2s. 달성률 renders as `--%` (never `0%`, which reads as real data).

## 5. Layout Principles

### Spacing System
- Base unit: 8px
- Scale: 4px, 8px, 12px, 16px, 20px, 24px, 32px, 40px, 48px
- Card internal padding: 16px
- Section gaps: 24px-32px

### Grid & Container
- Mobile: full-width, 16px horizontal padding
- Content max-width: 768px for tablet/web
- Landmark grid: 3-column on mobile, 4-column on tablet
- Region chips: horizontal scroll with equal-height pills

### Whitespace Philosophy
- **The map breathes**: The home map gets the full viewport minus a compact header — chrome floats over it, never boxes it in.
- **Scan-friendly density**: Collection grids balance showing 6-9 cards per viewport with enough detail per card to feel collectible.
- **Card-format composition**: All service information is composed of card-format combinations that auto-transform based on device.

### Border Radius Scale
- Standard (4px): Small badges, status tags
- Comfortable (8px): Buttons, inputs, standard cards
- Featured (12px): Landmark cards, image containers
- Search (20px): Search bar, large rounded containers
- Pill (9999px): Region chips, filter tags, floating locate button

## 6. Depth & Elevation

| Level | Treatment | Use |
|-------|-----------|-----|
| Flat (Level 0) | No shadow | Page background, inline elements |
| Natural (Level 1) | `0px 1px 3px rgba(0,0,0,0.04)` | Subtle card separation, list items |
| Deep (Level 2) | `0px 2px 8px rgba(0,0,0,0.08)` | Standard landmark cards |
| Sharp (Level 3) | `0px 4px 12px rgba(0,0,0,0.10)` | Floating locate button, map toolbar |
| Outlined (Level 4) | `0px 4px 16px rgba(0,0,0,0.12)` | Bottom sheets, modal dialogs |
| Crisp (Level 5) | `0px 8px 24px rgba(0,0,0,0.16)` | Full-screen overlays, floating menus |

**Shadow Philosophy**: Five tiers — richer than most mobile apps, reflecting the layered nature of a map product where the map, region cards, certification sheets, and floating controls compete for attention. The naming (Natural, Deep, Sharp, Outlined, Crisp) uses evocative, human language rather than cold technical terms.

## 7. Do's and Don'ts

### Do
- Use TripLog Mint (`#2AC1BC`) as the primary brand accent for CTAs, active tabs, and visited-region fills
- Reserve display-size type for celebration moments (레벨업, 뱃지 획득) only
- Use functional type (Pretendard) for the core exploring/certifying UI — keep it trustworthy
- Make the map and landmark photography the star: floating chrome, no crowding
- Keep border-radius between 4px-12px for standard UI elements
- Use the conversational, warm tone for UX writing (모험 메이트 voice)
- Keep all five bottom tabs on one row — no raised center button

### Don't
- Don't use display flourishes for body text or functional UI — personality moments only
- Don't use heavy shadows on landmark photos — let the photography speak
- Don't introduce competing accent colors alongside mint — one-accent system
- Don't use cold, clinical blues for interactive elements — warm mint territory
- Don't use pure black (`#000000`) for text — `#212529` is the correct dark
- Don't apply mint to large background areas — it works as an accent, not a canvas
- Don't make certification/GPS flows "fun" — verifying a visit should be clear and trustworthy

## 8. Responsive Behavior

### Breakpoints
| Name | Width | Key Changes |
|------|-------|-------------|
| Mobile (Primary) | <480px | Full design, single column, 16px gutter |
| Tablet | 480-768px | Wider collection grids, expanded cards |
| Desktop (Web) | >768px | Centered content, max-width 768px |

### Touch Targets
- CTA buttons: minimum 48px height, full-width on mobile
- Landmark cards: entire card tappable, min 120px height
- Region chips: 44px minimum touch height
- Floating locate button: 44px circular, fixed bottom-right of the map, clear of the tab bar
- Map regions: generous hit areas; pinch-zoom to disambiguate small regions

### Collapsing Strategy
- Landmark grid: 3-column → keeps 3 columns with tighter gaps below 360px
- Region chips: horizontal scroll on all sizes, no wrapping
- Region detail: full-width sheet on mobile, side panel on tablet+
- Search: full-screen overlay on mobile, inline on desktop

### Image Behavior
- Landmark photos: 1:1 in grids, 4:5 in carousels, 12px radius
- Map: full-bleed within its area, pinch-zoom 1x-4x with pan
- Promotional banners: full-width, swipeable carousel

## 9. Agent Prompt Guide

### Quick Color Reference
- Primary CTA: TripLog Mint (`#2AC1BC`)
- CTA Pressed: Deep Mint (`#20A8A4`)
- Alternate CTA: Teal (`#12B886`)
- Background: Pure White (`#ffffff`)
- Background Surface: Light Gray (`#F8F9FA`)
- Heading text: Dark Charcoal (`#212529`)
- Body text: Dark Gray (`#495057`)
- Caption text: Medium Gray (`#868E96`)
- Placeholder: Soft Gray (`#ADB5BD`)
- Border: Light Gray (`#DEE2E6`)
- Error: Warm Red (`#FF6B6B`)
- Rating/Warning: Warm Orange (`#FFB347`)
- Current location: Info Blue (`#74C0FC`)

### Example Component Prompts
- "Create a landmark card: white bg, 12px radius. Square photo (12px top radius). Name 18px weight 700, #212529. 획득일 13px weight 400, #868E96. Locked state: photo grayscale + 35% opacity, name #ADB5BD. 16px padding."
- "Build a primary button: #2AC1BC bg, white text, 16px weight 700, 48px height, 8px radius, full-width. Pressed: #20A8A4."
- "Design the bottom tab bar: white bg, 1px #DEE2E6 top border. Five equal tabs on one row (홈·도감·인증·랭킹·마이). Active: #2AC1BC icon + 11px weight 600 label. Inactive: #868E96."
- "Build a floating locate button: 44px circle, white bg, 1px #DEE2E6 border, mint compass icon. Shadow: 0px 4px 12px rgba(0,0,0,0.10). Bottom-right of the map, at least 12px above the tab bar."
- "Design a search bar: #F8F9FA bg, 20px radius, 44px height. Left: 16px padding + #868E96 search icon. Placeholder '어디로 떠나볼까요?' in #ADB5BD. Text: #212529. Full-width with 16px margin."

### Iteration Guide
1. Functional type for UI; display flourishes for celebration moments only
2. Primary accent is `#2AC1BC` (TripLog Mint) — warm, not cold
3. The map and landmark photography are the centerpiece
4. Bold (700) used liberally for names, scores, CTAs — collection needs scannable bold
5. Border-radius: 8px buttons/inputs, 12px landmark cards, pill for chips
6. Five shadow levels: use Deep (Level 2) as default card shadow
7. Warm neutrals: #212529 headings, #F8F9FA backgrounds

---

## 10. Voice & Tone

TripLog's voice is **warm, witty, unmistakably Korean-vernacular** — a friendly adventure mate (모험 메이트) rather than a travel agency. It talks to users the way a well-traveled friend would: playful 요-endings on exploration surfaces, and a lightly celebratory register when a card lands in the 도감. The copy is allowed to be playful because browsing regions and collecting cards is low-stakes; GPS certification, data accuracy, and account surfaces drop the wit and become matter-of-fact. Korean is the primary voice — English UI strings are translations, not parity.

| Context | Tone |
|---|---|
| 탐험/홈 surfaces | Curious, inviting one-liners. "어디로 떠나볼까요" — evocative, never literal. |
| Category / service labels | Two- to four-character declarative nouns: `도감`, `인증`, `랭킹`, `마이`. Never English acronyms on primary nav. |
| CTAs on certification flow | Short Korean verb form (`인증하기`, `도감에 담기`, `기록 남기기`). Imperative but not curt. |
| Empty states | Explain the *why* conversationally in one line, suggest one action. Lightly playful tolerated; never a blank "데이터 없음". |
| Error messages | Blameless + specific + actionable. Humor retreats here — a failed GPS check is never funny. |
| Success toasts | Past-tense single sentence (`도감에 담겼어요`). Exclamation marks sparing; emoji promo-only. |
| 인증 진행 표시 | Matter-of-fact, present tense (`현재 위치를 확인하고 있어요`). Progression is the drama; copy stays calm. |
| 축하 모먼트 (레벨업, 뱃지) | Wordplay and warmth licensed here. "전국 8% — 지도가 점점 민트빛이에요!" |
| 계정 / 데이터 / 분쟁 | Formal 합니다 endings. Humor forbidden. The only surface where TripLog stops being playful. |

**Forbidden phrases.** `불편을 드려 죄송합니다` as a boilerplate opener (be specific instead), `Oops`, English `Sorry` on Korean UI, generic `데이터가 없습니다` / `오류가 발생했습니다`, and the harsh two-word summary `인증 실패`. On GPS-failure screens: state the cause and the fix (`현재 위치가 랜드마크 반경 밖에 있어요. 100m 안으로 다가가서 다시 시도해 주세요.`). Never use mint as a decorative tone cue in text — color is not voice.

## 11. Brand Narrative

TripLog (트립로그) is a university-team travel-tech project started in 2025 — a "여행의 다이어리이자 게임 컬렉션" (a travel diary that is also a game collection). The thesis: people book flights and hotels, but what they actually remember is *"내가 그 지역에 발을 디뎠고, 그 명소를 직접 눈에 담았다"* — so the product records exactly that. Users explore South Korea's 250 시/군/구, certify visits by GPS at real landmarks, and collect them as cards and badges that permanently color in their personal map of the country.

What TripLog refuses: the reservation-anxiety framing of incumbent travel platforms (price countdowns, scarcity banners); the cold utility aesthetic of pure navigation apps; and the harsh, mechanical error language of legacy Korean services. What it embraces: the deliberate counter-category mint green (`#2AC1BC`) — a "we are not a booking company" flag; the map as the single hero surface; and a voice that treats every visit as an achievement worth celebrating ("모든 방문은 성취다").

## 12. Principles

1. **Wit on exploration, calm on certification.** Browsing regions, opening the 도감, and celebration moments are licensed for playful copy and motion. GPS certification, data accuracy, and account screens drop the wit entirely. *UI implication:* one screen can host both registers — a playful map header above a matter-of-fact 인증 sheet. Never mix tones inside the same sentence.

2. **Every visit is an achievement.** Each region and landmark a user reaches becomes a permanent, visible collection entry. *UI implication:* map fills, 도감 updates, and badge reveals must render state change clearly and pleasurably — but only in the two licensed overshoot locations (§15).

3. **Mint is the counter-category signal.** `#2AC1BC` was chosen against travel-industry sky blues. *UI implication:* mint is an accent, not a canvas — CTAs, active tabs, visited-region fills, selection highlights. Flooding a screen with mint erases the signal.

4. **The map is the star; UI is the frame.** The home map's job is to make the country feel explorable. Chrome that crowds, overlays, or boxes the map fails the brief. *UI implication:* floating controls with clear margins (locate button never overlaps the tab bar), full-bleed map area, gesture-first navigation (pinch-zoom, pan).

5. **Scannable bold for collection screens.** Collection is a high-choice, quick-scan context (17 regions, hundreds of cards). *UI implication:* 700-weight for landmark names, scores, and CTAs. Reserve 400 for descriptions and metadata.

6. **Progressive density, spacious summaries.** Home and region overviews are spacious; the 도감 grid and record lists are denser. *UI implication:* as the user commits (홈 → 지역 → 랜드마크 → 인증), padding tightens and rows compress.

7. **Card-format composition.** Every major block (지역, 랜드마크, 뱃지, 기록) is a self-contained card with its own radius and padding. Cards never overlap, and adjacent cards never share a border — spacing is the separator.

8. **Trustworthy motion on money-equivalent surfaces.** In TripLog the "money surface" is certification — the moment of proof. Motion there is standard-easing and precise; bounce belongs to celebration only.

## 13. Personas

*Personas are fictional archetypes representing TripLog's core loops (수집·기록·경쟁), not individual people.*

**민지 (Min-ji), 24, Suwon.** 성취 지향 수집가. Weekend day-tripper who plans routes around which badges she can collect next. The map coloring in one cell at a time is her core dopamine loop; she screenshots her map every month.

**준우 (Jun-woo), 28, Seoul.** 감성 기록가. Visits landmarks to archive the moment — GPS-certifies, attaches photos, writes a short entry. Cares that his collection page looks neat and curated; grid alignment matters to him more than rank.

**지훈 (Ji-hoon), 32, Daejeon.** 랭킹 경쟁가. Checks the weekly ranking tab first. Plans long-distance trips specifically for first-visit bonuses. A ranking that updates slowly or ambiguously would move him to stop competing.

## 14. States

| State | Treatment |
|---|---|
| **Empty (미방문 도감)** | White canvas. One line of `#495057` body text (14px): *"아직 방문한 랜드마크가 없어요. 첫 번째 모험을 시작해 보세요!"* Below: one mint CTA *"탐험하러 가기"*. Locked cards show as `#F1F3F5` tiles with `???` labels. |
| **Empty (검색 결과 없음)** | `#495057` body text, neutral and specific: *"'<query>' 검색 결과가 없어요. 다른 지역으로 찾아보시겠어요?"* Suggested-region chips follow below. Never a "sorry" apology. |
| **Loading (지도)** | Map skeleton: `#F1F3F5` land shapes, shimmer ≤ 1.2s. Region stats show `--%` until resolved — never `0%`. |
| **Loading (위치 확인)** | Blue location dot pulses between two opacity values over 1.5s while GPS resolves. Distance shows `--m` until server returns. |
| **Loading (인증 전송)** | Full-width mint button shows a 3-dot white animation replacing the label. Button width does not change; the press is committed. |
| **Error (inline field)** | 2px `#FF6B6B` border on the input. Error text below in red (13px weight 400). One actionable sentence. |
| **Error (반경 이탈)** | Modal — not a transient toast. Headline 18px weight 700 `#212529`: *"현재 위치가 랜드마크 반경 밖에 있어요"*. Body 14px explaining distance remaining. Two CTAs: *"다시 시도"* (primary mint) and *"닫기"* (neutral). No humor on this surface. |
| **Error (network)** | Top banner, `#343A40` bg, white text, one sentence (*"일시적으로 연결이 불안정해요"*) + retry pill. Auto-dismisses when connectivity returns. |
| **Success (인증 완료)** | Dedicated confirmation screen — not a toast. Mint checkmark top-center, landmark name 20px weight 700, XP breakdown in 14px. Single primary CTA *"도감 보러 가기"*. Certification is ceremonial — the record matters. |
| **Success (카드 획득)** | Bottom toast, `#212529` bg, white 14px text, 2.5s auto-dismiss (*"도감에 담겼어요"*). |
| **Skeleton** | `#F1F3F5` blocks at exact final card dimensions. Shimmer 1.2s. Scores render as `--` — never `0`, which reads as real data. |
| **Disabled** | Button background drops to `#DEE2E6`, text to `#ADB5BD`. Radius stays 8px. Disabled 인증 CTA shows *why* inline above the button (e.g., "랜드마크 100m 안에서 인증할 수 있어요"), not as a separate toast. |

## 15. Motion & Easing

TripLog's motion is **warm, responsive, and slightly playful** — more kinetic than a booking app's restraint, more disciplined than a game. Spring and overshoot are **licensed in two narrow places only** (badge/card acquisition reveal, favorite/like toggle) because the brand leans playful and those two moments are the product's reward beats. Everywhere else — map navigation, certification, account — motion is standard-easing and functional. A spring on the "인증하기" button would read as unserious with proof; a spring on a newly earned badge reads as a deserved celebration.

**Durations** (named, not raw milliseconds):

| Token | Value | Use |
|---|---|---|
| `motion-instant` | 0ms | Toggle commits, checkbox state changes |
| `motion-fast` | 150ms | Press feedback, focus, thumbnail tap |
| `motion-standard` | 250ms | The default — sheet opens, card expands, tab switches, toast appear |
| `motion-slow` | 400ms | 인증 성공 체크마크 stroke, badge reveal |
| `motion-page` | 300ms | Route transitions between top-level tabs |

**Easings:**

| Token | Curve | Use |
|---|---|---|
| `ease-enter` | `cubic-bezier(0.0, 0.0, 0.2, 1)` | Things appearing — sheets, toasts, screen pushes |
| `ease-exit` | `cubic-bezier(0.4, 0.0, 1, 1)` | Things leaving — dismissals, pops |
| `ease-standard` | `cubic-bezier(0.4, 0.0, 0.2, 1)` | Two-way transitions — card expand/collapse, map drill-down, tab content |
| `ease-bounce` | `cubic-bezier(0.34, 1.56, 0.64, 1)` | **Licensed only for:** badge/card acquisition reveal and favorite toggle. Overshoot on CTAs, certification, or map navigation is forbidden — proof precision outranks kinetic delight. |

**Signature motions.**

1. **Card tap feedback.** Card scales `1.0` → `0.98` over `motion-fast / ease-standard` on press, returns on release. Tactile but not bouncy.
2. **Badge acquisition.** Badge scales `1.0` → `1.1` → `1.0` over `motion-standard` with `ease-bounce`. One of two places overshoot is allowed — this is the reward beat.
3. **Favorite toggle.** Icon fills over `motion-fast` with a brief scale pulse (`1.0` → `1.15` → `1.0`) using `ease-bounce`. Second and final licensed overshoot location.
4. **Map drill-down.** National → province transition is a crossfade + slight scale (`0.97` → `1.0`) over `motion-standard / ease-standard`. No spring — navigation stays composed.
5. **인증 성공.** Mint checkmark draws over `motion-slow` with `ease-standard` (not `ease-bounce` — proof-related completions stay precise). XP rows reveal below with a 100ms staggered fade.
6. **Locate button recenter.** Map camera animates to current position over `motion-standard / ease-standard`; the blue dot pulses once on arrival.
7. **Reduce motion.** Under reduce-motion settings, all `motion-*` tokens collapse to `motion-instant`. `ease-bounce` is suppressed entirely — acquisitions swap to a crossfade. Motion is never load-bearing for comprehension.
