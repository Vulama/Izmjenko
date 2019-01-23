package com.example.manjaro.izmjenko;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class BootReceiver extends BroadcastReceiver {
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /*Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            int interval = 100000;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);*/

            PeriodicWorkRequest.Builder WorkBuilder =
                    new PeriodicWorkRequest.Builder(BackgroundWorker.class, 3,
                            TimeUnit.MINUTES);

            PeriodicWorkRequest myWork = WorkBuilder.build();
            WorkManager.getInstance().enqueue(myWork);
        }
    }
}
