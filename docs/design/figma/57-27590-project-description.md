# Figma Node 57:27590 — Project Description / Concept

Source file: `FVVplMUExRWDAY1npMxRmj` — Check2Go

Status: **captured from Figma MCP before the read limit was reached**.

This node contains product/UX concept material in Russian and English. It is not a final mobile screen set.

## Product definition captured from the node

Check2GO is presented as a **checklist-first global mobile travel-planning app** with future expansion toward booking, purchasing and payments.

Primary product value:
- reduce chaos and anxiety before a trip;
- give users control, clarity, structure and readiness;
- keep trip preparation in one place;
- make checklists the central UX logic rather than a secondary feature.

## Product layers

1. **Preparation**
   - checklists;
   - packing;
   - tasks;
   - documents;
   - reminders;
   - deadlines.

2. **Trip planning**
   - trip creation;
   - routes;
   - points of interest;
   - events;
   - recommendations.

3. **Collaboration**
   - shared lists;
   - shared plans;
   - task distribution;
   - shared trip details.

4. **Transaction layer — future**
   - booking;
   - purchasing;
   - payments;
   - confirmations;
   - transactional data.

## Target audience

Primary:
- 23–40-year-old digital-native travelers;
- planners;
- couples;
- friends;
- small groups.

Secondary:
- families;
- anxious planners;
- users worried about forgetting things;
- users who prefer one app instead of many separate travel tools.

## Brand personality

The design concept describes the product as:
- Reliable
- Clear
- Supportive
- Modern
- Lightweight
- Organized

The intended experience should not feel chaotic, decorative, childish or overly travel-romantic.

## Design principles

- clarity first;
- checklist-driven hierarchy;
- calm interface and lower cognitive load;
- trustworthy enough for future transaction scenarios;
- scalable design system;
- clean + structured + travel-inspired visual direction.

Functional UI should remain strict and clear. More emotional travel imagery belongs mainly in landing/promotional contexts rather than in dense task-oriented flows.

## Typography recommendation captured from Figma

Primary family: **Inter**.

- Display: 56/64 Bold
- H1: 40/48 Bold
- H2: 32/40 Bold
- H3: 24/32 SemiBold
- H4: 20/28 SemiBold
- Body L: 18/28 Regular
- Body M: 16/24 Regular
- Body S: 14/20 Regular
- Caption: 12/16 Regular or Medium
- Button: 16/20 SemiBold

## Color-system concept

The concept describes the system by roles:
- Brand / Primary blue;
- Text Primary dark navy;
- Text Secondary muted gray-blue;
- Background light neutral;
- Surface white / near-white;
- Surface Secondary light gray;
- Border light neutral;
- semantic Success / Warning / Error.

Exact implementation colors are controlled by the later UI-kit and are documented in `docs/design/design-tokens.md`.

## Component system proposed in the concept

Buttons:
- Primary
- Secondary
- Tertiary
- Text
- Icon
- states: default / hover / pressed / disabled / loading

Inputs:
- text
- search
- email
- password
- date
- select
- checkbox
- radio
- toggle

Navigation:
- top navigation for landing contexts;
- bottom tab navigation for the app;
- tabs;
- filters;
- segmented controls.

Status UI:
- badges;
- chips;
- labels;
- progress;
- tags;
- pills.

Product-specific components:
- Trip card
- Checklist item variants
- Checklist section
- Document row
- Event / recommendation card
- Route item
- future Booking module

Suggested first components to formalize:
- Buttons
- Inputs
- Tabs
- Bottom navigation
- Trip cards
- Checklist items
- Checklist sections
- Status chips
- Document rows
- landing Feature cards
- Store badges
- CTA blocks

## Checklist UX direction

Checklist experience should be:
- simple;
- scannable;
- not visually dense;
- groupable;
- status-clear;
- comfortable for one-handed mobile use.

Important distinctions and interactions:
- section vs item hierarchy;
- progress visibility;
- quick completion feedback;
- clear icons;
- collaboration without visual noise.

## Tone of voice examples captured from the concept

- “Stay organized”
- “Don’t forget important details”
- “Keep everything in one place”
- “Prepare your trip step by step”

## Recovery rule

This node is a **concept/product source**. It should guide product direction and design intent, but current direct product decisions and the active UI-kit take precedence where there is a conflict.
