# Contributing to Check2GO

This repository is developed collaboratively. The `main` branch should remain stable and should not be used for day-to-day direct development.

## Branch workflow

Create a dedicated branch for each task.

Recommended naming:
- `android/<short-task>`
- `ios/<short-task>`
- `backend/<short-task>`
- `design/<short-task>`
- `docs/<short-task>`
- `fix/<short-task>`

Examples:
- `android/add-trip-flow`
- `ios/checklist-screen`
- `backend/travel-api`
- `docs/update-screen-inventory`

## Pull requests

All completed work should be merged into `main` through a Pull Request.

Before opening a PR:
1. Pull the latest `main`.
2. Resolve merge conflicts in the feature branch.
3. Verify that no secrets, credentials, signing files, API keys or private environment files are included.
4. Test the affected flow locally when code is involved.
5. Describe what changed and what was tested.

## Review rule

For now, use at least one review from the other collaborator before merging significant code changes.

Small documentation-only fixes may be merged with a lighter review process, but direct pushes to `main` should still be avoided.

## Shared product contract

Android and iOS are separate native codebases, but they must stay aligned on:
- screen behavior;
- business rules;
- API contracts;
- data models;
- localization keys and copy;
- analytics event names;
- design tokens;
- QA acceptance criteria.

If Android and iOS intentionally differ because of platform-native UX conventions, document the difference in the Pull Request.

## Security

Never commit:
- AWS access keys or secret keys;
- API keys or tokens;
- `.env` files with credentials;
- Android keystores or `key.properties`;
- iOS signing certificates or provisioning profiles;
- Cognito client secrets;
- raw backend exports that contain secrets.

Recovered historical artifacts must be sanitized before they are committed.
