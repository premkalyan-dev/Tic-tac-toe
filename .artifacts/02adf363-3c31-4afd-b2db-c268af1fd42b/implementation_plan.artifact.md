# Android 16 / API 36 Upgrade Plan

This plan outlines the steps to upgrade the Tic-tac-toe application to target Android 16 (API 36), ensuring compliance with Google Play requirements while maintaining app stability and look-and-feel.

## User Review Required

> [!IMPORTANT]
> The application is currently a **Tic-tac-toe** app, although the initial request mentioned Sudoku. I will proceed with upgrading the Tic-tac-toe codebase.

> [!NOTE]
> `compileSdk` is currently set to 37 in the project, which is unusual. I will align it to 36 (Android 16) as requested.

## Proposed Changes

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/build.gradle.kts)
- Update `compileSdk` to 36.
- Update `targetSdk` to 36.

### UI & Behavior Changes (Android 16 Compatibility)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/MainActivity.kt)
- Add `enableEdgeToEdge()` to comply with Android 15+ edge-to-edge requirements, which are mandatory for apps targeting API 35+.

#### [MODIFY] [TicTacToeUI.kt](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/src/main/java/com/prem/tic_tac_toe/ui/TicTacToeUI.kt)
- Add `Modifier.systemBarsPadding()` to the root container to ensure the UI is not obscured by the status bar or navigation bar.

## Verification Plan

### Automated Tests
- Run `gradlew test` (Unit tests).
- Run `gradlew connectedAndroidTest` (Instrumentation tests).

### Manual Verification
- **Build Validation**: Ensure successful Gradle sync and clean build.
- **Launch Test**: Verify the app launches correctly on an Android 16 emulator (or latest available).
- **UI Audit**: Check that the "Settings" button and game grid are correctly positioned relative to system bars.
- **Game Logic**: Verify game play, stats reset, and settings work as expected.
- **AAB Generation**: Verify `gradlew bundleRelease` completes and the AAB targets API 36.
