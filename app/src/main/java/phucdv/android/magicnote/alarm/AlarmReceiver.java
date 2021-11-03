package phucdv.android.magicnote.alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import phucdv.android.magicnote.MagicNoteActivity;
import phucdv.android.magicnote.R;

public class AlarmReceiver extends BroadcastReceiver {
    public static String ACTION_SET_UP_ALARM = "phucdv.android.action.SET_UP_ALARM";
    public static String ACTION_FIRE_ALARM = "phucdv.android.action.FIRE_ALARM";
    public static String EXTRA_TIME_REMINDER = "phucdv.android.extra.time_reminder";

    private NotificationManager mNotificationManager;
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "magic_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        createNotificationChannel(context);
        if(ACTION_SET_UP_ALARM.equals(action)){
            long timestamp = intent.getLongExtra(EXTRA_TIME_REMINDER, -1);
            if(timestamp > 0) {
                setUpAlarm(context, timestamp);
            }
        } else if(ACTION_FIRE_ALARM.equals(action)){
            deliverNotification(context, "You has a work to do", "Click to navigate to note");
        }
    }

    public void setUpAlarm(Context context, long timestamp){
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService
                (Context.ALARM_SERVICE);
        Intent notifyIntent = new Intent(context, AlarmReceiver.class);
        notifyIntent.setAction(ACTION_FIRE_ALARM);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (context, NOTIFICATION_ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        if(alarmManager != null){
            alarmManager.set(AlarmManager.RTC, timestamp, notifyPendingIntent);
        }
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel(Context context) {

        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Magic note reminder notification channel",
                            NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Magic note reminder notification channel");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * Builds and delivers the notification.
     *
     * @param context, activity context.
     */
    private void deliverNotification(Context context, String title, String content) {
        // Create the content intent for the notification, which launches
        // this activity
        Intent contentIntent = new Intent(context, MagicNoteActivity.class);

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT);
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_twotone_access_alarm_24)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Deliver the notification
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
