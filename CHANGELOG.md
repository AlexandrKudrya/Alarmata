# Changelog

## [Unreleased]

### Added
- Core alarm scheduling and firing infrastructure
  - `AlarmScheduler` - schedules exact alarms via `AlarmManager.setAlarmClock()`
  - `AlarmReceiver` - BroadcastReceiver that triggers alarm service
  - `AlarmService` - foreground service with MediaPlayer (ringtone) + Vibrator + gradual volume ramp
  - `NotificationHelper` - notification channel + full-screen alarm notification
  - `AlarmActionReceiver` - dismiss/snooze actions from notification buttons
  - `ActiveAlarmActivity` - full-screen alarm UI over lock screen (dismiss + snooze + back button blocked)
  - `BootReceiver` - reschedules all enabled alarms after device reboot
  - `ic_alarm` vector drawable for notification icon
- Alarm scheduling wired into ViewModels:
  - `AddEditAlarmViewModel` schedules alarm on save
  - `AlarmListViewModel` schedules/cancels on toggle, cancels on delete
- Russian language support (`values-ru/strings.xml`)
  - Language picker in Settings (System / English / Russian)
  - `LocaleHelper` for storing and applying locale preference
  - All hardcoded UI strings moved to string resources
  - `EnumStringResources` for localized enum display names (days, tasks, difficulty)

### Fixed
- Missing Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle-wrapper.jar`) for CI
- Typo in `settings.gradle.kts` (`dependencyResolution` -> `dependencyResolutionManagement`)
- `.gitignore` blocking `gradle-wrapper.jar`
- Missing mipmap launcher icons (placeholder PNGs for all density buckets)
- Material3 API incompatibilities with BOM 2023.10.01 (downgraded to 1.1.x APIs):
  - `Icons.AutoMirrored` -> `Icons.Default`
  - `HorizontalDivider` -> `Divider`
  - `SwipeToDismissBox` -> `SwipeToDismiss` + related types
  - Added `@OptIn(ExperimentalMaterial3Api::class)` for Card/FilterChip
- Room KSP "Empty schema file" error (`exportSchema = false`)
- `confirmStateChange` -> `confirmValueChange` for `rememberDismissState`
- Removed `?attr/colorControlNormal` from `ic_alarm.xml` (AppCompat attr unavailable in Compose)
- Added missing `FOREGROUND_SERVICE_MEDIA_PLAYBACK` permission for Android 14+
- Alarm UI not appearing when alarm fires:
  - `AlarmReceiver` now launches `ActiveAlarmActivity` directly (not only via full-screen intent)
  - Added runtime permission requests in `MainActivity`:
    - `POST_NOTIFICATIONS` (Android 13+)
    - `SCHEDULE_EXACT_ALARM` (Android 12+)
    - `USE_FULL_SCREEN_INTENT` (Android 14+)

### Changed
- `AlarmService`: gradual volume increase 0% → 100% over 10 seconds
- `ActiveAlarmActivity`: back button blocked during active alarm
- MVP-1.3 iteration marked as completed

### Docs
- Added iteration `MVP-1.6` — Quick Sleep Setup (quick alarm in 2 swipes + sleep cycle calculator)
- Added iteration `IT-2.5` — UI/UX Polish & Design System (between iterations 2 and 3)
- Updated iteration `IT-3.2` — added Smartwatch / Health Connect integration section
- Updated `docs/iterations/index.md` with new iteration entries
- Updated `docs/ROADMAP.md` with MVP 1.6 and IT-2.5 sections
