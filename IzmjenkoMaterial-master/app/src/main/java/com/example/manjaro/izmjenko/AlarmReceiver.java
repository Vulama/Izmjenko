package com.example.manjaro.izmjenko;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    SharedPreferences sharedPref;
    boolean isProf=false;
    String dateTomorrow="";
    String domain="";
    Context context;
    String imePrezime="";
    String shift="";
    String Class="";
    String urlGet="";
    String lastResponse="";

    @Override
    public void onReceive(final Context arg0, Intent arg1) {

        context=arg0;
        sharedPref = arg0.getSharedPreferences("IZMJENKO.sharedPref",Context.MODE_PRIVATE);
        getDate();
        readDomain();
        readIsProf();


        if(isProf){
            readImePrezime();

        } else{
            readClass();
            readShift();
        }

        try{
            lastResponse= FileIO.readFromFile(context,"response.txt");
        }catch (FileNotFoundException e){
            lastResponse="No history";
        }

        final RequestQueue ExampleRequestQueue = Volley.newRequestQueue(context);
        if(isProf){
            urlGet = domain+"/professors?date=" + dateTomorrow + "&name=" + imePrezime;
        }else{
            urlGet = domain+"/changes?date=" + dateTomorrow + "&class=" +Class+ "&shift=" + shift;
        }
        urlGet=urlGet.replaceAll(" ","%20");
        Log.e("URLGET",urlGet);

        try {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, urlGet, new Response.Listener<String>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(String response) {
                            if(!response.equals(lastResponse)){
                                Intent intent = new Intent(arg0, MainActivity.class);;
                                int id = createID();

                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(arg0, "Izmjenko")
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

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(arg0);
                                notificationManager.notify(id, mBuilder.build());

                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    ExampleRequestQueue.add(ExampleStringRequest);
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {

        }



    }

    private void getDate(){
        Calendar calendar = Calendar.getInstance();
        FileIO.writeToFile(calendar.toString(),context,"wakeup");
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        dateTomorrow = mdformat.format(calendar.getTime());
    }
    private void readDomain(){
        domain=sharedPref.getString("IZMJENKO.domain","https://izmjenko.ddns.net");
    }
    private void readIsProf(){
        if(sharedPref.contains("IZMJENKO.isProf")){
            isProf=sharedPref.getBoolean("IZMJENKO.isProf",false);
        }
    }

    private void readImePrezime(){
        if(sharedPref.contains("IZMJENKO.imePrezime")){
            imePrezime=sharedPref.getString("IZMJENKO.imePrezime","null");
        }
    }
    private void readClass(){
        if(sharedPref.contains("IZMJENKO.Class")){
            Class=sharedPref.getString("IZMJENKO.Class","null");
        }
    }
    private void readShift(){
        if(sharedPref.contains("IZMJENKO.shift")){
            shift=sharedPref.getString("IZMJENKO.shift","null");
        }
    }

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }
}
