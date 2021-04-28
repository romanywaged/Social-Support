package com.example.socialsupport.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class OreoNotification extends ContextWrapper {
    private static final String CHANNEL_ID="Romany.com";
    private static final String CHANNEL_NAME="Support";
    private NotificationManager notificationManager;
    public OreoNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CreateChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void CreateChannel() {
        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getmessage().createNotificationChannel(channel);

    }
    public NotificationManager getmessage()
    {
        if(notificationManager==null)
        {
            notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String tittle, String body,
                                                PendingIntent pendingIntent, Uri sound, String icon)
    {
        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(tittle)
                .setContentText(body)
                .setSound(sound)
                .setSmallIcon(Integer.parseInt(icon))
                .setAutoCancel(true);
    }
}
