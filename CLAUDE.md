# Check2GO — Claude Development Rules

Check2GO is being rebuilt as two separate native applications. Android is currently the active implementation track.

## Current platform priority

- Android: Kotlin + Jetpack Compose — active
- iOS: Swift + SwiftUI — separate codebase, do not modify unless a task explicitly requests it
- historical Flutter code/APKs are recovery references only

## Read before coding

Use these files as the primary project contract:

1. `docs/source-priority.md`
2. `docs/product-definition.md`
3. `docs/screen-inventory.md`
4. `docs/flows.md`
5. `docs/platform-parity.md`
6. `docs/design/design-tokens.md`
7. `docs/android/architecture.md`

For a feature tied to Figma/PDF recovery, also inspect the relevant files under `docs/design/`.

## Source priority

When sources conflict:
1. current product-owner decision;
2. current Figma / current PDF design;
3. latest recovered APK behavior;
4. historical AWS artifacts;
5. older APKs/designs;
6. pitch/business concept.

Do not silently resolve contradictions. Document them or ask for a product decision.

## Android rules

- Work only under `app/android/` plus shared docs required by the task.
- Use Kotlin and Jetpack Compose.
- Use the architecture in `docs/android/architecture.md`.
- Keep composables focused on rendering and UI interaction.
- Keep business rules out of composables.
- Do not create abstractions until a real feature needs them.
- Do not connect the recovered AWS dev backend unless the task explicitly approves an API/auth contract.
- Do not change package/application id `co.check2go` without explicit approval.

## Design rules

- Use the authoritative Check2GO tokens from `docs/design/design-tokens.md`.
- Do not enable Material dynamic color for Check2GO brand surfaces.
- Do not invent missing Figma dimensions, spacing, radii, typography or assets.
- When exact design information is unavailable, use a clearly marked temporary implementation rather than presenting guessed values as final.

## Git workflow

- Never develop directly on `main`.
- Use `android/<task>` branches for Android work.
- Keep changes scoped to the task.
- Open a Pull Request to `main`.
- Describe what changed and what was tested.
- Do not merge your own significant change without review when another collaborator is available.

## Validation before PR

For Android code, run the narrowest relevant checks and report the result. Once the Gradle wrapper is committed, the baseline build command is:

```bash
cd app/android
./gradlew :app:assembleDebug
```

Run tests that exist for the touched feature. Do not claim a successful build/test if it was not actually run.

## Security

Never commit credentials, API keys, tokens, signing files, `.env` data, keystores, production user data, or raw recovered backend exports containing secrets.

If a task appears to require one of those, stop and request a safe configuration approach instead.
