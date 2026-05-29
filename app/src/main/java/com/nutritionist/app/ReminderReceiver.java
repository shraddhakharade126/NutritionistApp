package com.nutritionist.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "meal_reminder";

    @Override
    public void onReceive(Context context, Intent intent) {
        String mealName = intent.getStringExtra("meal_name");
        if (mealName == null) mealName = "Meal Time!";

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ── CHANGED: channel now has vibration + sound ────────────
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            AudioAttributes audioAttr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Meal Reminders", NotificationManager.IMPORTANCE_HIGH);

            channel.enableVibration(true);                          // ADDED
            channel.setVibrationPattern(new long[]{0, 400, 200, 400}); // ADDED
            channel.setSound(soundUri, audioAttr);                  // ADDED
            channel.enableLights(true);                             // ADDED

            manager.createNotificationChannel(channel);
        }

        // ── CHANGED: builder now has sound + vibrate pattern ──────
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🥗 Meal Reminder")
                .setContentText("Time for: " + mealName)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("⏰ Time for your meal: " + mealName
                                + "\nStay on track with your diet plan!"))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(defaultSound)                             // ADDED
                .setVibrate(new long[]{0, 400, 200, 400, 200, 400}); // ADDED

        manager.notify((int) System.currentTimeMillis(), builder.build());

        // ── ADDED: vibrate phone directly for instant feedback ────
        vibratePhone(context);
    }

    // ── ADDED: new method ─────────────────────────────────────────
    private void vibratePhone(Context context) {
        long[] pattern = {0, 400, 200, 400, 200, 400};
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vm = (VibratorManager)
                        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                if (vm != null) {
                    vm.getDefaultVibrator()
                            .vibrate(VibrationEffect.createWaveform(pattern, -1));
                }
            } else {
                Vibrator vibrator = (Vibrator)
                        context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
                    } else {
                        vibrator.vibrate(pattern, -1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}