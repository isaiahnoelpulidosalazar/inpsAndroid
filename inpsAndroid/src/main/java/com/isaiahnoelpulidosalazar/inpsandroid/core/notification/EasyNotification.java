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
 * Android 13+ ({@code POST_NOTIFICATIONS}) permission checks, fluent notification styles ({@code
 * BigText}, {@code BigPicture}, {@code Inbox}), progress bars, action buttons, and safe {@link
 * PendingIntent} creation.
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

  /**
   * Returned by {@link Request#show()} when notifications are disabled or permission is missing.
   */
  public static final int RESULT_BLOCKED = -1;

  private static final AtomicInteger NEXT_ID = new AtomicInteger(1000);

  /**
   * Optional hook invoked right before dispatching to tweak the {@link NotificationCompat.Builder}.
   */
  public interface BuilderCustomizer {
    /**
     * Customizes the underlying {@link NotificationCompat.Builder} right before {@code build()}.
     *
     * @param builder configured notification builder
     */
    void customize(@NonNull NotificationCompat.Builder builder);
  }

  /** Optional listener notified when a dispatch succeeds or is blocked by missing permissions. */
  public interface DispatchListener {
    /**
     * Invoked after a notification is successfully posted to the system notification manager.
     *
     * @param id notification identifier used for dispatch
     * @param tag optional notification tag, or {@code null}
     * @param notification built {@link Notification} instance that was posted
     */
    void onDispatched(int id, @Nullable String tag, @NonNull Notification notification);

    /**
     * Invoked when dispatching is skipped or blocked before reaching the notification shade.
     *
     * @param id intended notification identifier
     * @param tag optional notification tag, or {@code null}
     * @param reason human-readable explanation of why posting was blocked
     */
    void onBlocked(int id, @Nullable String tag, @NonNull String reason);
  }

  private final Context appContext;
  private final NotificationManagerCompat managerCompat;
  @Nullable private final NotificationManager systemManager;
  @Nullable private DispatchListener dispatchListener;
  @DrawableRes private int defaultSmallIconRes;

  /**
   * Creates a new notification dispatcher bound to the application context.
   *
   * @param context any Android context; its application context is retained to avoid leaks
   */
  public EasyNotification(@NonNull Context context) {
    Objects.requireNonNull(context, "context");
    this.appContext = context.getApplicationContext();
    this.managerCompat = NotificationManagerCompat.from(appContext);
    this.systemManager =
        (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
    this.defaultSmallIconRes = resolveDefaultIcon(appContext);
  }

  /**
   * Sets the default small icon resource used when a {@link Request} does not override it.
   *
   * @param iconRes drawable resource ID for the status bar icon
   * @return this dispatcher for chaining
   * @throws IllegalArgumentException if {@code iconRes == 0}
   */
  public EasyNotification setDefaultSmallIcon(@DrawableRes int iconRes) {
    if (iconRes == 0) {
      throw new IllegalArgumentException("iconRes must be a valid drawable resource");
    }
    this.defaultSmallIconRes = iconRes;
    return this;
  }

  /**
   * Registers a {@link DispatchListener} to observe posted and blocked notifications.
   *
   * @param listener listener callback, or {@code null} to clear
   * @return this dispatcher for chaining
   */
  public EasyNotification setDispatchListener(@Nullable DispatchListener listener) {
    this.dispatchListener = listener;
    return this;
  }

  /**
   * Creates or updates a notification channel with default importance ({@link
   * NotificationManagerCompat#IMPORTANCE_DEFAULT}). Safe no-op below API 26.
   *
   * @param channelId stable channel identifier
   * @param name user-visible channel name shown in system settings
   * @return this dispatcher for chaining
   */
  public EasyNotification createChannel(@NonNull String channelId, @NonNull CharSequence name) {
    return createChannel(
        channelId, name, null, NotificationManagerCompat.IMPORTANCE_DEFAULT, true, true);
  }

  /**
   * Creates or updates a notification channel with the given importance. Safe no-op below API 26.
   *
   * @param channelId stable channel identifier
   * @param name user-visible channel name shown in system settings
   * @param importance channel importance constant from {@link NotificationManagerCompat}
   * @return this dispatcher for chaining
   */
  public EasyNotification createChannel(
      @NonNull String channelId, @NonNull CharSequence name, int importance) {
    return createChannel(channelId, name, null, importance, true, true);
  }

  /**
   * Creates or updates a notification channel with full configuration. Safe no-op below API 26.
   *
   * @param channelId stable channel identifier
   * @param name user-visible channel name shown in system settings
   * @param description optional channel description, or {@code null}
   * @param importance channel importance constant from {@link NotificationManagerCompat}
   * @param vibrationEnabled whether vibration is enabled on this channel
   * @param soundEnabled whether the default notification sound is enabled
   * @return this dispatcher for chaining
   */
  public EasyNotification createChannel(
      @NonNull String channelId,
      @NonNull CharSequence name,
      @Nullable String description,
      int importance,
      boolean vibrationEnabled,
      boolean soundEnabled) {
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

  /**
   * Checks whether the given notification channel exists on API 26+.
   *
   * @param channelId channel identifier to look up
   * @return {@code true} if the channel exists on API 26+, or {@code true} below API 26
   */
  public boolean hasChannel(@NonNull String channelId) {
    requireNonBlank(channelId, "channelId");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && systemManager != null) {
      return systemManager.getNotificationChannel(channelId) != null;
    }
    return true;
  }

  /**
   * Deletes a notification channel on API 26+ (no-op on older platforms).
   *
   * @param channelId channel identifier to delete
   */
  public void deleteChannel(@NonNull String channelId) {
    requireNonBlank(channelId, "channelId");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && systemManager != null) {
      systemManager.deleteNotificationChannel(channelId);
    }
  }

  /**
   * Checks runtime notification permission status.
   *
   * @return {@code true} if {@code Manifest.permission.POST_NOTIFICATIONS} is granted (always
   *     {@code true} below Android 13 / API 33)
   */
  public boolean hasPostNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      return true;
    }
    return ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
        == PackageManager.PERMISSION_GRANTED;
  }

  /**
   * Checks whether notifications are enabled for this app in system settings.
   *
   * @return {@code true} when notifications are enabled at the OS level
   */
  public boolean areNotificationsEnabled() {
    return managerCompat.areNotificationsEnabled();
  }

  /**
   * Convenience check combining system notification toggle and API 33+ runtime permission.
   *
   * @return {@code true} if both OS notifications are enabled and runtime permission is granted
   */
  public boolean canPostNotifications() {
    return areNotificationsEnabled() && hasPostNotificationPermission();
  }

  /**
   * Starts a fluent notification {@link Request} on {@link #DEFAULT_CHANNEL_ID}.
   *
   * @return new builder/dispatcher request
   */
  public Request prepare() {
    return new Request(this, DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME);
  }

  /**
   * Starts a fluent notification {@link Request} targeting the given {@code channelId}.
   *
   * @param channelId target channel identifier
   * @return new builder/dispatcher request
   */
  public Request prepare(@NonNull String channelId) {
    requireNonBlank(channelId, "channelId");
    String name = DEFAULT_CHANNEL_ID.equals(channelId) ? DEFAULT_CHANNEL_NAME : channelId;
    return new Request(this, channelId, name);
  }

  /**
   * Cancels a posted notification by {@code id}.
   *
   * @param id notification ID returned by {@link Request#show()}
   */
  public void cancel(int id) {
    managerCompat.cancel(id);
  }

  /**
   * Cancels a posted notification by {@code tag} and {@code id}.
   *
   * @param tag optional notification tag passed to {@link Request#tag(String)}, or {@code null}
   * @param id notification ID returned by {@link Request#show()}
   */
  public void cancel(@Nullable String tag, int id) {
    managerCompat.cancel(tag, id);
  }

  /** Cancels all active notifications posted by this application. */
  public void cancelAll() {
    managerCompat.cancelAll();
  }

  /**
   * Checks whether a notification with {@code id} is currently active in the system shade.
   *
   * @param id notification ID to inspect
   * @return {@code true} if currently active
   */
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
    @Nullable private String tag;

    @Nullable private CharSequence title;
    @Nullable private CharSequence message;
    @Nullable private CharSequence subText;
    @Nullable private CharSequence bigText;
    @Nullable private Bitmap bigPicture;
    @Nullable private Bitmap bigPictureLargeIcon;
    private boolean hasBigPicture;
    @Nullable private List<CharSequence> inboxLines;

    @DrawableRes private int smallIconRes;
    @Nullable private Bitmap largeIcon;
    @Nullable private Integer accentColor;
    private int priority = NotificationCompat.PRIORITY_DEFAULT;
    @Nullable private String category;
    @Nullable private String groupKey;
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

    @Nullable private PendingIntent contentIntent;
    @Nullable private BuilderCustomizer customizer;

    Request(EasyNotification dispatcher, String channelId, CharSequence channelName) {
      this.dispatcher = dispatcher;
      this.channelId = channelId;
      this.channelName = channelName;
      this.smallIconRes = dispatcher.defaultSmallIconRes;
    }

    /**
     * Sets an explicit non-negative notification ID (otherwise an ID is auto-assigned).
     *
     * @param id non-negative notification ID
     * @return this request for chaining
     * @throws IllegalArgumentException if {@code id < 0}
     */
    public Request id(int id) {
      if (id < 0) {
        throw new IllegalArgumentException("Notification id must be >= 0");
      }
      this.id = id;
      this.hasExplicitId = true;
      return this;
    }

    /**
     * Sets an optional notification tag used together with {@link #id(int)}.
     *
     * @param tag notification tag, or {@code null}
     * @return this request for chaining
     */
    public Request tag(@Nullable String tag) {
      this.tag = tag;
      return this;
    }

    /**
     * Sets the target channel ID and display name (auto-created on API 26+ if absent).
     *
     * @param channelId stable channel identifier
     * @param channelName user-visible channel name
     * @return this request for chaining
     */
    public Request channel(@NonNull String channelId, @NonNull CharSequence channelName) {
      requireNonBlank(channelId, "channelId");
      requireNonBlank(channelName, "channelName");
      this.channelId = channelId;
      this.channelName = channelName;
      return this;
    }

    /**
     * Sets channel importance used when auto-creating the channel on API 26+.
     *
     * @param importance channel importance constant from {@link NotificationManagerCompat}
     * @return this request for chaining
     */
    public Request importance(int importance) {
      this.importance = importance;
      return this;
    }

    /**
     * Sets the primary notification title text.
     *
     * @param title header title, or {@code null} to omit
     * @return this request for chaining
     */
    public Request title(@Nullable CharSequence title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the collapsed notification body text.
     *
     * @param message body text, or {@code null} to omit
     * @return this request for chaining
     */
    public Request message(@Nullable CharSequence message) {
      this.message = message;
      return this;
    }

    /**
     * Sets optional secondary header summary text shown beside the app name.
     *
     * @param subText subtext string, or {@code null}
     * @return this request for chaining
     */
    public Request subText(@Nullable CharSequence subText) {
      this.subText = subText;
      return this;
    }

    /**
     * Applies {@link NotificationCompat.BigTextStyle} for expandable multi-line text.
     *
     * @param bigText expanded body text, or {@code null}
     * @return this request for chaining
     */
    public Request bigText(@Nullable CharSequence bigText) {
      this.bigText = bigText;
      return this;
    }

    /**
     * Applies {@link NotificationCompat.BigPictureStyle}.
     *
     * @param picture banner image shown when expanded
     * @return this request for chaining
     */
    public Request bigPicture(@Nullable Bitmap picture) {
      return bigPicture(picture, null);
    }

    /**
     * Applies {@link NotificationCompat.BigPictureStyle} with an optional expanded large icon.
     *
     * @param picture banner image shown when expanded
     * @param largeIconWhenExpanded large icon shown while expanded, or {@code null}
     * @return this request for chaining
     */
    public Request bigPicture(@Nullable Bitmap picture, @Nullable Bitmap largeIconWhenExpanded) {
      this.bigPicture = picture;
      this.bigPictureLargeIcon = largeIconWhenExpanded;
      this.hasBigPicture = picture != null;
      return this;
    }

    /**
     * Applies {@link NotificationCompat.InboxStyle} with one line per entry.
     *
     * @param lines lines to render in expanded inbox view
     * @return this request for chaining
     */
    public Request inboxLines(@Nullable List<? extends CharSequence> lines) {
      if (lines == null || lines.isEmpty()) {
        this.inboxLines = null;
      } else {
        this.inboxLines = Collections.unmodifiableList(new ArrayList<>(lines));
      }
      return this;
    }

    /**
     * Sets the status-bar small icon drawable resource.
     *
     * @param iconRes valid drawable resource ID
     * @return this request for chaining
     * @throws IllegalArgumentException if {@code iconRes == 0}
     */
    public Request smallIcon(@DrawableRes int iconRes) {
      if (iconRes == 0) {
        throw new IllegalArgumentException("smallIcon must be a valid drawable resource");
      }
      this.smallIconRes = iconRes;
      return this;
    }

    /**
     * Sets the optional large icon bitmap displayed on the right/leading edge.
     *
     * @param largeIcon bitmap icon, or {@code null}
     * @return this request for chaining
     */
    public Request largeIcon(@Nullable Bitmap largeIcon) {
      this.largeIcon = largeIcon;
      return this;
    }

    /**
     * Sets the accent color used to tint the small icon and action buttons.
     *
     * @param color ARGB color integer
     * @return this request for chaining
     */
    public Request accentColor(@ColorInt int color) {
      this.accentColor = color;
      return this;
    }

    /**
     * Sets the legacy priority for Android 7.1 and lower ({@code NotificationCompat.PRIORITY_*}).
     *
     * @param priority notification priority constant
     * @return this request for chaining
     */
    public Request priority(int priority) {
      this.priority = priority;
      return this;
    }

    /**
     * Sets the system notification category (e.g. {@link NotificationCompat#CATEGORY_MESSAGE}).
     *
     * @param category notification category constant
     * @return this request for chaining
     */
    public Request category(@Nullable String category) {
      this.category = category;
      return this;
    }

    /**
     * Assigns this notification to a bundled notification group key.
     *
     * @param groupKey group identifier, or {@code null}
     * @return this request for chaining
     */
    public Request group(@Nullable String groupKey) {
      this.groupKey = groupKey;
      return this;
    }

    /**
     * Marks whether this notification acts as the summary header for {@link #group(String)}.
     *
     * @param isSummary {@code true} for the group summary notification
     * @return this request for chaining
     */
    public Request groupSummary(boolean isSummary) {
      this.groupSummary = isSummary;
      return this;
    }

    /**
     * Controls whether tapping the notification automatically dismisses it (default {@code true}).
     *
     * @param autoCancel {@code true} to dismiss on tap
     * @return this request for chaining
     */
    public Request autoCancel(boolean autoCancel) {
      this.autoCancel = autoCancel;
      return this;
    }

    /**
     * Marks the notification as ongoing (cannot be swiped away by the user).
     *
     * @param ongoing {@code true} for persistent ongoing notifications
     * @return this request for chaining
     */
    public Request ongoing(boolean ongoing) {
      this.ongoing = ongoing;
      return this;
    }

    /**
     * Suppresses sound and vibration alerts for this notification instance.
     *
     * @param silent {@code true} to post silently
     * @return this request for chaining
     */
    public Request silent(boolean silent) {
      this.silent = silent;
      return this;
    }

    /**
     * Ensures sound/vibration plays only the first time this notification ID is posted.
     *
     * @param onlyAlertOnce {@code true} to suppress repeated alerts on updates
     * @return this request for chaining
     */
    public Request onlyAlertOnce(boolean onlyAlertOnce) {
      this.onlyAlertOnce = onlyAlertOnce;
      return this;
    }

    /**
     * Controls whether the timestamp ({@link #when(long)}) is displayed in the header.
     *
     * @param showWhen {@code true} to display the timestamp
     * @return this request for chaining
     */
    public Request showWhen(boolean showWhen) {
      this.showWhen = showWhen;
      return this;
    }

    /**
     * Sets an explicit epoch timestamp in milliseconds for the notification header.
     *
     * @param timestampMs epoch time in milliseconds
     * @return this request for chaining
     */
    public Request when(long timestampMs) {
      this.whenTimestampMs = timestampMs;
      return this;
    }

    /**
     * Attaches a determinate or indeterminate progress bar to the notification.
     *
     * @param max maximum progress value
     * @param progress current progress value
     * @param indeterminate {@code true} for an indeterminate spinning bar
     * @return this request for chaining
     */
    public Request progress(int max, int progress, boolean indeterminate) {
      this.hasProgress = true;
      this.progressMax = max;
      this.progressValue = progress;
      this.progressIndeterminate = indeterminate;
      return this;
    }

    /**
     * Sets a custom {@link PendingIntent} launched when the user taps the notification body.
     *
     * @param intent tap intent, or {@code null}
     * @return this request for chaining
     */
    public Request contentIntent(@Nullable PendingIntent intent) {
      this.contentIntent = intent;
      return this;
    }

    /**
     * Configures the tap action to launch {@code activityClass} with {@link
     * PendingIntent#FLAG_IMMUTABLE} and {@link PendingIntent#FLAG_UPDATE_CURRENT}.
     *
     * @param activityClass target {@link android.app.Activity} class to launch on tap
     * @return this request for chaining
     */
    public Request tapActivity(@NonNull Class<?> activityClass) {
      Objects.requireNonNull(activityClass, "activityClass");
      Intent intent = new Intent(dispatcher.appContext, activityClass);
      intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
      int requestCode = activityClass.getName().hashCode();
      this.contentIntent =
          PendingIntent.getActivity(dispatcher.appContext, requestCode, intent, flags);
      return this;
    }

    /**
     * Appends an action button (Android displays up to 3 actions).
     *
     * @param iconRes drawable icon resource for the action
     * @param actionTitle button label shown below the notification
     * @param actionIntent {@link PendingIntent} invoked when the action is tapped
     * @return this request for chaining
     */
    public Request addAction(
        @DrawableRes int iconRes,
        @NonNull CharSequence actionTitle,
        @NonNull PendingIntent actionIntent) {
      Objects.requireNonNull(actionTitle, "actionTitle");
      Objects.requireNonNull(actionIntent, "actionIntent");
      actions.add(
          new NotificationCompat.Action.Builder(iconRes, actionTitle, actionIntent).build());
      return this;
    }

    /**
     * Sets a final {@link BuilderCustomizer} hook applied right before {@link #build()}.
     *
     * @param customizer customizer callback, or {@code null}
     * @return this request for chaining
     */
    public Request customizer(@Nullable BuilderCustomizer customizer) {
      this.customizer = customizer;
      return this;
    }

    /**
     * Builds the configured {@link Notification} without dispatching it.
     *
     * @return constructed {@link Notification} ready for posting or inspection
     */
    @NonNull
    public Notification build() {
      NotificationCompat.Builder builder =
          new NotificationCompat.Builder(dispatcher.appContext, channelId)
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
        NotificationCompat.BigPictureStyle style =
            new NotificationCompat.BigPictureStyle().bigPicture(bigPicture);
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
        NotificationCompat.BigTextStyle style =
            new NotificationCompat.BigTextStyle().bigText(bigText);
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
     * @return the posted notification ID ({@code >= 0}), or {@link #RESULT_BLOCKED} ({@code -1}) if
     *     OS notifications are disabled or {@code POST_NOTIFICATIONS} is not granted.
     */
    public int show() {
      return dispatcher.dispatchInternal(this);
    }
  }
}
