# Check2GO Screen Inventory

Source: 49-page exported Check2GO design PDF supplied on 2026-09-13.

Status labels in this document:
- **Verified** — directly visible in the PDF.
- **Strong inference** — relationship/behavior is evident from adjacent states.

## 1. Home / Trips

### Home — empty state
Verified.
- Header: Check2go
- Hero: “Start your first journey!”
- Supporting copy: add trips to the app
- CTA: Add a trip
- Bottom navigation: Home / Documents / Checklists / Events
- Floating `+` action

PDF pages: 2, 5.

### My trips — list view
Verified.
- Header / page title
- Filter tabs: Active / All
- Trip cards with image, status, route/name and date
- Add a trip CTA
- Toggle between larger/list-style and compact/grid-style presentation
- Example cards: London - Tel Aviv; Tel Aviv - Abu Dhabi

PDF pages: 1, 6–9.

## 2. Add Trip flow

### Destination / trip start
Verified.
- Destination country input
- Autofill
- Departure country
- Trip name
- Start CTA
- Background travel image
- Keyboard state is represented

PDF pages: 4, 11, 12.

### Travel dates
Verified.
- Step indicator
- Quick Fill panel
- Boarding-pass upload
- One-way No/Yes segmented control
- Departure date
- Return date when not one-way
- Travel reminder toggle
- Back / Next step

PDF pages: 3, 13, 14, 16, 17.

### Load ticket bottom sheet
Verified.
- Choose from gallery
- Choose from files
- Recently downloaded files
- File thumbnails/name/size

PDF page: 15.

### Date picker bottom sheet
Verified.
- Calendar month navigation
- Selected date
- Save date

PDF page: 18.

### Travelers step
Verified.
- Solo adventure / Group adventure
- My travelers list for group mode
- Add traveler
- Pets Yes/No
- Back / Complete

PDF pages: 19, 20, 25, 26.

## 3. Travelers

### My travelers — empty state
Verified.
- Empty-state illustration
- Explanation of reusable traveler profiles
- Add a new traveler CTA

PDF page: 21.

### Add / edit traveler
Verified.
- Profile photo
- Replace photo
- Full name
- Gender selection
- Age field/dropdown
- Family affiliation
- Save changes

PDF pages: 22, 23.

### Replace photo bottom sheet
Verified.
- Choose from gallery
- Make a photo
- Delete photo

PDF page: 23.

### My travelers — populated
Verified.
- Traveler row/card
- Avatar
- Name
- Relationship/family role
- Edit icon
- Add a new traveler

PDF page: 24.

### Traveler row swipe/delete state
Verified.
- Edit action
- Destructive delete action revealed from the row

PDF page: 26.

## 4. Reminder / Date-Time Picker

Verified reusable flow.
- Calendar date selection
- Time selection
- Date/time chips
- Disabled and enabled CTA states
- “Set a reminder” mode
- “Set deadline” mode

PDF pages: 27–29 and 38–40.

## 5. Trip Detail / Trip Hub

Verified as a long composite trip-detail screen with multiple sections.

Visible sections/capabilities:
- Trip header/status/date
- Quick services: Accommodation / Taxi / Rent a Car / My smart home
- Checklists with progress
- Create new one
- Blogger routes / route recommendations
- Events calendar
- Documents summary
- Team checklists upsell
- What to see nearby
- Restaurants and bars
- Famous photo locations
- Transfer booking
- Car rental request
- Accommodation selection
- Travel album creation variant

PDF pages: 30–33.

Strong inference: several pages are variants of the same trip-hub screen where promotional/service modules differ.

## 6. Checklists — main tab

### Empty state
Verified.
- No active checklists
- Create checklist
- Team checklists promo/free-trial banner
- Bottom navigation

PDF page: 34.

### Populated list
Verified.
Checklist examples:
- Transfer
- Accommodation
- Documents
- Take on a trip
- Before leaving
- Things to take with you
- For pets
- For packing children
- For packing babies

Each row shows trip/personal context and completion percentage.

PDF page: 35.

## 7. Checklist Detail

### Documents checklist
Verified.
- Completion checkboxes
- Items such as Passport, Visa, Car license, Health insurance
- Upload file action per eligible item
- Attached scans/files shown under an item
- Promotional accommodation module inside checklist content
- Notification controls
- Edit checklist
- Duplicate
- Delete checklist
- Completion progress
- Share checklist

PDF pages: 36, 37.

### Take on a trip checklist
Verified.
- Simple completed checklist items
- Team checklists promo embedded between items
- Multiple notifications
- Edit / Duplicate / Delete
- Completion percentage
- Share checklist

PDF page: 46.

### Transfer checklist
Verified.
- Travel/airport-transfer preparation tasks
- Embedded car-rental promo
- Notifications
- Edit / Duplicate / Delete
- Completion percentage
- Share checklist

PDF page: 47.

### Accommodation checklist
Verified.
- Accommodation booking/upload item
- How to get to accommodation
- Team-checklists promo
- Notifications
- Edit / Duplicate / Delete
- Completion percentage
- Share checklist

PDF page: 48.

## 8. Create / Edit Checklist

Verified.

Fields and actions represented across states:
- Checklist name
- Optional checklist photo
- Link with trip
- Add to another trip
- Section name
- Item/point name
- Include file toggle/option
- Add item
- Add section
- Use now
- Share checklist
- Duplicate checklist
- Delete checklist
- Save changes / Save checklist

PDF pages: 41–43, 45, 49.

### Duplicate success bottom sheet
Verified.
- “A copy has been created”
- Go back
- Use now

PDF page: 41.

### Replace checklist photo bottom sheet
Verified.
- Choose from gallery
- Make a photo
- Delete photo

PDF page: 43.

## 9. Checklist Sharing / Monetization

Verified historical design state.
- Share checklist bottom sheet
- Subscription price shown as $5/month
- 7-day free trial message
- Find out more CTA

PDF page: 44.

Important: treat the exact price as historical design evidence, not an automatically approved current commercial price.

## 10. Events

Verified partial behavior from supplied screens.
- Events calendar section in trip hub
- World Events Calendar
- Event date selection bottom sheet/calendar
- Example holiday/event entries

PDF pages: 10, 30–33.

## 11. Documents

Verified at least as:
- bottom-navigation destination;
- checklist-integrated document/file storage;
- trip-detail document summary;
- uploads/scans attached to checklist items.

The PDF does not provide a full standalone Documents-tab flow comparable in depth to the checklist flow.

## 12. Persistent navigation and chrome

Verified across many screens:
- top header/back navigation;
- settings icon;
- help/question icon;
- notification bell;
- bottom nav: Home / Documents / Checklists / Events;
- floating `+` button on main-tab screens.

## 13. Platform implementation mapping

Every screen above should receive a shared screen ID and acceptance criteria, then be implemented separately in:
- Android Jetpack Compose;
- iOS SwiftUI.

Shared behavior should be defined first; visual details may use platform-native conventions where appropriate.
