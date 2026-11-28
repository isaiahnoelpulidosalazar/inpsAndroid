package com.isaiahnoelpulidosalazar.inpsandroid.core.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.test.core.app.ApplicationProvider;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNotificationManager;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class EasyNotificationTest {

  private Application app;
  private NotificationManager systemManager;
  private ShadowNotificationManager shadowManager;
  private EasyNotification dispatcher;

  @Before
  public void setUp() {
    app = ApplicationProvider.getApplicationContext();
    Shadows.shadowOf(app).grantPermissions(Manifest.permission.POST_NOTIFICATIONS);
    systemManager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
    shadowManager = Shadows.shadowOf(systemManager);
    dispatcher = new EasyNotification(app);
  }

  @Test
  public void testChannelLifecycleAndValidation() {
    assertFalse(dispatcher.hasChannel("custom_alerts"));
    dispatcher.createChannel(
        "custom_alerts",
        "Custom Alerts",
        "Important updates",
        NotificationManagerCompat.IMPORTANCE_HIGH,
        true,
        false);
    assertTrue(dispatcher.hasChannel("custom_alerts"));

    dispatcher.deleteChannel("custom_alerts");
    assertFalse(dispatcher.hasChannel("custom_alerts"));

    assertThrows(IllegalArgumentException.class, () -> dispatcher.createChannel("  ", "Name"));
    assertThrows(IllegalArgumentException.class, () -> dispatcher.createChannel("id", ""));
    assertThrows(IllegalArgumentException.class, () -> dispatcher.prepare().id(-1));
    assertThrows(IllegalArgumentException.class, () -> dispatcher.setDefaultSmallIcon(0));
  }

  @Test
  public void testDispatchAutoCreatesDefaultChannelAndBuildsNotification() {
    AtomicInteger dispatchedId = new AtomicInteger(-1);
    AtomicReference<Notification> dispatchedNotification = new AtomicReference<>();

    dispatcher.setDispatchListener(
        new EasyNotification.DispatchListener() {
          @Override
          public void onDispatched(
              int id, @Nullable String tag, @NonNull Notification notification) {
            dispatchedId.set(id);
            dispatchedNotification.set(notification);
          }

          @Override
          public void onBlocked(int id, @Nullable String tag, @NonNull String reason) {
            throw new AssertionError("Should not be blocked: " + reason);
          }
        });

    PendingIntent actionIntent =
        PendingIntent.getActivity(
            app,
            7,
            new Intent("android.intent.action.VIEW"),
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

    int id =
        dispatcher
            .prepare()
            .title("Upload Ready")
            .message("Report exported")
            .bigText("Report exported successfully to downloads.")
            .subText("inpsAndroid")
            .accentColor(Color.BLUE)
            .silent(true)
            .progress(100, 50, false)
            .addAction(android.R.drawable.ic_menu_share, "Share", actionIntent)
            .customizer(b -> b.setNumber(3))
            .show();

    assertTrue(id >= 1000);
    assertEquals(id, dispatchedId.get());
    assertTrue(dispatcher.hasChannel(EasyNotification.DEFAULT_CHANNEL_ID));

    Notification posted = shadowManager.getNotification(id);
    assertNotNull(posted);
    assertNotNull(dispatchedNotification.get());
    assertEquals("Upload Ready", posted.extras.getCharSequence(NotificationCompat.EXTRA_TITLE));
    assertEquals("Report exported", posted.extras.getCharSequence(NotificationCompat.EXTRA_TEXT));
    assertEquals("inpsAndroid", posted.extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT));
    assertEquals(3, posted.number);
    assertEquals(1, posted.actions.length);
    assertTrue(dispatcher.isNotificationActive(id));

    dispatcher.cancel(id);
    assertFalse(dispatcher.isNotificationActive(id));
  }

  @Test
  public void testPermissionDeniedBlocksDispatchSafely() {
    Shadows.shadowOf(app).denyPermissions(Manifest.permission.POST_NOTIFICATIONS);
    assertFalse(dispatcher.hasPostNotificationPermission());
    assertFalse(dispatcher.canPostNotifications());

    AtomicBoolean blockedCalled = new AtomicBoolean(false);
    AtomicReference<String> blockedReason = new AtomicReference<>();
    dispatcher.setDispatchListener(
        new EasyNotification.DispatchListener() {
          @Override
          public void onDispatched(
              int id, @Nullable String tag, @NonNull Notification notification) {
            throw new AssertionError("Should not dispatch when permission denied");
          }

          @Override
          public void onBlocked(int id, @Nullable String tag, @NonNull String reason) {
            blockedCalled.set(true);
            blockedReason.set(reason);
          }
        });

    int result =
        dispatcher.prepare("alerts").id(42).title("Blocked").message("Requires permission").show();

    assertEquals(EasyNotification.RESULT_BLOCKED, result);
    assertTrue(blockedCalled.get());
    assertTrue(blockedReason.get().contains("POST_NOTIFICATIONS"));
  }

  @Test
  public void testInboxAndBigPictureStylesAndTaggedCancel() {
    int inboxId =
        dispatcher
            .prepare("digest")
            .id(201)
            .tag("digest_tag")
            .title("3 New Notes")
            .message("Summary")
            .inboxLines(Arrays.asList("First note", "Second note", "Third note"))
            .show();
    assertEquals(201, inboxId);
    assertNotNull(shadowManager.getNotification("digest_tag", 201));

    Bitmap pic = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);
    try {
      int picId =
          dispatcher
              .prepare("media")
              .id(202)
              .title("Snapshot")
              .message("Captured")
              .bigPicture(pic)
              .show();
      assertEquals(202, picId);
      assertNotNull(shadowManager.getNotification(202));
    } finally {
      pic.recycle();
    }

    dispatcher.cancel("digest_tag", 201);
    assertEquals(1, shadowManager.size());
    dispatcher.cancelAll();
    assertEquals(0, shadowManager.size());
  }
}
