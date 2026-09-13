# Check2GO Android Architecture

Status: initial native Android architecture for the rebuild.

## Platform

- Kotlin
- Jetpack Compose
- single-activity application
- Android package / application id: `co.check2go`

## Architecture direction

Use a pragmatic layered architecture with feature-oriented code organization.

```text
Compose UI
  ↓
Screen state / ViewModel
  ↓
Use cases when business logic warrants them
  ↓
Repository contracts
  ↓
Local + Remote data sources
```

The historical Flutter BLoC/repository structure is evidence about product behavior, not a requirement to reproduce its framework architecture.

## Package direction

```text
co.check2go/
  app/                 app-level navigation and composition
  core/
    design/            reusable design-system primitives
    model/             shared app models where justified
    data/              shared data infrastructure
  feature/
    home/
    trip/
    travelers/
    checklists/
    documents/
    events/
  ui/theme/            Check2GO Compose theme and tokens
```

Do not create empty abstraction layers just to match this diagram. Add packages when a real feature requires them.

## State management

Preferred Android-native approach:
- immutable UI state;
- `ViewModel` as screen-level state holder where state survives configuration changes;
- Kotlin coroutines / `StateFlow` for asynchronous state;
- UI emits explicit user actions/events;
- business rules stay outside composables.

Small stateless screens do not need a ViewModel.

## Navigation

Use one activity and Compose navigation. Product documentation uses stable shared screen IDs such as `HOME_EMPTY`, `TRIP_CREATE_DATES` and `CHECKLIST_DETAIL`; Android route names may differ internally but must map back to those shared IDs.

Navigation details should be added only as screens are implemented. Do not prebuild a large route graph from historical APK screens that are not in the approved current scope.

## Data direction

The product is intended to support local-first/offline-friendly behavior.

Planned layers:
- local persistence: Room is the default candidate;
- remote API: approved AppSync/GraphQL contract;
- authentication: approved Cognito contract;
- repositories reconcile local/remote data.

Do not connect the recovered historical AWS environment directly from the new client until the production API/auth contract is approved.

## Design system

Authoritative color roles are in `docs/design/design-tokens.md`.

Rules:
- no dynamic Material color for brand surfaces;
- do not invent Figma spacing/radius/typography values when they are unknown;
- platform-native Android behavior is allowed for system pickers, permissions, haptics and accessibility;
- product behavior and business rules must remain parity-compatible with iOS.

## Testing direction

Per feature:
- unit-test business/state transformations;
- Compose UI tests for critical user paths;
- repository tests around local/remote reconciliation once data layers exist;
- parity acceptance criteria come from shared product docs.

## Security

Never commit:
- AWS access/secret keys;
- API keys or OAuth secrets;
- `google-services.json` containing environment-specific configuration without explicit review;
- keystores or `key.properties`;
- `.env` files;
- production user data.
