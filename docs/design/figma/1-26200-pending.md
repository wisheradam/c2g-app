# Figma Node 1:26200 — Pending Direct MCP Read

Source file: `FVVplMUExRWDAY1npMxRmj` — Check2Go

Original node ID: `1:26200`.

Direct Figma MCP reading of this node was attempted after the connected View seat had reached its MCP read-call limit, so no direct node metadata/context was captured in that attempt.

## Current substitute source

A 49-page PDF export of the Check2GO mobile screens was provided and fully parsed in the recovery session. Its screen-level information is documented in:
- `docs/design/pdf-export-2026-09-13.md`
- `docs/screen-inventory.md`
- `docs/flows.md`

## Follow-up

When Figma MCP access resumes, re-read node `1:26200`, compare it against the PDF-derived screen inventory, and record any details that PDF cannot preserve well, including:
- exact component/variant structure;
- auto-layout and constraints;
- exact spacing and sizing;
- Figma variables/style bindings;
- hidden states/layers;
- prototype transitions;
- assets not visible in the PDF export.
