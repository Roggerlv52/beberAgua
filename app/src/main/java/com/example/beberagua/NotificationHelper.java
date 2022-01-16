package com.example.beberagua;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String channel1id = "channel1id";
    public  static  final String getChannel1Name = "Channel 1";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }
    public  void createChannels() {
        NotificationChannel channel1 = new NotificationChannel(channel1id, getChannel1Name,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(getColor(R.color.Minha_cor_roger));
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel1);
    }
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            vibrar();
        }
        return mManager;
    }
    public NotificationCompat.Builder getChannel1Notification(String title, String message){
        return new NotificationCompat.Builder(getApplicationContext(),channel1id)
                .setContentTitle(title)
                .setContentText(message).setSmallIcon(R.drawable.ic_notification_important);
    }

    private void vibrar(){
        long[] s = {1000,1000,1000};
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        rr.vibrate(s,1);
    }
}
