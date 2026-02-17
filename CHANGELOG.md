# Changelog

## [Unreleased]

### Added
- Core alarm scheduling and firing infrastructure
  - `AlarmScheduler` - schedules exact alarms via `AlarmManager.setAlarmClock()`
  - `AlarmReceiver` - BroadcastReceiver that triggers alarm service
  - `AlarmService` - foreground service with MediaPlayer (ringtone) + Vibrator
  - `NotificationHelper` - notification channel + full-screen alarm notification
  - `AlarmActionReceiver` - dismiss/snooze actions from notification buttons
  - `ActiveAlarmActivity` - full-screen alarm UI over lock screen (dismiss + snooze)
  - `BootReceiver` - reschedules all enabled alarms after device reboot
  - `ic_alarm` vector drawable for notification icon
- Alarm scheduling wired into ViewModels:
  - `AddEditAlarmViewModel` schedules alarm on save
  - `AlarmListViewModel` schedules/cancels on toggle, cancels on delete

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
