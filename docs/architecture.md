# Check2GO Recovery Architecture

## Target reconstruction layers

```text
Flutter UI
  ↓
BLoC / state management
  ↓
Use cases / repositories
  ↓
Local persistence (Drift / SQLite)
  ↕
AWS client integration
  ↓
Cognito + AppSync
  ↓
DynamoDB
```

## Current source mapping

| Layer | Best recovery source |
|---|---|
| UI layout and assets | APK + Figma |
| Navigation and client flows | APK |
| State-management names/structure | APK |
| Local database concepts | APK |
| Cloud domain model | AWS 2025 backend |
| Authentication | AWS Cognito + APK integration |
| GraphQL relations and operations | AWS AppSync / Amplify |
| Target visual system | Current design tokens + Figma |

## Rebuild principle

The new repository should contain clean, maintainable Flutter source rather than patched decompiled code. Historical artifacts are references used to verify behavior and data contracts.
