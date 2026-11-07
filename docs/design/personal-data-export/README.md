# Personal data export design

The approved concept is the visual reference. The subsequent iOS direction uses standard grouped
sections and the existing buttons with default corner radii.

![Approved three-state concept](approved-concept.png)

| State | Rendered iOS preview |
|---|---|
| Request | [Screenshot](request.png) |
| Preparing | [Screenshot](preparing.png) |
| Ready | [Screenshot](ready.png) |

The screenshots render the production `PersonalDataExportContent` with sample screen state in an
isolated simulator preview app. The ready-state expiry is an example. No backend operation or passkey
confirmation is performed, and no UI-test dependencies or screenshot assertions are added.

The implementation uses neutral headings, a document or success icon, separate passkey confirmation,
and a file row with expiry details. iOS uses standard inset-grouped `List`/`Section` containers, native
row separators and section footers, and the original `StateButton`/`TextButton` components. It adds no
custom button border, shape or corner radius. Android follows the concept's cards and outlined refresh.
Both platforms use the same shared state for icons, action visibility and helper text.

The current request description explains that JSON is a structured data format and describes its uses.
The helper text refers to “the app”. Status is observed from Room; the default state shown here is the
cached never-requested state, which opens without a network refresh.
