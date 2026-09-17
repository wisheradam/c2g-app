# Personal account — initial data contract

Status: initial Android/domain scope. UI is intentionally deferred until an approved Figma design exists.

## Principles

- The model is versioned (`schemaVersion = 1`) so it can be migrated later.
- Age is derived from date of birth for a requested reference date; it is not persisted separately and therefore cannot become stale.
- Citizenship is a set and supports multiple countries.
- Country values remain string codes until the shared country catalogue/API contract is approved.
- Family members have stable IDs and can be edited or removed independently.
- Interests have stable IDs and can be added or removed independently.
- Consent history is append-only: every decision stores accepted/declined status, timestamp, and legal-text version.
- Marketing and product-update consents are optional and can be withdrawn. Required account consents cannot be withdrawn through the optional-preferences operation.
- Account deletion is represented as an explicit lifecycle request rather than immediately erasing local state.

## Included data

The v1 contract covers identity, gender, date of birth, derived age, structured address, multiple citizenships, current residence, residence permits, country entry restrictions/travel bans, relatives, interests, privacy/consent history, and account-deletion status.

The predefined relationship list is: spouse, partner, child, parent, sibling, grandparent, grandchild, legal guardian, dependent, and other. `Other` requires a manually entered value.

## Deferred integration

- Compose screens and navigation require approved designs.
- Room entities/migrations require an approved local retention and encryption policy.
- Remote DTOs require the production API and authentication contract.
- Account deletion execution requires backend lifecycle, re-authentication, grace-period, and audit requirements.
- Country-specific recommendation logic will consume this model but is not encoded into the profile itself.
