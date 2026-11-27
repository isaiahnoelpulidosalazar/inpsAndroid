package com.isaiahnoelpulidosalazar.inpsandroid.core.notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom notification dispatcher handling Android 8.0+ ({@code API 26+}) notification channels,
 * Android 13+ ({@code POST_NOTIFICATIONS}) permission checks, fluent notification styles
 * ({@code BigText}, {@code BigPicture}, {@code Inbox}), progress bars, action buttons, and
 * safe {@link PendingIntent} creation.
 *
 * <pre>{@code
 * EasyNotification notifications = new EasyNotification(context);
 * notifications.createChannel("alerts", "Alerts", NotificationManagerCompat.IMPORTANCE_HIGH);
 *
 * int id = notifications.prepare("alerts")
 *         .title("Sync complete")
 *         .message("3 notes synced with EasySQL.")
 *         .tapActivity(MainActivity.class)
 *         .show();
 * }</pre>
 */
public final class EasyNotification {

    /** Default channel ID created automatically when dispatching without an explicit channel. */
    public static final String DEFAULT_CHANNEL_ID = "inps_default_notifications";
    /** Default channel name shown in system notification settings. */
    public static final String DEFAULT_CHANNEL_NAME = "General";
    /** Returned by {@link Request#show()} when notifications are disabled or permission is missing. */
    public static final int RESULT_BLOCKED = -1;

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1000);

    /** Optional hook invoked right before dispatching to tweak the {@link NotificationCompat.Builder}. */
    public interface BuilderCustomizer {
        void customize(@NonNull NotificationCompat.Builder builder);
    }

    /** Optional listener notified when a dispatch succeeds or is blocked by missing permissions. */
    public interface DispatchListener {
        void onDispatched(int id, @Nullable String tag, @NonNull Notification notification);

        void onBlocked(int id, @Nullable String tag, @NonNull String reason);
    }

    private final Context appContext;
    private final NotificationManagerCompat managerCompat;
    @Nullable
    private final NotificationManager systemManager;
    @Nullable
    private DispatchListener dispatchListener;
    @DrawableRes
    private int defaultSmallIconRes;

    public EasyNotification(@NonNull Context context) {
        Objects.requireNonNull(context, "context");
        this.appContext = context.getApplicationContext();
        this.managerCompat = NotificationManagerCompat.from(appContext);
        this.systemManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        this.defaultSmallIconRes = resolveDefaultIcon(appContext);
    }

    /** Sets the default small icon resource used when a {@link Request} does not override it. */
    public EasyNotification setDefaultSmallIcon(@DrawableRes int iconRes) {
        if (iconRes == 0) {
            throw new IllegalArgumentException("iconRes must be a valid drawable resource");
        }
        this.defaultSmallIconRes = iconRes;
        return this;
    }

    /** Registers a {@link DispatchListener} to observe posted and blocked notifications. */
    public EasyNotification setDispatchListener(@Nullable DispatchListener listener) {
        this.dispatchListener = listener;
        return this;
    }

    /**
     * Creates or updates a notification channel with default importance
     * ({@link NotificationManagerCompat#IMPORTANCE_DEFAULT}). Safe no-op below API 26.
     */
    public EasyNotification createChannel(@NonNull String channelId, @NonNull CharSequence name) {
        return createChannel(channelId, name, null, NotificationManagerCompat.IMPORTANCE_DEFAULT, true, true);
    }

    /** Creates or updates a notification channel with the given importance. Safe no-op below API 26. */
    public EasyNotification createChannel(@NonNull String channelId, @NonNull CharSequence name, int importance) {
        return createChannel(channelId, name, null, importance, true, true);
    }

    /** Creates or updates a notification channel with full configuration. Safe no-op below API 26. */
    public EasyNotification createChannel(
            @NonNull String channelId,
            @NonNull CharSequence name,
            @Nullable String description,
            int importance,
            boolean vibrationEnabled,
            boolean soundEnabled
    ) {
        requireNonBlank(channelId, "channelId");
        requireNonBlank(name, "name");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && systemManager != null) {
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            if (description != null) {
                channel.setDescription(description);
            }
            channel.enableVibration(vibrationEnabled);
            if (!soundEnabled) {
                channel.setSound(null, null);
            }
            systemManager.createNotificationChannel(channel);
        }
        return this;
    }

    /** Returns {@code true} if the channel exists on API 26+, or {@code true} below API 26. */
    public boolean hasChannel(@NonNull String channelId) {
        requireNonBlank(channelId, "channelId");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && systemManager != null) {
            return systemManager.getNotificationChannel(channelId) != null;
        }
        return true;
    }

    /** Deletes a notification channel on API 26+. */
    public void deleteChannel(@NonNull String channelId) {
        requireNonBlank(channelId, "channelId");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && systemManager != null) {
            systemManager.deleteNotificationChannel(channelId);
        }
    }

    /**
     * Returns {@code true} if {@code Manifest.permission.POST_NOTIFICATIONS} is granted (always
     * {@code true} below Android 13 / API 33).
     */
    public boolean hasPostNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    /** Returns {@code true} when notifications are enabled at the OS level for this app. */
    public boolean areNotificationsEnabled() {
        return managerCompat.areNotificationsEnabled();
    }

    /** Returns {@code true} if both OS notifications are enabled and runtime permission is granted. */
    public boolean canPostNotifications() {
        return areNotificationsEnabled() && hasPostNotificationPermission();
    }

    /** Starts a fluent notification {@link Request} on {@link #DEFAULT_CHANNEL_ID}. */
    public Request prepare() {
        return new Request(this, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME);
    }

    /** Starts a fluent notification {@link Request} on the given {@code channelId}. */
    public Request prepare(@NonNull String channelId) {
        requireNonBlank(channelId, "channelId");
        String name = DEFAULT_CHANNEL_ID.equals(channelId) ? DEFAULT_CHANNEL_NAME : channelId;
        return new Request(this, channelId, name);
    }

    /** Cancels a posted notification by {@code id}. */
    public void cancel(int id) {
        managerCompat.cancel(id);
    }

    /** Cancels a posted notification by {@code tag} and {@code id}. */
    public void cancel(@Nullable String tag, int id) {
        managerCompat.cancel(tag, id);
    }

    /** Cancels all active notifications posted by this application. */
    public void cancelAll() {
        managerCompat.cancelAll();
    }

    /** Returns {@code true} if a notification with {@code id} is currently active in the shade. */
    public boolean isNotificationActive(int id) {
        if (systemManager == null) {
            return false;
        }
        StatusBarNotification[] active = systemManager.getActiveNotifications();
        if (active == null) {
            return false;
        }
        for (StatusBarNotification sbn : active) {
            if (sbn != null && sbn.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    int dispatchInternal(@NonNull Request request) {
        int id = request.hasExplicitId ? request.id : NEXT_ID.getAndIncrement();
        if (!hasPostNotificationPermission()) {
            notifyBlocked(id, request.tag, "POST_NOTIFICATIONS permission not granted");
            return RESULT_BLOCKED;
        }
        if (!areNotificationsEnabled()) {
            notifyBlocked(id, request.tag, "Notifications are disabled for this app");
            return RESULT_BLOCKED;
        }

        ensureChannel(request.channelId, request.channelName, request.importance);
        Notification notification = request.build();
        try {
            if (request.tag != null) {
                managerCompat.notify(request.tag, id, notification);
            } else {
                managerCompat.notify(id, notification);
            }
            if (dispatchListener != null) {
                dispatchListener.onDispatched(id, request.tag, notification);
            }
            return id;
        } catch (SecurityException e) {
            notifyBlocked(id, request.tag, e.getMessage() != null ? e.getMessage() : "SecurityException");
            return RESULT_BLOCKED;
        }
    }

    void ensureChannel(@NonNull String channelId, @NonNull CharSequence channelName, int importance) {
        if (!hasChannel(channelId)) {
            createChannel(channelId, channelName, importance);
        }
    }

    private void notifyBlocked(int id, @Nullable String tag, @NonNull String reason) {
        if (dispatchListener != null) {
            dispatchListener.onBlocked(id, tag, reason);
        }
    }

    private static int resolveDefaultIcon(Context context) {
        int appIcon = context.getApplicationInfo().icon;
        return appIcon != 0 ? appIcon : android.R.drawable.ic_dialog_info;
    }

    private static void requireNonBlank(@Nullable CharSequence value, String name) {
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
    }

    /** Fluent notification builder/dispatcher created via {@link EasyNotification#prepare()}. */
    public static final class Request {
        private final EasyNotification dispatcher;
        private final List<NotificationCompat.Action> actions = new ArrayList<>(3);

        private String channelId;
        private CharSequence channelName;
        private int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
        private int id;
        private boolean hasExplicitId;
        @Nullable
        private String tag;

        @Nullable
        private CharSequence title;
        @Nullable
        private CharSequence message;
        @Nullable
        private CharSequence subText;
        @Nullable
        private CharSequence bigText;
        @Nullable
        private Bitmap bigPicture;
        @Nullable
        private Bitmap bigPictureLargeIcon;
        private boolean hasBigPicture;
        @Nullable
        private List<CharSequence> inboxLines;

        @DrawableRes
        private int smallIconRes;
        @Nullable
        private Bitmap largeIcon;
        @Nullable
        private Integer accentColor;
        private int priority = NotificationCompat.PRIORITY_DEFAULT;
        @Nullable
        private String category;
        @Nullable
        private String groupKey;
        private boolean groupSummary;
        private boolean autoCancel = true;
        private boolean ongoing;
        private boolean silent;
        private boolean onlyAlertOnce;
        private boolean showWhen = true;
        private long whenTimestampMs;

        private boolean hasProgress;
        private int progressMax;
        private int progressValue;
        private boolean progressIndeterminate;

        @Nullable
        private PendingIntent contentIntent;
        @Nullable
        private BuilderCustomizer customizer;

        Request(EasyNotification dispatcher, String channelId, CharSequence channelName) {
            this.dispatcher = dispatcher;
            this.channelId = channelId;
            this.channelName = channelName;
            this.smallIconRes = dispatcher.defaultSmallIconRes;
        }

        /** Sets an explicit non-negative notification ID (otherwise an ID is auto-assigned). */
        public Request id(int id) {
            if (id < 0) {
                throw new IllegalArgumentException("Notification id must be >= 0");
            }
            this.id = id;
            this.hasExplicitId = true;
            return this;
        }

        /** Sets an optional notification tag used together with {@link #id(int)}. */
        public Request tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        /** Sets the target channel ID and display name (auto-created on API 26+ if absent). */
        public Request channel(@NonNull String channelId, @NonNull CharSequence channelName) {
            requireNonBlank(channelId, "channelId");
            requireNonBlank(channelName, "channelName");
            this.channelId = channelId;
            this.channelName = channelName;
            return this;
        }

        /** Sets channel importance used when auto-creating the channel on API 26+. */
        public Request importance(int importance) {
            this.importance = importance;
            return this;
        }

        public Request title(@Nullable CharSequence title) {
            this.title = title;
            return this;
        }

        public Request message(@Nullable CharSequence message) {
            this.message = message;
            return this;
        }

        public Request subText(@Nullable CharSequence subText) {
            this.subText = subText;
            return this;
        }

        /** Applies {@link NotificationCompat.BigTextStyle} for expandable multi-line text. */
        public Request bigText(@Nullable CharSequence bigText) {
            this.bigText = bigText;
            return this;
        }

        /** Applies {@link NotificationCompat.BigPictureStyle}. */
        public Request bigPicture(@Nullable Bitmap picture) {
            return bigPicture(picture, null);
        }

        /** Applies {@link NotificationCompat.BigPictureStyle} with an optional expanded large icon. */
        public Request bigPicture(@Nullable Bitmap picture, @Nullable Bitmap largeIconWhenExpanded) {
            this.bigPicture = picture;
            this.bigPictureLargeIcon = largeIconWhenExpanded;
            this.hasBigPicture = picture != null;
            return this;
        }

        /** Applies {@link NotificationCompat.InboxStyle} with one line per entry. */
        public Request inboxLines(@Nullable List<? extends CharSequence> lines) {
            if (lines == null || lines.isEmpty()) {
                this.inboxLines = null;
            } else {
                this.inboxLines = Collections.unmodifiableList(new ArrayList<>(lines));
            }
            return this;
        }

        public Request smallIcon(@DrawableRes int iconRes) {
            if (iconRes == 0) {
                throw new IllegalArgumentException("smallIcon must be a valid drawable resource");
            }
            this.smallIconRes = iconRes;
            return this;
        }

        public Request largeIcon(@Nullable Bitmap largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        public Request accentColor(@ColorInt int color) {
            this.accentColor = color;
            return this;
        }

        public Request priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Request category(@Nullable String category) {
            this.category = category;
            return this;
        }

        public Request group(@Nullable String groupKey) {
            this.groupKey = groupKey;
            return this;
        }

        public Request groupSummary(boolean isSummary) {
            this.groupSummary = isSummary;
            return this;
        }

        public Request autoCancel(boolean autoCancel) {
            this.autoCancel = autoCancel;
            return this;
        }

        public Request ongoing(boolean ongoing) {
            this.ongoing = ongoing;
            return this;
        }

        public Request silent(boolean silent) {
            this.silent = silent;
            return this;
        }

        public Request onlyAlertOnce(boolean onlyAlertOnce) {
            this.onlyAlertOnce = onlyAlertOnce;
            return this;
        }

        public Request showWhen(boolean showWhen) {
            this.showWhen = showWhen;
            return this;
        }

        public Request when(long timestampMs) {
            this.whenTimestampMs = timestampMs;
            return this;
        }

        public Request progress(int max, int progress, boolean indeterminate) {
            this.hasProgress = true;
            this.progressMax = max;
            this.progressValue = progress;
            this.progressIndeterminate = indeterminate;
            return this;
        }

        public Request contentIntent(@Nullable PendingIntent intent) {
            this.contentIntent = intent;
            return this;
        }

        /**
         * Configures the tap action to launch {@code activityClass} with
         * {@link PendingIntent#FLAG_IMMUTABLE} and {@link PendingIntent#FLAG_UPDATE_CURRENT}.
         */
        public Request tapActivity(@NonNull Class<?> activityClass) {
            Objects.requireNonNull(activityClass, "activityClass");
            Intent intent = new Intent(dispatcher.appContext, activityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            int requestCode = activityClass.getName().hashCode();
            this.contentIntent = PendingIntent.getActivity(dispatcher.appContext, requestCode, intent, flags);
            return this;
        }

        /** Appends an action button (Android displays up to 3 actions). */
        public Request addAction(
                @DrawableRes int iconRes,
                @NonNull CharSequence actionTitle,
                @NonNull PendingIntent actionIntent
        ) {
            Objects.requireNonNull(actionTitle, "actionTitle");
            Objects.requireNonNull(actionIntent, "actionIntent");
            actions.add(new NotificationCompat.Action.Builder(iconRes, actionTitle, actionIntent).build());
            return this;
        }

        /** Sets a final {@link BuilderCustomizer} hook applied right before {@link #build()}. */
        public Request customizer(@Nullable BuilderCustomizer customizer) {
            this.customizer = customizer;
            return this;
        }

        /** Builds the configured {@link Notification} without dispatching it. */
        @NonNull
        public Notification build() {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(dispatcher.appContext, channelId)
                    .setSmallIcon(smallIconRes)
                    .setPriority(priority)
                    .setAutoCancel(autoCancel)
                    .setOngoing(ongoing)
                    .setSilent(silent)
                    .setOnlyAlertOnce(onlyAlertOnce)
                    .setShowWhen(showWhen);

            if (title != null) builder.setContentTitle(title);
            if (message != null) builder.setContentText(message);
            if (subText != null) builder.setSubText(subText);
            if (largeIcon != null) builder.setLargeIcon(largeIcon);
            if (accentColor != null) builder.setColor(accentColor);
            if (category != null) builder.setCategory(category);
            if (groupKey != null) {
                builder.setGroup(groupKey);
                builder.setGroupSummary(groupSummary);
            }
            if (whenTimestampMs > 0L) builder.setWhen(whenTimestampMs);
            if (contentIntent != null) builder.setContentIntent(contentIntent);
            if (hasProgress) builder.setProgress(progressMax, progressValue, progressIndeterminate);

            if (hasBigPicture && bigPicture != null) {
                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(bigPicture);
                if (bigPictureLargeIcon != null) {
                    style.bigLargeIcon(bigPictureLargeIcon);
                }
                if (title != null) {
                    style.setBigContentTitle(title);
                }
                if (message != null) {
                    style.setSummaryText(message);
                }
                builder.setStyle(style);
            } else if (inboxLines != null && !inboxLines.isEmpty()) {
                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                for (CharSequence line : inboxLines) {
                    style.addLine(line);
                }
                if (title != null) {
                    style.setBigContentTitle(title);
                }
                if (message != null) {
                    style.setSummaryText(message);
                }
                builder.setStyle(style);
            } else if (bigText != null) {
                NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                        .bigText(bigText);
                if (title != null) {
                    style.setBigContentTitle(title);
                }
                builder.setStyle(style);
            }

            for (NotificationCompat.Action action : actions) {
                builder.addAction(action);
            }

            if (customizer != null) {
                customizer.customize(builder);
            }
            return builder.build();
        }

        /**
         * Ensures the target channel exists, checks notification permissions, and posts the
         * notification.
         *
         * @return the posted notification ID ({@code >= 0}), or {@link #RESULT_BLOCKED} ({@code -1})
         * if OS notifications are disabled or {@code POST_NOTIFICATIONS} is not granted.
         */
        public int show() {
            return dispatcher.dispatchInternal(this);
        }
    }
}
