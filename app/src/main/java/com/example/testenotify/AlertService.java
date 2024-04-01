package com.example.testenotify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class AlertService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "AlertChannel4";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        startAlarmSound(this);
        startVibration();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAlarmSound();
        stopVibration();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alert Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Intent notificationIntent = new Intent(this, AlertActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

//        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.activity_alert);
//        notificationLayout.setOnClickPendingIntent(R.id.liberar_button, pendingIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Alerta!")
                .setContentText("Toque para interagir")
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.escudo_ka)
//               .setContent(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingIntent, true)
                .setSound(null);


        return builder.build();
    }

    private void startAlarmSound(Context context) {
        Uri incomingRingtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.incoming);

        // Inicia o MediaPlayer com o som de notificação padrão
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(context, incomingRingtoneUri);

            // Define o tipo de áudio do MediaPlayer para STREAM_NOTIFICATION
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build());

            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void stopAlarmSound() {
        if (mediaPlayer != null) {
            // Para o som e libera os recursos do MediaPlayer
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 1000, 1000}; // Padrão de vibração (0ms de espera, 1s de vibração, 1s de espera, 1s de vibração, ...)
            vibrator.vibrate(pattern, 0); // Inicia a vibração
        }
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel(); // Para a vibração
        }
    }
}

