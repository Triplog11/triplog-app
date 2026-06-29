/**
 * TripLog Design System Theme - Inspired by Triple
 * Authoritative color tokens, border radius, and spacing variables.
 */
export const theme = {
  colors: {
    // Brand / Interactive
    primary: '#368FFF',         // TripLog Blue
    primaryPressed: '#1A85E8',  // Pressed blue
    skyAccent: '#1AADF6',       // Sky blue for illustration/accent
    blueTint: '#F1F7FF',        // Light blue background for cards/info
    blueWash: 'rgba(54, 143, 255, 0.07)', // Selected state translucent background

    // Surfaces
    canvas: '#FFFFFF',          // Page/screen background
    surface: '#F5F6F7',         // Section gray grouping background

    // Text
    textPrimary: '#1B1D1F',     // Main headlines, user names, high contrast
    textBody: '#3A3E41',        // Regular body copy, place/badge names
    textSecondary: '#7E848A',   // Subtitles, metadata, dates, distance
    textTertiary: '#ADB1B5',    // Form placeholders, disabled text

    // Borders & Dividers
    border: '#E8EAEC',          // Default light borders, dividers
    borderStrong: '#D5D8DB',    // Input active border, focused lines

    // Semantic States
    success: '#22C55E',         // Visited completed state, badge acquired
    highlightTeal: '#0ECEDB',   // Map active radius highlights
    error: '#FF3B30',           // Validation error, radius out of bound
    warning: '#FAAD14',         // Warning alert, pending actions
  },

  rounded: {
    sm: 12,                     // Small items, input fields, snackbars
    md: 12,                     // Medium items
    lg: 20,                     // Cards, modal dialogs, bottom sheets
    pill: 36,                   // Primary button height 56px standard radius
    full: 9999,                 // Filter chips, badges
  },

  spacing: {
    xs: 4,
    sm: 8,
    md: 12,
    base: 16,
    lg: 24,
    xl: 32,
    xxl: 48,
  },
};

export default theme;
