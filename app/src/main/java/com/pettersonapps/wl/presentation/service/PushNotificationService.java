package com.pettersonapps.wl.presentation.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.DataManagerImpl;
import com.pettersonapps.wl.presentation.ui.splash.SplashActivity;

import java.util.Random;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String TAG = "PushService";
    private static final String CHANEL_ID = "default_id";
    private static final String CHANEL_NAME = "Default";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";

    @Override
    public void onNewToken(String token) {
        DataManagerImpl.newInstance().updateToken();
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getTitle() != null && remoteMessage.getNotification().getBody() != null) {
            Log.d(TAG, remoteMessage.getNotification().getBody());
            showNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getData() != null && remoteMessage.getData().containsKey(KEY_TITLE) && remoteMessage.getData().containsKey(KEY_BODY)) {
            Log.d(TAG, remoteMessage.getData().toString());
            showNotification(this, remoteMessage.getData().get(KEY_TITLE), remoteMessage.getData().get(KEY_BODY));
        }
    }

    private void showNotification(Context context, String title, String body) {
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;
        final Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(CHANEL_ID,
                    CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            notificationBuilder = new Notification.Builder(context, CHANEL_ID);
            notificationBuilder.setChannelId(CHANEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(context);
        }
        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_stat_main)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body)
                .setTicker(body)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
        final PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                SplashActivity.getLaunchIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
        if (pIntent != null) {
            notificationBuilder.setContentIntent(pIntent);
        }
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }
}
