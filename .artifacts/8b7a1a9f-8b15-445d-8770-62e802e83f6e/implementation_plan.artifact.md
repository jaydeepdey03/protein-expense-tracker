# Implementation Plan - Fix Theme Switching and Toggle Status

The app currently fails to switch to light mode when the system is in dark mode because the toggle sets the theme to `SYSTEM` instead of `LIGHT`. Additionally, the toggle doesn't correctly reflect the current theme status when the system theme is dark.

## User Review Required

> [!IMPORTANT]
> The theme toggle will now explicitly set the app to `LIGHT` or `DARK` mode when interacted with. This means it will override the system theme preference until the user resets it (if a reset option is added in the future, though currently, we are just using a binary toggle).

## Proposed Changes

### UI Components

#### [MODIFY] [ProfileScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/ui/screens/ProfileScreen.kt)

- Calculate the effective theme status (`isDark`) using `isSystemInDarkTheme()` and the current `themeMode`.
- Update the `Switch` to use `isDark` as its `checked` state.
- Update `onCheckedChange` to explicitly set `ThemeMode.DARK` or `ThemeMode.LIGHT`.

## Verification Plan

### Manual Verification
- **System Dark, App Dark**: Toggle should be ON.
- **Toggle OFF**: App should switch to LIGHT. Toggle should be OFF.
- **System Dark, App Light**: Toggle should be OFF.
- **Toggle ON**: App should switch to DARK. Toggle should be ON.
- **System Light, App Light**: Toggle should be OFF.
- **Toggle ON**: App should switch to DARK. Toggle should be ON.
