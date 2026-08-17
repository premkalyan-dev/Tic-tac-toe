# Target Android 16 (API 36) Migration Plan

This plan outlines the steps to update the Tic-Tac-Toe application to target Android 16 (API level 36) as required by Google Play Console policies.

## User Review Required

> [!IMPORTANT]
> The project currently targets API 35. I will update it to API 36. This requires `compileSdk` and `targetSdk` updates.
> I will verify the build and AAB generation.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/premk/StudioProjects/Tic-tac-toe/app/build.gradle.kts)
- Update `compileSdk` to 36.
- Update `targetSdk` to 36.

## Verification Plan

### Automated Tests
- Run `./gradlew clean` to ensure a fresh build.
- Run `./gradlew assembleRelease` to verify the APK can be generated.
- Run `./gradlew bundleRelease` to verify the AAB can be generated for Google Play.

### Manual Verification
- Inspect the generated build report to confirm `targetSdk` is 36.
