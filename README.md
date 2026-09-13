# Check2GO App

Private recovery and rebuild repository for the Check2GO mobile application.

## Current recovery sources

This repository combines four sources of truth:

1. **Latest Android APK** — actual client behavior and recoverable Flutter structure.
2. **Older APK versions** — historical screens, assets and removed functionality.
3. **AWS backend (2025)** — recovered Amplify/AppSync/Cognito/DynamoDB infrastructure and data model.
4. **Figma + current product decisions** — target UI/UX and current design system.

## Current status

- Android package: `co.check2go`
- Latest inspected app version: `0.5.0`
- Client technology: Flutter / Dart
- Historical backend: AWS Amplify Gen 1, AppSync, Cognito, DynamoDB
- AWS region: `eu-north-1`
- Recovery work is in progress; this is not yet a production-ready source tree.

## Repository structure

```text
app/flutter/             rebuilt Flutter application
backend/amplify/         safe recovered Amplify configuration
backend/graphql/         recovered historical GraphQL schema
docs/                    architecture, recovery and product documentation
legacy/                  historical/sandbox references only
```

## Security

Do not commit API keys, AWS credentials, Cognito secrets, signing keys, `.env` files, keystores, or raw backend exports containing credentials.

See `docs/recovery-map.md` for the current reconstruction status.
