package com.example.manjaro.izmjenko;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView lv;
    FloatingActionButton fab;

    private PendingIntent pendingIntent;
    private AlarmManager manager;
    boolean showSchedule=false;
    boolean isProf=false;
    String dateTomorrow="";
    String dateToday="";
    String shift="";
    String dateCustom="";

    String Class="";
    String urlGet="";
    String domain="";
    String imePrezime="";
    String cacheToday="";
    String cacheTomorrow="";
    boolean startedAnotherIntent=false;
    ArrayList<String> changes=new ArrayList<>();
    ArrayList<String> schedule=new ArrayList<>();
    ArrayList<String> header=new ArrayList<>();
    ArrayList<String> data2=new ArrayList<>();
    ArrayList<String> startTime=new ArrayList<>();
    ArrayList<String> endTime=new ArrayList<>();
    ArrayList<ClassInfo> alClassInfo=new ArrayList<>();
    ArrayList<ClassInfo> alChangesInfo=new ArrayList<>();
    Toolbar toolbar;
    SharedPreferences sharedPref;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntent().setAction("Already created");
        createNotificationChannel();

        BackColor();


        sharedPref = getSharedPreferences("IZMJENKO.sharedPref",Context.MODE_PRIVATE);

        getDate();

        readDomain();
        readIsProf();
        Log.e("isProf",String.valueOf(isProf));
        if(isProf){
            readImePrezime();
            Log.e("imePrezime",imePrezime);
        } else{
            readClass();
            readShift();
            Log.e("Class",Class);
            Log.e("shift",shift);
        }


        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 100000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);




        lv=findViewById(R.id.listV);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(dateTomorrow);

        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!showSchedule){
                    fab.setImageDrawable(getDrawable(R.drawable.ic_changes));
                }else{
                    fab.setImageDrawable(getDrawable(R.drawable.ic_schedule));
                }
                showSchedule=!showSchedule;
                showChanges();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_tomorrow);
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void getDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
        dateToday= mdformat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        dateTomorrow = mdformat.format(calendar.getTime());
    }
    private void readDomain(){
      domain=sharedPref.getString("IZMJENKO.domain","https://izmjenko.ddns.net");
    }
    private void readIsProf(){
        if(sharedPref.contains("IZMJENKO.isProf")){
            isProf=sharedPref.getBoolean("IZMJENKO.isProf",false);
        }else{
            if(!startedAnotherIntent){
                startActivity(new Intent(this,Settings.class));
                startedAnotherIntent=true;
            }

        }
    }
    private void readImePrezime(){
        if(sharedPref.contains("IZMJENKO.imePrezime")){
            imePrezime=sharedPref.getString("IZMJENKO.imePrezime","null");
        }else{
            if(!startedAnotherIntent){
                startActivity(new Intent(this,Settings.class));
                startedAnotherIntent=true;
            }
        }
    }
    private void readClass(){
        if(sharedPref.contains("IZMJENKO.Class")){
            Class=sharedPref.getString("IZMJENKO.Class","null");
        }else{
            if(!startedAnotherIntent){
                startActivity(new Intent(this,Settings.class));
                startedAnotherIntent=true;
            }
        }
    }
    private void readShift(){
        if(sharedPref.contains("IZMJENKO.shift")){
            shift=sharedPref.getString("IZMJENKO.shift","null");
        }else{
            if(!startedAnotherIntent){
                startActivity(new Intent(this,Settings.class));
                startedAnotherIntent=true;
            }
        }
    }
    private void updateListView(String response){

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        fab.setVisibility(View.GONE);
        int id = item.getItemId();
        if (id == R.id.nav_today) {
            toolbar.setTitle(dateToday);
            if(cacheToday.equals("")){
                final RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getApplicationContext());
                if(isProf){
                    urlGet = domain+"/professors?date=" + dateToday + "&name=" + imePrezime;
                }else{
                    urlGet = domain+"/changes?date=" + dateToday + "&class=" +Class+ "&shift=" + shift;
                }
                urlGet=urlGet.replaceAll(" ","%20");

                try {
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, urlGet, new Response.Listener<String>() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void onResponse(String response) {
                                    fab.setVisibility(View.VISIBLE);
                                    cacheToday=response;
                                    parseResponse(response);
                                    showChanges();
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
            }else{
                fab.setVisibility(View.VISIBLE);
                parseResponse(cacheToday);
                showChanges();
            }

        } else if (id == R.id.nav_tomorrow) {
            toolbar.setTitle(dateTomorrow);
            if(cacheTomorrow.equals("")){
                final RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getApplicationContext());
                if(isProf){
                    urlGet = domain+"/professors?date=" + dateTomorrow + "&name=" + imePrezime;
                }else{
                    urlGet = domain+"/changes?date=" + dateTomorrow + "&class=" +Class+ "&shift=" + shift;
                }
                urlGet=urlGet.replaceAll(" ","%20");

                try {
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, urlGet, new Response.Listener<String>() {
                                @SuppressLint("RestrictedApi")
                                @Override
                                public void onResponse(String response) {
                                    fab.setVisibility(View.VISIBLE);
                                    cacheTomorrow=response;
                                    parseResponse(response);
                                    showChanges();
                                    FileIO.writeToFile(response,getApplicationContext(),"response.txt");
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
            }else{
                fab.setVisibility(View.VISIBLE);
                parseResponse(cacheTomorrow);
                showChanges();
            }
        }
        else if(id==R.id.nav_date){
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            dateCustom=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            toolbar.setTitle(dateCustom);

                            final RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getApplicationContext());
                            if(isProf){
                                urlGet = domain+"/professors?date=" + dateCustom + "&name=" + imePrezime;
                            }else{
                                urlGet = domain+"/changes?date=" + dateCustom + "&class=" +Class+ "&shift=" + shift;
                            }
                            urlGet=urlGet.replaceAll(" ","%20");

                            try {
                                Thread thread = new Thread(new Runnable() {

                                    @Override
                                    public void run() {

                                        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, urlGet, new Response.Listener<String>() {
                                            @SuppressLint("RestrictedApi")
                                            @Override
                                            public void onResponse(String response) {
                                                fab.setVisibility(View.VISIBLE);
                                                parseResponse(response);
                                                showChanges();
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
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }

        else if(id==R.id.nav_data2){
            Toast.makeText(this,"dodatna tablica - bit ce kad bude",Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void parseResponse(String response){
        emptyArrayLists();
            if(response.equals("Not found")){
                changes.add("Izmjene nisu objavljene!");
            }else{

                if(isProf){
                    parseHeaderProf();
                    parseChangesProf(response);
                    parseScheduleProf(response);
                    parseStartTimeProf();
                    parseEndTImeProf();

                }else{
                    parseHeader(response);
                    parseChanges(response);
                    parseSchedule(response);
                    parseStartTime(response);
                    parseEndTime(response);
                }


                ///////creatig ClassInfo objects for showing in custom listView////
                for(int i=0;i<header.size();i++){
                    alClassInfo.add(new ClassInfo(schedule.get(i),startTime.get(i),endTime.get(i),header.get(i)));
                }
                for(int i=0;i<header.size();i++){
                    alChangesInfo.add(new ClassInfo(changes.get(i),startTime.get(i),endTime.get(i),header.get(i)));
                }

            }


    }

    private void emptyArrayLists(){
        changes=new ArrayList<>();
        schedule=new ArrayList<>();
        data2=new ArrayList<>();
        header=new ArrayList<>();
        startTime=new ArrayList<>();
        endTime=new ArrayList<>();
        alClassInfo=new ArrayList<>();
        alChangesInfo=new ArrayList<>();
    }

    private void parseChangesProf(String response){
        try {
            JSONObject parser = new JSONObject(response);
            JSONArray data = parser.getJSONArray("changes");
            boolean imaIzmjena=false;
            for (int i = 0; i < header.size(); i++) {
                if(!data.getString(i).equals("null")){
                    changes.add(data.getString(i));
                    imaIzmjena=true;
                }
                else changes.add("");
            }
            if(!imaIzmjena){
                changes.set(0,"Nema izmjena");
            }

        } catch (JSONException e) {
            changes.add("Nema izmjena");
            for(int i=1;i<header.size();i++){
                changes.add("");
            }
        }
    }
    private void parseHeaderProf(){
        for(int i=1;i<15;i++){
            if(i>7)header.add(String.valueOf(i-7));
            else header.add(String.valueOf(i));
        }
    }
    private void parseStartTimeProf(){
        for(int i=0;i<header.size();i++){
            startTime.add("Nepoznato");
        }
    }
    private void parseEndTImeProf(){
        for(int i=0;i<header.size();i++){
            endTime.add("Nepoznato");
        }
    }
    private void parseScheduleProf(String response){
        try{
            JSONObject parser = new JSONObject(response);
            JSONArray data = parser.getJSONArray("schedule");

            if (data != null) {
                for (int i = 1; i < data.length(); i++) {
                    if(!data.getString(i).equals("null")){
                        schedule.add(data.getString(i));
                    }else{
                        schedule.add("");
                    }
                }
                for(int i=0;i<changes.size();i++){
                    if(!changes.get(i).equals("") && !changes.get(i).equals("Nema izmjena")){
                        schedule.set(i,schedule.get(i)+" ("+changes.get(i)+" - izmjena)").trim();
                    }
                }
            }
        }
        catch(JSONException e){
            schedule.add("Nema rasporeda!");
        }
    }

    private void parseChanges(String response){
            try {
                JSONObject parser = new JSONObject(response);
                JSONArray data = parser.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    changes.add(data.getString(i));
                }
                for(int i=data.length();i<header.size();i++){
                    changes.add("");
                }

            } catch (JSONException e) {
                changes.add("Nema izmjena");
                for(int i=1;i<header.size();i++){
                    changes.add("");
                }
            }
    }


    private void showChanges(){
        if(showSchedule){
            ArrayAdapter<ClassInfo> adapter=new customArrayAdapter(this,0,alClassInfo);
            lv.setAdapter(adapter);
        }else{
            ArrayList<String> final_showing=new ArrayList<>();
            if(changes.get(0).equals("Izmjene nisu objavljene!")){
                final_showing.add("Izmjene nisu objavljene!");
                lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,final_showing));
            }
            else if(changes.get(0).equals("Nema izmjena")){
                final_showing.add("Nema izmjena");
                lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,final_showing));
            }
            else{
                for (int i = 0; i < header.size(); i++) {
                    ArrayAdapter<ClassInfo> adapter=new customArrayAdapter(this,0,alChangesInfo);
                    lv.setAdapter(adapter);
                }
            }
        }
    }



    private void parseSchedule(String response){
        try{
            JSONObject parser = new JSONObject(response);
            JSONArray data = parser.getJSONArray("schedule");

            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    if(!data.getString(i).equals("null")){
                        schedule.add(data.getString(i));
                    }else{
                        schedule.add("");
                    }
                }
                for(int i=0;i<changes.size();i++){
                    if(!changes.get(i).equals("") && !changes.get(i).equals("Nema izmjena")){
                        if(!schedule.get(i).equals("")) schedule.set(i,schedule.get(i)+" ("+changes.get(i)+" - izmjena)");
                        else schedule.set(i,changes.get(i)+" - izmjena");
                    }
                }
            }
        }
        catch(JSONException e){
            schedule.add("Nema rasporeda!");
        }
    }
    private void parseHeader(String response){
        try{
            JSONObject parser = new JSONObject(response);
            JSONArray data = parser.getJSONArray("header");

            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    header.add(data.getString(i));
                }
            }
        }
        catch(JSONException e){
            //izmjene za profesore

        }
    }
    private void parseStartTime(String response){
        try{
            JSONObject parser = new JSONObject(response);
            JSONArray data = parser.getJSONArray("starttime");

            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    startTime.add(data.getString(i));
                }
            }
        }
        catch(JSONException e){
            for(int i=0;i<header.size();i++){
                startTime.add("Nepoznato");
            }
        }
    }
    private void parseEndTime(String response){
        try{
            JSONObject parser = new JSONObject(response);
            JSONArray data = parser.getJSONArray("endtime");

            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    endTime.add(data.getString(i));
                }
            }
        }
        catch(JSONException e){
            for(int i=0;i<header.size();i++){
                endTime.add("Nepoznato");
            }
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Izmjenko";
            String description = "Sustav izmjena u rasporedu";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Izmjenko", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onResume() {
        Log.v("Example", "onResume");

        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        // Remove the unique action so the next time onResume is called it will restart
        else
            getIntent().setAction(null);

        super.onResume();
    }

    public void BackColor() {
        DrawerLayout drawer=findViewById(R.id.drawer_layout);
        drawer.setBackgroundColor(Color.BLACK);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setBackgroundColor(Color.DKGRAY);

    }

}

