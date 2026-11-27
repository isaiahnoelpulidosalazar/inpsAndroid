# EasyNotification

**Package:** `com.isaiahnoelpulidosalazar.inpsandroid.core.notification`

Custom notification dispatcher handling Android 8.0+ (`API 26+`) notification channels, Android 13+ (`POST_NOTIFICATIONS`) permission checks, fluent notification styles (`BigText`, `BigPicture`, `Inbox`), progress indicators, actions, and `FLAG_IMMUTABLE` tap `PendingIntent` helpers.

## Features

- **Channel Management:** `createChannel(...)`, `hasChannel(...)`, and `deleteChannel(...)` safely manage `NotificationChannel`s on API 26+ and no-op below API 26. Auto-creates `DEFAULT_CHANNEL_ID` (`inps_default_notifications`) if a custom channel is omitted.
- **Permission & Availability Checks:** `hasPostNotificationPermission()`, `areNotificationsEnabled()`, and `canPostNotifications()` guard dispatches so denied permissions return `EasyNotification.RESULT_BLOCKED` (`-1`) instead of throwing.
- **Fluent Dispatcher (`Request`):** Configure `title`, `message`, `subText`, `bigText`, `bigPicture`, `inboxLines`, `progress`, `tapActivity(Class<?>)`, `addAction(...)`, `silent`, `ongoing`, and a `BuilderCustomizer` hook before calling `show()` or `build()`.
- **Dispatch Lifecycle Hook:** Register an `EasyNotification.DispatchListener` to observe `onDispatched(id, tag, notification)` and `onBlocked(id, tag, reason)`.

## Usage

```java
EasyNotification notifications = new EasyNotification(context)
        .createChannel(
                "alerts",
                "Alerts",
                "High-priority app alerts",
                NotificationManagerCompat.IMPORTANCE_HIGH,
                true,
                true
        );

if (notifications.hasPostNotificationPermission()) {
    int id = notifications.prepare("alerts")
            .title("Sync Complete")
            .message("All notes are saved.")
            .bigText("All notes were saved cleanly to EasySQL.")
            .tapActivity(MainActivity.class)
            .show();
}
```

## API Reference

| Method | Description |
|---|---|
| `createChannel(id, name[, description, importance, vibration, sound])` | Registers or updates a `NotificationChannel` on API 26+. |
| `hasChannel(id)` / `deleteChannel(id)` | Checks whether a channel exists or deletes it on API 26+. |
| `hasPostNotificationPermission()` | Checks `Manifest.permission.POST_NOTIFICATIONS` on API 33+ (`true` below API 33). |
| `areNotificationsEnabled()` / `canPostNotifications()` | Verifies OS notification toggle and runtime permission together. |
| `prepare()` / `prepare(channelId)` | Starts a fluent `EasyNotification.Request`. |
| `cancel(id)` / `cancel(tag, id)` / `cancelAll()` | Cancels specific or all active notifications for this app. |
| `isNotificationActive(id)` | Returns `true` if a notification with `id` is currently active. |
| `setDispatchListener(listener)` | Receives `onDispatched` or `onBlocked` callbacks during `Request.show()`. |
| `setDefaultSmallIcon(iconRes)` | Sets a fallback drawable resource when `Request.smallIcon` is not set. |
