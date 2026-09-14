# Check2GO Android

Native Android target for Check2GO.

## Target stack

- Kotlin
- Jetpack Compose
- Android SDK
- AWS backend integration through AppSync/Cognito APIs
- Local persistence to be selected during rebuild (Room is the default candidate)

## Bootstrap baseline

The native Android project is bootstrapped under this directory with:

- Android Gradle Plugin 9.4.0
- Gradle 9.6.0 configuration
- Compose BOM 2026.08.00
- Activity Compose 1.13.0
- AndroidX Core 1.19.0
- `compileSdk = 37`
- `targetSdk = 36`
- `minSdk = 24`
- Java 17 bytecode target
- package / application id `co.check2go`

The first screen is intentionally a temporary developer bootstrap screen. It must be replaced by the first approved product screen (`HOME_EMPTY`) rather than treated as product UI.

## Design system rule

Authoritative Check2GO light/dark colors come from `docs/design/design-tokens.md` and are mapped into the Compose theme. Dynamic Material color is intentionally disabled so brand colors do not drift by device.

Do not invent missing spacing, typography, radius, icon or component values. Use Figma/PDF evidence or document a deliberate product decision first.

## Recovery source

The historical Flutter APK remains a behavioral and architectural reference only. Android implementation is rebuilt natively.

## Build note

`gradle-wrapper.properties` is committed. The wrapper scripts/JAR should be generated from Gradle 9.6.0 in the development environment before the first CI build and committed in a follow-up change.
