package com.example.manjaro.izmjenko;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class BackgroundWorker extends Worker {

    public BackgroundWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Worker.Result doWork() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);;
        int id = createID();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "Izmjenko")
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle("Izmjenko")
                .setContentText("Objavljene izmjene!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setLights(0xff00ff00, 300, 100)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Objavljene izmjene!"))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(id, mBuilder.build());
        return Worker.Result.success();
    }
    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}
