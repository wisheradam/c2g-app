# Platform Strategy

## Decision

Check2GO will be rebuilt as two native mobile applications sharing the same product requirements and backend contract:

- Android: Kotlin + Jetpack Compose
- iOS: Swift + SwiftUI

The existing Flutter application is a recovery source, not the target runtime.

## Shared layers

The two applications should share product behavior through documentation and backend contracts rather than a shared UI codebase. Shared sources of truth:

1. Product requirements and user flows
2. Figma/design system
3. GraphQL/API schema
4. Authentication rules
5. Analytics event specification
6. Localization keys/content
7. Acceptance criteria and QA scenarios

## Platform independence

Each platform may use native patterns where appropriate. Visual identity and product behavior should remain aligned, but navigation, gestures, system pickers, permissions, notifications and accessibility should follow native platform conventions.

## Recovery approach

Historical Flutter APKs will be analyzed feature by feature. Each recovered feature will be mapped to two implementation tracks: Android and iOS. No recovered Flutter class should be copied conceptually without validating the current product requirement.
