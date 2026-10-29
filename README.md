# PermissionsLab — Practice Work: Permissions in Android

This repository contains the completed hands-on lab work on Android Permissions covering Install-time, Runtime, and Special permissions, as well as Jetpack Compose integration and version-dependent behaviors.

---

## 🛠 Project Overview & Architecture

* **Package Name:** `uz.ttpu.permissionslab`
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material3)
* **Architecture:** Single Activity setup with modular Compose screens for each task.

---

## 📋 Task Summaries & Implementations

### Task 1: Kotlin Model (Denial States)
* **Location:** `Task1.kt`
* **Implementation:** Standard Kotlin class `PermissionModel` tracking `granted`, `rationale`, and `dialogShows` state flags without Android SDK dependencies.
* **Logic:**
    1. **First Denial:** `rationale = true`, `dialogShows = true`.
    2. **Second Denial:** `rationale = false`, `dialogShows = false` (Permanently Denied).
    3. **Subsequent Calls:** System dialog is blocked (`dialogShows = false`), returning immediate denial.

---

### Task 2: Normal Permissions & Network Observer
* **Location:** `Task2Screen.kt` & `AndroidManifest.xml`
* **Permissions Declared:** `INTERNET`, `ACCESS_NETWORK_STATE`
* **Implementation:** Uses `ConnectivityManager.NetworkCallback` inside a `DisposableEffect` to track network availability (`Online` / `Offline`).
* **Note:** Normal permissions require no runtime check or user dialog because they pose minimal security risk and are auto-granted at install time.

---

### Task 3: Camera Screen (Runtime Permissions)
* **Location:** `Task3Screen.kt`
* **Permission Declared:** `CAMERA`
* **Implementation:** Uses `rememberLauncherForActivityResult` with `ActivityResultContracts.RequestPermission()`.
* **Handled States:**
    1. **Granted:** Shows success status.
    2. **Denied Once (Rationale):** Shows rationale text explaining why the camera is required, accompanied by a grant button.
    3. **Denied Twice (Permanently Denied):** Hides standard request dialogs and provides an "Open Settings" button (`Settings.ACTION_APPLICATION_DETAILS_SETTINGS`).
* **API Check:** Bypasses dynamic prompts when `Build.VERSION.SDK_INT < 23`.

---

### Task 4: Special Permission (Overlay)
* **Location:** `Task4Screen.kt`
* **Permission Declared:** `SYSTEM_ALERT_WINDOW`
* **Implementation:** Controlled via a Compose `Switch` that launches `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`.
* **Lifecycle Refresh:** Utilizes `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` to re-check `Settings.canDrawOverlays(context)` when returning from System Settings, ensuring immediate UI sync.

---

### Task 5: One-Time Location & Lifecycle Re-check
* **Location:** `Task5Screen.kt`
* **Permissions Declared:** `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`
* **Implementation:** Uses `RequestMultiplePermissions` contract returning a `Map<String, Boolean>`. Evaluates states into `PRECISE`, `APPROXIMATE`, or `NONE`.
* **Observation:**
  > **One-time Permission Observation (Android 11+ / API 30+):**
  > When the user selects **"Only this time"**, the OS revokes the granted location permission as soon as the app goes to the background. By triggering a state re-evaluation inside `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` instead of reading state once in `onCreate`, the app immediately detects permission revocation upon resuming and updates the UI state back to `NONE`.

---

### Task 6: Notification Permission (Version-Dependent)
* **Location:** `Task6Screen.kt`
* **Permission Declared:** `POST_NOTIFICATIONS`
* **Observation & Multi-API Testing:**
    * **Tested API Levels:** API 30 (Android 11) & API 34 (Android 14).
    * **API 33+ (Android 13+):** Requires dynamic runtime permission `POST_NOTIFICATIONS`. Clicking the action triggers the system prompt; once approved, a test notification is posted via `NotificationManager`.
    * **API < 33:** Notifications are granted by default at install time. The `Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU` guard skips the request dialog and posts the notification directly.

---

## 📝 Part A Answers (Theory Summary)

All theoretical questions from Part A have been analyzed and answered according to the lecture slides:
1. **Permission Workflow:** 4 decisions (Need permission? $\rightarrow$ Select permission $\rightarrow$ Declare in Manifest $\rightarrow$ Runtime handling).
2. **Permission Categories:** Install-time (auto-granted), Runtime (user prompt required), Special (system settings redirect required).
3. **Normal Permissions:** Auto-granted, no dialogs needed. Omitting from manifest throws `SecurityException`.
4. **Signature Protection Level:** Granted only to apps signed with the same certificate without showing a dialog.
5. **One-Time Permissions (API 30+):** Revoked when app process stops/leaves foreground; state must be re-checked on `ON_RESUME`.
6. **Notification Permission Changes:** Runtime permission starting from API 33 (`POST_NOTIFICATIONS`).

---

## 📂 Submissions Checklist

- [x] Separate Git commit for every task.
- [x] Screenshots captured for Camera states, Overlay settings, One-time location, and Notifications.
- [x] Answers to Part A theory questions completed.
- [x] Tested on multiple API levels (API 30 & API 34).