# Check2GO Product Definition

Status: recovery/rebuild working specification.

## Product direction

Check2GO is a checklist-first mobile travel-planning product. The core value is to reduce pre-trip chaos and anxiety by giving travelers a clear, structured way to prepare step by step.

The product is not intended to begin as a generic travel super-app. Its strongest wedge is trip preparation: checklists, packing, documents, reminders and shared preparation. Broader itinerary, route, recommendation, booking and payment capabilities can be layered on later.

## Core product layers

### 1. Preparation
- Checklists
- Packing
- Tasks
- Documents
- Reminders
- Deadlines

### 2. Trip planning
- Trip creation
- Dates
- Travelers
- Routes
- POI
- Events
- Recommendations

### 3. Collaboration
- Shared lists
- Shared preparation
- Task distribution
- Shared trip details

### 4. Transaction layer — later stage
- Reservation import/storage
- Booking redirects
- Accommodation selection
- Transfers
- Car rental
- Direct booking/payment only after the preparation/trip-hub foundation is mature

## Audience

Primary:
- travelers roughly 23–40;
- digital-native planners;
- couples, friends and small groups.

Secondary:
- families;
- anxious planners;
- people who are afraid of forgetting important things;
- users who want one organized place instead of many apps.

## Brand personality

- Reliable
- Clear
- Supportive
- Modern
- Lightweight
- Organized

Avoid visual/product behavior that feels chaotic, decorative, childish or overly romanticized.

## UX principles

- Clarity first
- Checklist-driven hierarchy
- Calm interface and reduced cognitive load
- Strong trust cues for future transaction features
- Scalable component system
- Fast one-handed checklist interaction
- Clear status/progress feedback

## Product positioning

Working positioning:

> A checklist-first travel app that helps people prepare for a trip without chaos.

Alternative phrasing:

> The clearest and calmest way to prepare a trip step by step.

## Competitor interpretation from Figma research

- Wanderlog / Stippl: closest all-in-one planning references.
- TripIt: benchmark for reservations/documents and post-booking organization.
- PackPoint: benchmark for checklist/packing simplicity.
- Polarsteps: benchmark for emotional journey/storytelling and engagement.

Check2GO should differentiate through the combination of checklist-first preparation, calm shared planning and a future path into reservations/transactions.

## Recommended staged development

### Stage 1 — Win the wedge
- Smart checklists
- Packing
- Documents
- Reminders/deadlines
- Shared preparation

### Stage 2 — Build the trip hub
- Itinerary
- Routes
- Places
- Events
- Shared trip view

### Stage 3 — Transaction-adjacent
- Reservation import
- Confirmation/document storage
- Price/booking redirects
- Accommodation/transfer/car-rental partner flows

### Later
- Direct booking and payments

## Current platform decision

Two separate native apps:

- Android: Kotlin + Jetpack Compose
- iOS: Swift + SwiftUI

The old Flutter application remains only as a recovery and behavior reference.
