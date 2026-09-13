# Check2GO App

Private recovery and rebuild repository for the Check2GO mobile applications.

## Platform decision

Check2GO will be rebuilt as two native applications:

- **Android:** Kotlin + Jetpack Compose
- **iOS:** Swift + SwiftUI

The historical Flutter/Dart application is retained only as a recovery and behavior reference.

## Current recovery sources

This repository combines four sources of truth:

1. **Latest Android APK** — actual client behavior and recoverable historical Flutter structure.
2. **Older APK versions** — historical screens, assets and removed functionality.
3. **AWS backend (2025)** — recovered Amplify/AppSync/Cognito/DynamoDB infrastructure and data model.
4. **Figma + current product decisions** — target UI/UX and current design system.

## Current status

- Historical Android package: `co.check2go`
- Latest inspected app version: `0.5.0`
- Historical client technology: Flutter / Dart
- Target Android technology: Kotlin / Jetpack Compose
- Target iOS technology: Swift / SwiftUI
- Historical backend: AWS Amplify Gen 1, AppSync, Cognito, DynamoDB
- AWS region: `eu-north-1`
- Recovery work is in progress; this is not yet a production-ready source tree.

## Repository structure

```text
app/android/                  native Android application
app/ios/                      native iOS application
backend/amplify/              safe recovered Amplify configuration
backend/graphql/              recovered historical GraphQL schema
docs/                         architecture, recovery and product documentation
legacy/flutter-reference/     historical Flutter recovery notes only
```

## Shared product contract

Android and iOS remain separate codebases, but they should share the same product requirements, Figma design system, API/GraphQL contract, authentication rules, analytics specification, localization content and QA acceptance criteria.

## Security

Do not commit API keys, AWS credentials, Cognito secrets, signing keys, `.env` files, keystores, provisioning profiles or raw backend exports containing credentials.

See `docs/platform-strategy.md` and `docs/recovery-map.md` for current reconstruction status.
