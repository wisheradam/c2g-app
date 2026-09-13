# Check2GO App

Private recovery and rebuild repository for the Check2GO mobile applications.

## Platform decision

Check2GO will be rebuilt as two native applications:

- **Android:** Kotlin + Jetpack Compose
- **iOS:** Swift + SwiftUI

The historical Flutter/Dart application is retained only as a recovery and behavior reference.

## Current recovery sources

This repository combines the following sources of truth:

1. **Latest Android APK** — actual client behavior and recoverable historical Flutter structure.
2. **Older APK versions** — historical screens, assets and removed functionality.
3. **AWS backend (2025)** — recovered Amplify/AppSync/Cognito/DynamoDB infrastructure and data model.
4. **Figma** — current product concept, UI kit, references and competitor analysis.
5. **49-page Check2GO PDF export** — current/historical mobile screen states used as the primary visual recovery source while Figma MCP is rate-limited.
6. **Current product-owner decisions** — highest-priority source when they conflict with historical artifacts.

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
docs/                         architecture, recovery, product, flow and design documentation
legacy/flutter-reference/     historical Flutter/APK recovery notes only
```

## Core documentation

- `docs/product-definition.md` — product positioning, layers, audience and staged roadmap.
- `docs/source-priority.md` — authority/confidence rules for conflicting artifacts.
- `docs/screen-inventory.md` — screen inventory reconstructed from the 49-page PDF.
- `docs/flows.md` — recovered core user flows.
- `docs/platform-strategy.md` — native-platform decision.
- `docs/platform-parity.md` — rules for keeping Android and iOS behavior aligned.
- `docs/recovery-map.md` — AWS/backend recovery status.
- `docs/design/design-tokens.md` — current authoritative light/dark color tokens.
- `docs/design/figma-source-map.md` — inspected Figma nodes and their role.
- `docs/design/pdf-export-2026-09-13.md` — PDF source record and page grouping.
- `legacy/flutter-reference/apk-2026-02-02.md` — latest inspected APK recovery notes.

## Shared product contract

Android and iOS remain separate codebases, but they should share the same product requirements, screen IDs, Figma design system, API/GraphQL contract, authentication rules, analytics specification, localization content and QA acceptance criteria.

## Design source rule

The explicit Figma `Colors_light` and `Colors_dark` values are the active design tokens. Older/conceptual palette notes found on the same Figma page must not silently override them.

For exported product/design assets, use **sRGB** as the default color space for cross-platform consistency.

## Security

Do not commit API keys, AWS credentials, Cognito secrets, signing keys, `.env` files, keystores, provisioning profiles or raw backend exports containing credentials.

The GitHub connector used during this recovery session supports UTF-8 text writes, not direct binary PDF upload. The functional content of the supplied PDF has therefore been captured in repository documentation; the original PDF can be added manually later if desired.
