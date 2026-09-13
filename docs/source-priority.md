# Source Priority and Confidence Rules

This file defines how conflicting recovery evidence should be resolved during the Check2GO rebuild.

## Priority order

When two sources disagree, use the following order unless a deliberate product decision overrides it:

1. Current direct product decisions from the product owner.
2. Current Figma design and exported Figma/PDF screens.
3. Latest recovered APK behavior.
4. Historical AWS backend artifacts.
5. Older APK versions and historical designs.
6. Pitch deck / business concept material.

The pitch deck is a concept and strategy source, not an automatic MVP specification.

## Confidence labels

- **Verified** — directly observed in a current source such as Figma, PDF, APK metadata, AWS, or a direct product decision.
- **Strong inference** — supported by multiple artifacts but not directly confirmed as the intended current behavior.
- **Hypothesis** — plausible reconstruction that still requires validation.

## Platform decision

The target product is two native applications:

- Android: Kotlin + Jetpack Compose
- iOS: Swift + SwiftUI

Historical Flutter/Dart artifacts are recovery references only.

## Shared cross-platform contract

Android and iOS should share:

- feature definitions and acceptance criteria;
- screen IDs and navigation semantics;
- domain/data model definitions;
- API and GraphQL contracts;
- authentication rules;
- analytics event names;
- localization keys and product copy;
- design tokens;
- QA scenarios and parity checks.

Platform-specific UI should still follow Android and Apple conventions where appropriate.

## Security rule

Never commit raw AWS credentials, API keys, Cognito secrets, signing keys, Android keystores, provisioning profiles, `.env` files, or recovered backend exports that contain secrets.
