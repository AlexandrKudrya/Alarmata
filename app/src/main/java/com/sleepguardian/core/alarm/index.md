# core.alarm

Alarm scheduling and triggering infrastructure.

## Files
- `AlarmScheduler.kt` — Wrapper around AlarmManager for scheduling exact alarms.
- `AlarmReceiver.kt` — BroadcastReceiver triggered when alarm time arrives.
- `WakeLockService.kt` — Foreground Service managing wake lock, alarm sound, and vibration.
- `BootReceiver.kt` — BroadcastReceiver that restores scheduled alarms after device reboot.
