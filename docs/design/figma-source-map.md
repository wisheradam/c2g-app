# Figma Source Map

Figma file: `FVVplMUExRWDAY1npMxRmj` — Check2Go.

This document records the design nodes already inspected during recovery and how they should be used.

## Node `57:27590` — Project description / Concept

Contains product and UX concept material rather than final mobile screens.

Key direction:
- checklist-first global travel-planning app;
- preparation is the primary wedge;
- future expansion into itinerary, recommendations, booking and payments;
- calm, reliable, structured, modern product personality;
- checklist UX should be simple, scannable, groupable and one-handed;
- emotional travel imagery should be used selectively, mainly in promotional/landing contexts.

Typography recommendation in this concept area:
- Inter primary;
- Display 56/64 Bold;
- H1 40/48 Bold;
- H2 32/40 Bold;
- H3 24/32 SemiBold;
- H4 20/28 SemiBold;
- Body L 18/28 Regular;
- Body M 16/24 Regular;
- Body S 14/20 Regular;
- Caption 12/16 Regular/Medium;
- Button 16/20 SemiBold.

## Node `61:28105` — Competitor analysis

Contains competitor and product-viability analysis, not final UI screens.

Main references:
- Wanderlog and Stippl — broad all-in-one travel planning;
- TripIt — reservations/documents;
- PackPoint — packing/checklist simplicity;
- Polarsteps — emotional journey/storytelling.

Strategic conclusion: Check2GO should enter through checklist-first preparation and shared planning before expanding into a larger trip hub and transaction layer.

## Node `39:27565` — References

Reference/moodboard page, not a final Check2GO design.

Observed reference patterns:
- trip cards with image/date/status;
- schedule/itinerary timelines;
- checklist/task rows;
- map + list combinations;
- travel booking cards;
- bottom navigation;
- step-by-step flows;
- functional UI combined with selective travel imagery.

Use this only as visual inspiration. Do not reproduce third-party reference designs literally.

## Node `1:26193` — UI-kit

This is a key implementation source.

Verified component families include:
- button states: Default / Hover / Active / Disabled;
- Header;
- iPhone Tab Bar;
- app inputs;
- radio controls;
- date/time reminder picker;
- checklist rows;
- Points-checklist;
- destination-country form examples;
- keyboard examples.

### Authoritative current color tokens

Light theme:
- Primary: `#043CB3`
- Primary text: `#002349`
- Secondary/inactive text and icons: `#8D919A`
- Background: `#F4F7FA`
- Error: `#B3261E`

Dark theme:
- Primary: `#043CB3`
- Primary text: `#D0D0D0`
- Disabled surface: `#3A4F66`
- Background: `#253C55`
- Secondary/inactive text and icons: `#8D919A`
- Error: `#B3261E`

The same page also contains older/conceptual palette notes with different values. Do not mix those values into the current implementation unless explicitly approved. The explicit `Colors_light` and `Colors_dark` blocks above are the active source.

## Node `1:26200`

Not yet read through MCP because the connected Figma View seat hit its MCP read-call limit.

A PDF export of the mobile screens has been provided separately and is now the primary visual recovery source until direct Figma reads are available again.

## Implementation mapping

Shared design specification should map to native components, not a shared UI framework.

Android examples:
- `Check2GoTheme`
- `C2GButton`
- `C2GInput`
- `C2GChecklistItem`
- `C2GTabBar`

iOS examples:
- `Check2GoTheme`
- `C2GButton`
- `C2GInput`
- `C2GChecklistItem`
- `C2GTabBar`

The behavior and visual intent should remain aligned, while platform-native interaction conventions are allowed where they improve usability.
