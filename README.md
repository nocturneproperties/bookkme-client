# BookkMe

**BookkMe** is a free, source-available booking app for **Android and iOS** that helps small businesses — salons, studios, clinics, tutors, repair shops, and anyone who works by appointment — run their day from their phone.

Keep your calendar, clients, services, and team in one place, and let your clients book with you directly.

---

## What you can do with BookkMe

### For business owners and staff

- **Appointments** — create, reschedule, complete, cancel, or mark appointments as no-shows; browse the calendar and the full appointment history.
- **Booking requests** — clients can request a time; approve or decline each request in one tap.
- **Clients** — keep a client list with contact details, or import clients straight from your phone's contacts.
- **Services** — describe what you offer with prices and durations, grouped into categories, in your business's currency.
- **Team** — invite employees with a code, choose which services each of them performs, and suspend or reinstate access at any time.
- **Dashboard** — see today's business at a glance and switch between businesses if you run more than one.
- **Plugins** — turn on optional features, such as online appointment booking, only when you need them.
- **Business data export** — download a full copy of your business's data whenever you want.

### For everyone

- **Password-free sign-in** with passkeys.
- **Notifications** about new bookings, changes and cancellations, with per-channel settings.
- **Privacy controls** — export your personal data, opt in to crash reporting, or delete your account from the app.
- **Light and dark themes.**
- **Available in English, Ukrainian, Russian and Polish.**

---

## Download

BookkMe will soon be available in the app stores:

- **App Store (iPhone)** — coming soon
- **Google Play (Android)** — coming soon

