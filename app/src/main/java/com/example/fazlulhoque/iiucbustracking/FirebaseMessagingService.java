package com.example.fazlulhoque.iiucbustracking;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Md Azad on 2/12/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder mBuilder=
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Notification")
                .setContentText("Hello World");

       int mNotificationId=(int) System.currentTimeMillis();
        NotificationManager mNotifymgr=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifymgr.notify(mNotificationId,mBuilder.build());
    }
}
