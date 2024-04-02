package com.example.testenotify;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";


    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Get in");
        Bundle extras = new Bundle();
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

//            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
//            } else {
                // Handle message within 10 seconds
//                handleNow();
//            }



        }



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        // New code
        Map<String, String> dataPayLoad = remoteMessage.getData();
        for (Map.Entry<String, String> entry: dataPayLoad.entrySet()){
            extras.putString(entry.getKey(), entry.getValue());
        }

        final Intent startAppIntent = new Intent();
        Context ctx = getApplicationContext();
        String mPackage = ctx.getPackageName();
        String mClass = "IncomingCallActivity";

        startAppIntent.setComponent(new ComponentName(mPackage,mClass));
        Class<?> activityClass;
        try {
            activityClass = Class.forName(mPackage+'.'+mClass);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "activityClass is null");
            e.printStackTrace();
            return;
        }

//        if (isForeground(mPackage)){
//            sendBroadcast(extras);
//        } else {
            wakeApp();
            if (extras.getString("openApp", "").equals("true")) {
                startActivity(activityClass, extras);
            } else {
//                showNotification(remoteMessage, dataPayLoad, activityClass, extras);
                Intent serviceIntent = new Intent(this, AlertService.class);
                startService(serviceIntent);
            }
//        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

    }

    private void startActivity(Class<?> activityClass, Bundle extras){
        Log.d(TAG, "Starting activity...");
        Intent myIntent = new Intent(this,activityClass);

        myIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myIntent.setAction("android.intent.action.MAIN");
        myIntent.addCategory("android.intent.category.LAUNCHER");

        myIntent.putExtras(extras);
        this.getApplicationContext().startActivity(myIntent);
    }

    private void wakeApp(){
        PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean screenIsOn = pm.isInteractive(); // check if screen is on
        Log.d(TAG,"wakeApp" + screenIsOn);
        if (!screenIsOn) {
            final String wakeLockTag = getPackageName() + "WAKELOCK";

            PowerManager.WakeLock  wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, wakeLockTag);

            //acquire will turn on the display
            wakeLock.acquire();

            //release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings
            wakeLock.release();
        }
    }

    private void sendBroadcast(Bundle extras){
        Log.d(TAG, "Sending broadcast after received a message");
        Intent intent = new Intent("onNewEcosDoc");
        intent.putExtras(extras);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void createNotificationChannel(String channelID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Foreground Service Channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void showNotification(RemoteMessage remoteMessage, Map<String, String> dataPayLoad, Class<?> activityClass, Bundle extras) {

        final String channelID = "NOTIFICATION_CHANNEL_ID";
        createNotificationChannel(channelID);

        // Check if message contains a notification payload.
        String title = dataPayLoad.containsKey("title")? dataPayLoad.get("title"): "title";
        String body = dataPayLoad.containsKey("body")? dataPayLoad.get("body"): "body";


        Intent notificationIntent = new Intent(getApplicationContext(), activityClass);
        notificationIntent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
        notificationIntent.setAction(Intent. ACTION_MAIN ) ;
        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
        notificationIntent.putExtras(extras);
        PendingIntent resultIntent = PendingIntent.getActivity(getApplicationContext() , 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , channelID)
                .setSmallIcon(R.drawable.escudo_ka )
                .setContentTitle( title )
                .setContentText( body )
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultIntent) ;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

}
