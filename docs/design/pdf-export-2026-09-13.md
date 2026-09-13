# Check2GO Design PDF Export — 2026-09-13

Source filename: `Check2Go.pdf`

The supplied export contains 49 pages of current/historical Check2GO mobile UI states and is being used as the primary visual recovery source while the connected Figma View seat is rate-limited.

## Export guidance

For product/UI exports, use **sRGB** as the default color space. It is the safest cross-platform choice for Android, iOS, web, screenshots, PDF handoff, QA and general design/dev consistency.

Display P3 may be used later for selected photography or marketing imagery, but core UI colors and design tokens should remain specified in sRGB.

## What the PDF covers

The export includes:
- Home empty state;
- My trips states and list/grid variants;
- Add Trip destination entry;
- Travel dates;
- ticket/boarding-pass loading;
- date picker;
- solo/group travelers;
- traveler management;
- reminder/deadline picker;
- trip detail / trip hub;
- Checklists empty/populated states;
- checklist detail;
- file uploads;
- checklist notifications;
- create/edit/duplicate/share checklist flows;
- historical team-checklist subscription messaging;
- transfer/accommodation/checklist variants;
- events calendar states.

## Page groups

- Pages 1–9: Home / My trips / Add trip entry states
- Pages 10–18: events/date selection and travel-date/ticket states
- Pages 19–26: Travelers and traveler management
- Pages 27–29: reminder picker
- Pages 30–33: trip hub variants
- Pages 34–35: Checklists tab empty/populated
- Pages 36–37: Documents checklist detail
- Pages 38–40: deadline picker
- Pages 41–45: checklist create/edit/duplicate/share
- Pages 46–48: checklist variants (Take on a trip / Transfer / Accommodation)
- Page 49: blank/simple checklist creation state

## Repository handling

The GitHub connector available in this recovery workflow supports UTF-8 text file creation/update but not direct binary PDF upload. Therefore:
- the PDF-derived information is preserved in `docs/screen-inventory.md`, `docs/flows.md` and this source record;
- the original binary PDF should be added to the repository manually later if the team wants the raw export version-controlled.

Do not treat the absence of the binary PDF in GitHub as loss of the recovered functional specification.
