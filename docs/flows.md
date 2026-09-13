# Check2GO Core User Flows

This document consolidates the user flows recovered from the current Figma/PDF designs and latest APK evidence.

## Flow 1 — First launch to first trip

1. Open Home empty state.
2. Tap `Add a trip`.
3. Choose destination country / departure country / trip name.
4. Continue to travel dates.
5. Optionally upload boarding pass/ticket for Quick Fill.
6. Choose one-way vs return trip.
7. Choose departure/return dates.
8. Optionally enable a travel reminder.
9. Continue to Travelers.
10. Choose Solo adventure or Group adventure.
11. In group mode, add/select traveler profiles.
12. Choose whether pets are included.
13. Complete trip creation.
14. Trip appears in My trips / Trip Hub.

## Flow 2 — Ticket upload / Quick Fill

1. From Add Trip > Travel dates, tap `Load your ticket`.
2. Bottom sheet opens.
3. Choose from gallery, choose from files, or select a recent download.
4. One or more tickets may be attached.
5. Attached ticket rows appear in the Quick Fill area and can be removed.
6. App may use OCR/parsing to prefill travel details where supported.

Latest APK evidence also supports OCR/file-processing behavior. Treat exact parsing behavior as implementation detail requiring validation.

## Flow 3 — Add reusable traveler

1. Enter Travelers step or My travelers.
2. Tap `Add traveler` / `Add a new traveler`.
3. Add/replace profile photo.
4. Enter full name.
5. Select gender.
6. Enter/select age information.
7. Select family affiliation/relationship.
8. Save changes.
9. Traveler becomes reusable in trip creation.

## Flow 4 — Manage traveler photo

1. Tap Replace photo.
2. Choose from gallery, take a photo, or delete existing photo.
3. Return to traveler editor.
4. Save changes.

## Flow 5 — Checklist discovery

1. Open Checklists tab.
2. If no active checklist exists, show empty state.
3. User can create a checklist.
4. When checklists exist, show them with context and completion percentage.
5. Tap a checklist to open detail.

## Flow 6 — Checklist completion

1. Open checklist detail.
2. Scan grouped or flat checklist items.
3. Toggle item completion.
4. Progress updates.
5. For items that allow files, attach/upload supporting document.
6. Add notifications/deadlines if needed.
7. Use Edit, Duplicate, Delete or Share actions when appropriate.

## Flow 7 — Create checklist

1. Tap Create checklist.
2. Enter checklist name.
3. Optionally add/replace checklist image.
4. Optionally link checklist to a trip.
5. Add items and/or sections.
6. Mark items that should include files.
7. Save changes.
8. Optionally `Use now`.

## Flow 8 — Edit checklist

1. Open checklist detail.
2. Tap Edit checklist.
3. Change title/image/trip linkage/items/sections/file requirements.
4. Save changes.

## Flow 9 — Duplicate checklist

1. From detail/editor, choose Duplicate checklist.
2. System creates a copy.
3. Confirmation bottom sheet appears.
4. User chooses Go back or Use now.

## Flow 10 — Reminder / deadline

1. Tap Set notification / Add notification / reminder control.
2. Open date-time picker.
3. Choose calendar date.
4. Choose time.
5. Confirm with Set a reminder / Set deadline.
6. Saved notification appears in checklist/trip state.

## Flow 11 — Share checklist

Historical design flow:
1. Tap Share checklist.
2. If sharing requires subscription, show monetization bottom sheet.
3. Historical copy references $5/month and a 7-day free trial.
4. Continue to Learn more / subscription flow.

The exact price and entitlement rules are historical evidence only and require current product approval.

## Flow 12 — Trip Hub

1. Open a trip from My trips.
2. See trip status/name/date.
3. Access quick services.
4. Review linked checklist progress.
5. Create or open checklists.
6. Review routes/recommendations.
7. Review events around destination.
8. Access document summary.
9. Explore places/food/photo locations.
10. Use partner/service modules such as accommodation, taxi, transfer or car rental where enabled.

## Navigation model

Historical/current design evidence shows a four-tab bottom navigation:
- Home
- Documents
- Checklists
- Events

Main screens also expose a floating `+` action.

The latest APK recovery contains additional internal screens and modules, including Profile, Settings, Notifications and People. These should be reconciled with the current Figma/PDF navigation before implementation.
