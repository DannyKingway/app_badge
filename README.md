# app_badge

Android Kotlin app icon badge implementation sample.

## Usage

```kotlin
val badgeManager = AppIconBadgeManager(
    context = this,
    smallIconResId = R.drawable.ic_notification,
    notificationTitle = getString(R.string.badge_title),
    notificationTextProvider = { count ->
        getString(R.string.badge_text, count)
    }
)

badgeManager.updateBadge(8) // show badge count 8
badgeManager.updateBadge(0) // clear badge
```

## File

- `app/src/main/java/com/dannykingway/app_badge/AppIconBadgeManager.kt`

## Notes

- Android 8.0+ launcher badges are controlled by notifications.
- `updateBadge(count)` creates/updates one low-priority notification with `setNumber(count)`.
- `count <= 0` clears the badge notification.
- Android 13+ requires `POST_NOTIFICATIONS` runtime permission before badge updates can be posted.
