# Figma Node 1:26200 — Screen Exports Available

Source file: `FVVplMUExRWDAY1npMxRmj` — Check2Go

Original node ID: `1:26200`.

The earlier recovery attempt was blocked by the connected View seat's MCP read-call limit. On 2026-09-13, direct page metadata was successfully read and all 49 top-level screen frames were exported. See [the image catalog](screens/README.md) for the images and individual Figma links.

## Current substitute source

A 49-page PDF export of the Check2GO mobile screens was provided and fully parsed in the recovery session. Its screen-level information is documented in:
- `docs/design/pdf-export-2026-09-13.md`
- `docs/screen-inventory.md`
- `docs/flows.md`

## Follow-up

For implementation, inspect individual frames with design-context tools, compare them against the PDF-derived screen inventory, and record details that static image exports do not fully preserve, including:
- exact component/variant structure;
- auto-layout and constraints;
- exact spacing and sizing;
- Figma variables/style bindings;
- hidden states/layers;
- prototype transitions;
- assets not visible in the PDF export.
