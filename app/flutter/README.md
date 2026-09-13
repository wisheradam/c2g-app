# Flutter App Recovery

This directory will contain the rebuilt Check2GO Flutter source project.

## Known facts from the latest inspected APK

- Package: `co.check2go`
- Version: `0.5.0`
- Flutter/Dart application
- BLoC-style state management
- Local SQLite/Drift persistence
- AWS Amplify client integration
- Cognito/AppSync configuration present
- Local notifications/reminders
- Camera/files/contact access
- OCR capability via ML Kit
- Light and dark themes

## Recovered feature areas

- Authorization
- Timeline
- Trips
- Checklists
- Documents
- People / Travelers
- Notifications
- Profile
- Settings

## Recovery rule

Do not treat decompiled APK output as maintainable source code. Rebuild a clean Flutter project while preserving verified behavior, data contracts and assets.
