# app_badge

Android Kotlin app icon badge implementation sample.

## Usage

```kotlin
val badgeManager = AppIconBadgeManager(this)

badgeManager.updateBadge(8) // show badge count 8
badgeManager.updateBadge(0) // clear badge
```

## File

- `app/src/main/java/com/dannykingway/app_badge/AppIconBadgeManager.kt`

## Notes

- Android 8.0+ launcher badges are controlled by notifications.
- `updateBadge(count)` creates/updates one low-priority notification with `setNumber(count)`.
- `count <= 0` clears the badge notification.