Until then, you can build the app yourself — see [Building the app](#building-the-app).

---

## Privacy and GDPR

BookkMe handles personal data of business owners, their employees and their clients, so privacy is built into the app rather than added on:

- **Right of access and data portability** — anyone can export a copy of their personal data, and business owners can export all of their business's data (profile, services, employees, clients, appointments and requests) as a JSON file, straight from the app.
- **Right to erasure** — users can delete their account from the app, and businesses can delete individual clients.
- **Storage limitation** — each business chooses how long client details on finished appointments and requests are kept (12–120 months). After that, they are anonymized or deleted.
- **Informed consent** — new users review and accept the Privacy Policy and Terms of Service when they sign up. When those documents change, the new versions are marked for review under **Settings → Legal**.
- **No tracking** — there is no analytics or advertising SDK, and advertising-ID permissions are removed from the Android app.
- **Optional crash reporting** — crash reports go to Firebase Crashlytics and contain only technical details about the error and the device, not names, emails, business or client data. They are off by default and can be turned on or off at any time under **Settings → Privacy**.
- **Contacts stay on the device** — importing clients from the phone's contacts asks for permission first and uploads only the contacts you select.

### If you self-host

When you run BookkMe for your own business, you are the **data controller** for the personal data it holds, including your clients' data. You are responsible for publishing your own Privacy Policy and Terms, choosing where your server and Firebase project are hosted, signing data processing agreements with your providers, and responding to requests from the people whose data you hold. The features above help you meet these obligations, but they don't do it for you.

---

## Security

- **No passwords** — sign-in uses passkeys, which can't be phished, reused or leaked in a password breach.
- **Extra confirmation for sensitive actions** — deleting an account, deleting a client, and exporting personal or business data each require a fresh passkey confirmation.
- **Verified email changes** — a new email address only takes effect after it is confirmed with a code sent to it.
- **Role-based access** — only the business owner can export the business's data, and suspended employees lose access to the business immediately.
- **Clean sign-out** — signing out clears the sign-in session and the business data cached on the device.

### Reporting a vulnerability

If you find a security issue, **please don't open a public issue.** Report it privately using GitHub's **Report a vulnerability** button on this repository's **Security** tab, and give us a reasonable time to fix it before you disclose it publicly.

---

## Ways to use BookkMe

### 1. Self-host it for your own business

The license explicitly allows you to run BookkMe for your own business, with your employees and your own clients using it — see [`LICENSE-SELF-HOSTING.md`](LICENSE-SELF-HOSTING.md). You can also have a hosting provider or IT contractor run it on your behalf.

The mobile app is a client: it needs a BookkMe-compatible backend to talk to. Once you have one running, point the app at it (see [Connect to your own server](#connect-to-your-own-server)) and build the app as described below.

### 2. Study it, modify it, learn from it

You are free to read the code, change it, and run it for internal use, education, and research. The project is a complete, real-world Kotlin Multiplatform app, so it is also a practical reference for building mobile apps that share logic between Android and iOS.

### What you can't do

You may not offer BookkMe, or a product built from it, to *other* businesses — as a hosted service, as software you sell, or as part of a booking platform or marketplace. See [License](#license) for details.

---

## Building the app

### Requirements

- **macOS, Linux or Windows** with **JDK 17+** and **Android Studio** (latest stable) — for Android.
- **macOS** with **Xcode** — additionally, for iOS.
- Your own **Firebase project** (used for push notifications and optional crash reporting).

### 1. Get the code

```bash
git clone git@github.com:nocturneproperties/bookkme-client.git
cd bookkme-client
```

### 2. Add your local configuration

These files are machine- or owner-specific and are not part of the repository. Create them before the first build:

| File | What it is |
|---|---|
| `local.properties` | Path to your Android SDK, e.g. `sdk.dir=/Users/you/Library/Android/sdk`. Android Studio creates it on first open. |
| `androidApp/keystore.properties` | Signing config with `debugKeyAlias`, `debugKeyPassword`, `debugStoreFile`, `debugStorePassword` and the same four `release*` keys. Point the store files at keystores you generate yourself. |
| `androidApp/google-services.json` | Firebase config for the Android app, downloaded from your Firebase project. |
| `iosApp/iosApp/Firebase/Dev/GoogleService-Info.plist`<br>`iosApp/iosApp/Firebase/Prod/GoogleService-Info.plist` | Firebase config for the iOS app, one per build flavor. |

### 3. Connect to your own server

The server address is set per build variant in [`shared/build.gradle.kts`](shared/build.gradle.kts) (`BASE_URL`). Replace the URLs there with the address of your backend, e.g. `https://booking.example.com/api`.

Sign-in uses passkeys, which are tied to a domain. To use them with your own server:

- update the associated domains (`applinks:` / `webcredentials:`) in the iOS `.entitlements` files to your domain, and
- publish the matching Digital Asset Links (Android) and Apple App Site Association (iOS) files on that domain, listing your app ID and signing certificate.

If you publish your own build, also change the application ID (`ROOT_PACKAGE` in `build-src/src/main/kotlin/build_src/constants/ApplicationConfig.kt`) and the iOS bundle identifier.

### 4. Build and run

**Android** — open the project in Android Studio and run the `androidApp` configuration, or build from the command line:

```bash
./gradlew :androidApp:assembleDevDebug     # development build
./gradlew :androidApp:assembleProdRelease  # release build
```

**iOS** — open `iosApp/iosApp.xcodeproj` in Xcode, pick your signing team, and run. The shared Kotlin code is compiled automatically as part of the Xcode build.

### Run the tests

```bash
./gradlew test
```

---

## Project structure

BookkMe is written in Kotlin Multiplatform: the business logic, data handling and screen state are written once and shared, while the UI is native on each platform — Jetpack Compose on Android and SwiftUI on iOS.

```
androidApp/    Android app
iosApp/        iOS app
shared/        Wiring that puts the shared modules together
feature/       One folder per feature: appointments, business, clients,
               employees, services, dashboard, settings, authorization
core/          Shared base building blocks
designsystem/  Shared UI components
database/      Local storage for offline access
library/       Small reusable modules (cache, permissions, files, …)
docs/          Database diagrams and per-operation flow diagrams
```

If you plan to modify the app, [`AGENTS.md`](AGENTS.md) describes the conventions the codebase follows — how screens, operations and tests are put together — and [`docs/`](docs) documents the local database and every operation the app performs.

---

## Contributing

Thank you for your interest in BookkMe! Unfortunately, **we do not accept contributions** — pull requests will not be reviewed or merged.

You are welcome to fork the project and adapt it for your own business within the terms of the license.

---

## License

BookkMe is **source-available** under the **Functional Source License, Version 1.1, ALv2 Future License** (`FSL-1.1-ALv2`) — see [`LICENSE`](LICENSE) — plus an [additional permission for self-hosting](LICENSE-SELF-HOSTING.md).

- **Allowed:** reading, modifying and running the code for any purpose other than a competing product — internal use, non-commercial education and research, and self-hosting BookkMe to take bookings for your own business, with your own clients booking through it.
- **Not allowed:** offering BookkMe, or a product built from it, to other businesses — as a hosted service, as software you sell, or as part of a booking platform.
- **Becomes Apache 2.0 after two years:** each version is also licensed under the Apache License, Version 2.0 from the second anniversary of its release.
- **Earlier versions stay Apache 2.0:** versions published before the switch to FSL — every commit whose `LICENSE` file is the Apache License — were released under the Apache License, Version 2.0 and remain available under it.
