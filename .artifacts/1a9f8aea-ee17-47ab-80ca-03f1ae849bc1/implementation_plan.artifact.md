# Implementation Plan - Google Play Store Policy Compliance & Cleanup

This plan addresses the requirements for the **Three Win** (Tic-Tac-Toe) app to comply with Google Play Store policies and improve app quality.

## User Review Required

> [!IMPORTANT]
> The following signing files are currently tracked by Git:
> - `release-keystore.jks`
> - `threewin-release.jks`
> - `upload_certificate.pem`
>
> I will add them to `.gitignore`, but they will remain in the Git history. It is highly recommended to remove them from the history if they are sensitive.

## Proposed Changes

### 1. App Name Consistency

#### [MODIFY] [strings.xml](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/res/values/strings.xml)
- Change `app_name` from "Tic-Tac-Toe" to "Three Win".

### 2. Privacy Policy & About Section

#### [NEW] [Constants.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/util/Constants.kt)
- Define `PRIVACY_POLICY_URL` placeholder.

#### [MODIFY] [TicTacToeUI.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/ui/TicTacToeUI.kt)
- Add "Privacy Policy" link to `SettingsDialog`.
- Add "About" section to `SettingsDialog` showing app name, version, and developer name (if found).
- Implement intent to open the Privacy Policy URL.

### 3. Signing Key Security

#### [MODIFY] [.gitignore](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/.gitignore)
- Add `*.jks`, `*.keystore`, `*.pem`, and `local.properties` to ensure they are ignored in the future.

### 4. Ads Code Cleanup

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/ui/HomeScreen.kt)
- Remove `bannerAd` parameter and its call site.

#### [MODIFY] [Navigation.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/ui/Navigation.kt)
- Update `HomeScreen` call site to remove `bannerAd` if necessary (though it uses default `{}`).

### 5. Accessibility Improvements

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/ui/HomeScreen.kt)
- Add `contentDescription` to icons: `EmojiEvents`, `icon` in `ModeCard`.

#### [MODIFY] [TicTacToeUI.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/ui/TicTacToeUI.kt)
- Add `contentDescription` to icons: `resultIcon` in `GameResultDialog`, `statusIcon` in `StatusIndicator`.

### 6. R8 / Release Build Optimization

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/build.gradle.kts)
- Enable R8/minification for the release build.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure it builds.
- Run `./gradlew assembleRelease` to ensure the release build (with R8) succeeds.

### Manual Verification
- Verify app name "Three Win" in the launcher.
- Verify Privacy Policy link opens the browser.
- Verify "About" section displays correct version.
- Verify no ads code remains in `HomeScreen`.
- Verify accessibility descriptions via inspection.
