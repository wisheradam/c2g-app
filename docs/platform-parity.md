# Android / iOS Platform Parity

The Check2GO rebuild uses two native codebases. This file defines what must remain functionally aligned across them.

## Target stacks

- Android: Kotlin + Jetpack Compose
- iOS: Swift + SwiftUI

## Must stay equivalent

Both apps should share the same:
- product requirements;
- screen IDs and feature names;
- business rules;
- data model semantics;
- API/GraphQL contracts;
- authentication behavior;
- checklist calculations/progress rules;
- reminder/deadline semantics;
- localization keys and copy;
- analytics event names;
- design tokens;
- error states;
- entitlement/subscription rules;
- QA acceptance criteria.

## Allowed platform differences

Platform-native differences are acceptable for:
- navigation gestures;
- system date/time pickers where appropriate;
- permission prompts;
- file/photo pickers;
- typography rendering differences;
- haptics;
- system share sheets;
- Android Material vs Apple Human Interface Guidelines behavior;
- platform-specific accessibility behavior.

## Parity checklist per feature

Before a feature is marked complete, verify:
1. Same user goal is achievable on both platforms.
2. Same required input fields and validation rules.
3. Same backend payload semantics.
4. Same success/failure outcomes.
5. Same analytics event contract.
6. Same localization keys exist.
7. Same design token roles are used.
8. Platform-specific behavior is documented, not accidental.

## Shared naming convention

Each product screen should have a stable shared ID, for example:
- `HOME_EMPTY`
- `HOME_TRIPS`
- `TRIP_CREATE_DESTINATION`
- `TRIP_CREATE_DATES`
- `TRIP_CREATE_TRAVELERS`
- `TRAVELERS_LIST`
- `TRAVELER_EDIT`
- `TRIP_HUB`
- `CHECKLISTS_HOME`
- `CHECKLIST_DETAIL`
- `CHECKLIST_EDIT`
- `REMINDER_PICKER`

Native route/class names may differ, but documentation and analytics should reference the shared ID.
