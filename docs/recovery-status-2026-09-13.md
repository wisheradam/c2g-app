# Recovery Status — 2026-09-13

## Decisions locked

- Rebuild as two native apps.
- Android: Kotlin + Jetpack Compose.
- iOS: Swift + SwiftUI.
- Flutter is legacy/reference only.
- Shared backend/API/product/design contracts must prevent platform drift.
- Core UI color space: sRGB.

## Sources recovered / inspected

### APK
Latest inspected Android APK:
- package `co.check2go`
- version `0.5.0`
- Flutter/Dart historical client
- SHA-256 documented in `legacy/flutter-reference/apk-2026-02-02.md`

### AWS
Historical 2025 Amplify Gen 1 backend is recoverable, including:
- AppSync GraphQL
- Cognito
- DynamoDB
- Amplify deployment artifacts
- original historical schema

A separate 2026 sandbox contains only a `Todo` schema and is not equivalent to the real 2025 application backend.

### Figma
Inspected nodes:
- `57:27590` — project description / concept
- `61:28105` — competitor analysis / viability
- `39:27565` — visual references
- `1:26193` — UI kit

Pending direct read:
- `1:26200` — blocked by the connected Figma View-seat MCP monthly call limit at the time of recovery.

### PDF design export
- Source file: `Check2Go.pdf`
- 49 pages
- Contains actual mobile UI states covering Trips, Add Trip, Travelers, Reminders, Trip Hub and Checklists.
- Functional content is captured in `docs/screen-inventory.md` and `docs/flows.md`.

The original PDF is approximately 145 MB, larger than GitHub's normal single-file limit and too large for the text-oriented connector workflow. It is therefore not committed as a raw binary in this recovery pass.

## Current authoritative design tokens

Light:
- primary `#043CB3`
- textPrimary `#002349`
- textSecondary / disabledText `#8D919A`
- background `#F4F7FA`
- error `#B3261E`

Dark:
- primary `#043CB3`
- textPrimary `#D0D0D0`
- disabledSurface `#3A4F66`
- background `#253C55`
- textSecondary / disabledText `#8D919A`
- error `#B3261E`

## Major recovered product areas

- Home / My Trips
- Add Trip flow
- Ticket/boarding-pass loading
- Travelers and reusable traveler profiles
- Pets in trip composition
- Reminders/deadlines
- Trip Hub
- Checklists
- Checklist templates and progress
- File attachments/documents
- Checklist create/edit/duplicate/share
- Notifications
- Events calendar
- Historical collaboration/subscription concept
- Historical accommodation/transfer/car-rental partner modules

## Source precedence

1. Current product-owner decisions
2. Current Figma / current PDF export
3. Latest APK
4. Historical AWS artifacts
5. Older APKs / older designs
6. Pitch deck

## Next reconstruction work

1. Assign stable shared IDs to every screen and flow.
2. Produce domain/data-model document by reconciling APK local models, AWS GraphQL and current screens.
3. Produce API contract for the native apps.
4. Build native design-system components in Compose and SwiftUI.
5. Implement Stage 1 preparation/checklist flows first.
6. Compare any older APKs as a corpus when supplied.
7. Re-open pending Figma nodes after MCP quota reset or seat upgrade.
