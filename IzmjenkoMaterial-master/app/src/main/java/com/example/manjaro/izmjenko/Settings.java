package com.example.manjaro.izmjenko;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    boolean isProf;
    String imePrezime="";
    String Class="";
    String domain="";
    String shift="";
    Switch sw;
    Switch swObavijesti;
    Switch dark;
    TextView imePrezimeTv,ClassTv,shiftTv,domainTv;
    ArrayList<String> alProfessors=new ArrayList<>();
    boolean showNotifications=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sw=findViewById(R.id.switch1);
        imePrezimeTv=findViewById(R.id.tvImePrezime);
        ClassTv=findViewById(R.id.tvClass);
        shiftTv=findViewById(R.id.tvShift);
        domainTv=findViewById(R.id.tvDomain);
        swObavijesti=findViewById(R.id.obavijesti_sw);
        dark=findViewById(R.id.sw_darkmode);



        sharedPref = getSharedPreferences("IZMJENKO.sharedPref",Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        domain=sharedPref.getString("IZMJENKO.domain","https://izmjenko.ddns.net");
        getProfessors();


        boolean darkTheme=sharedPref.getBoolean("IZMJENKO.darkTheme",false);

        if(darkTheme){
            BackColor();
            dark.setChecked(true);
        }

        if(!sharedPref.contains("IZMJENKO.isProf")){
            editor.putBoolean("IZMJENKO.isProf",false);
            editor.commit();
        }
        isProf=sharedPref.getBoolean("IZMJENKO.isProf",false);
        sw.setChecked(isProf);
        if(sw.isChecked()){
            ClassTv.setVisibility(View.GONE);
            shiftTv.setVisibility(View.GONE);
            imePrezimeTv.setVisibility(View.VISIBLE);
        }else{
            imePrezimeTv.setVisibility(View.GONE);
            ClassTv.setVisibility(View.VISIBLE);
            shiftTv.setVisibility(View.VISIBLE);
        }
        if(isProf){
            imePrezime=sharedPref.getString("IZMJENKO.imePrezime","null");
            if(!imePrezime.equals("null")){
                imePrezimeTv.setText("Ime i prezime: "+imePrezime);
            }

        }else{
            Class=sharedPref.getString("IZMJENKO.Class","null");
            if (!Class.equals("null")) {
                ClassTv.setText("Razred: "+Class);
            }
            shift=sharedPref.getString("IZMJENKO.shift","null");
            if(!shift.equals("null")){
                shiftTv.setText("Smjena: "+shift);
            }

        }

        showNotifications=sharedPref.getBoolean("IZMJENKO.showNotifications",true);
        swObavijesti.setChecked(showNotifications);

        swObavijesti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("IZMJENKO.showNotifications",true);

                }else{
                    editor.putBoolean("IZMJENKO.showNotifications",false);
                }
            }
        });



        domainTv.setText("Domena: "+domain);

        dark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("IZMJENKO.darkTheme",true);
                    Write("1",Settings.this,"dark.txt");
                    BackColor();

                }else{
                    editor.putBoolean("IZMJENKO.darkTheme",false);
                    Write("",Settings.this,"dark.txt");
                    Reverse();
                }
            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("IZMJENKO.isProf",true);
                    ClassTv.setVisibility(View.GONE);
                    shiftTv.setVisibility(View.GONE);
                    imePrezimeTv.setVisibility(View.VISIBLE);
                }else{
                    editor.putBoolean("IZMJENKO.isProf",false);
                    imePrezimeTv.setVisibility(View.GONE);
                    ClassTv.setVisibility(View.VISIBLE);
                    shiftTv.setVisibility(View.VISIBLE);
                }
            }
        });

        ClassTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                final EditText edittext = new EditText(getApplicationContext());
                edittext.setTextColor(Color.CYAN);
                alert.setTitle("Razred:");
                alert.setMessage("(Primjer: 1.H)");

                alert.setView(edittext);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Class = edittext.getText().toString();
                        if(Class.charAt(2)>='a' && Class.charAt(2)<='z'){
                            String class_string_temp=Class.substring(0,2)+(char)(Class.charAt(2)+'A'-'a');
                            Class=class_string_temp;
                        }
                        ClassTv.setText("Razred: "+Class);
                        editor.putString("IZMJENKO.Class",Class);
                    }
                });
                alert.show();
            }
        });

        shiftTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                alert.setTitle("Smjena");
                alert.setNegativeButton("A", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        shift="A";
                        editor.putString("IZMJENKO.shift",shift);
                        shiftTv.setText("Smjena: "+shift);
                    }
                });
                alert.setPositiveButton("B", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shift="B";
                        editor.putString("IZMJENKO.shift",shift);
                        shiftTv.setText("Smjena: "+shift);
                    }
                });
                alert.show();
            }

        });

        imePrezimeTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                final EditText edittext = new EditText(getApplicationContext());
                final AutoCompleteTextView autocompl=new AutoCompleteTextView(getApplicationContext());
                autocompl.setAdapter(getEmailAddressAdapter(getApplicationContext(),alProfessors));
                autocompl.setTextColor(Color.CYAN);
                alert.setTitle("Ime i prezime:");
                alert.setView(autocompl);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        imePrezime = autocompl.getText().toString();
                        imePrezimeTv.setText("Ime i prezime: "+imePrezime);
                        editor.putString("IZMJENKO.imePrezime",imePrezime);
                    }
                });
                alert.show();
            }

        });

        domainTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                final EditText edittext = new EditText(getApplicationContext());
                edittext.setTextColor(Color.CYAN);
                alert.setTitle("Domena");
                alert.setMessage("Primjer: https://izmjenko.ddns.net");

                alert.setView(edittext);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        domain = edittext.getText().toString();
                        editor.putString("IZMJENKO.domain",domain);
                        domainTv.setText("Domena: "+domain);
                    }
                });
                alert.show();
            }

        });



    }

    public void saveSettings(View v){

        editor.commit();
        finish();
    }

    private ArrayAdapter<String> getEmailAddressAdapter(Context context, ArrayList<String> alProf) {

        String[] professors=new String[alProf.size()];
        for(int i=0;i<professors.length;i++){
            professors[i]=alProf.get(i);
        }

        return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, professors);
    }

    private void getProfessors(){
        final RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getApplicationContext());

        final String urlGet=domain+"/professors?list=true";

        try {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, urlGet, new Response.Listener<String>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onResponse(String response) {

                            JSONObject parser = null;
                            try {
                                parser = new JSONObject(response);
                                JSONArray data = parser.getJSONArray("professors");
                                for (int i = 0; i < data.length(); i++) {
                                    alProfessors.add(data.get(i).toString());
                                }
                            } catch (JSONException e) {

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

    public void BackColor() {
        Button nes=findViewById(R.id.button);
        Switch prof=findViewById(R.id.switch1);
        Switch todo=findViewById(R.id.obavijesti_sw);
        Switch dark=findViewById(R.id.sw_darkmode);
        TextView imePrezimeTv,ClassTv,shiftTv,domainTv;
        imePrezimeTv=findViewById(R.id.tvImePrezime);
        ClassTv=findViewById(R.id.tvClass);
        shiftTv=findViewById(R.id.tvShift);
        domainTv=findViewById(R.id.tvDomain);

        nes.setBackgroundColor(Color.parseColor("#303030"));
        dark.setTextColor(Color.WHITE);
        nes.setTextColor(Color.WHITE);
        imePrezimeTv.setTextColor(Color.WHITE);
        ClassTv.setTextColor(Color.WHITE);
        shiftTv.setTextColor(Color.WHITE);
        domainTv.setTextColor(Color.WHITE);
        prof.setTextColor(Color.WHITE);
        todo.setTextColor(Color.WHITE);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
    }

    public void Reverse() {
        Button nes=findViewById(R.id.button);
        Switch prof=findViewById(R.id.switch1);
        Switch todo=findViewById(R.id.obavijesti_sw);
        Switch dark=findViewById(R.id.sw_darkmode);
        TextView imePrezimeTv,ClassTv,shiftTv,domainTv;
        imePrezimeTv=findViewById(R.id.tvImePrezime);
        ClassTv=findViewById(R.id.tvClass);
        shiftTv=findViewById(R.id.tvShift);
        domainTv=findViewById(R.id.tvDomain);

        nes.setBackgroundResource(android.R.drawable.btn_default);
        dark.setTextColor(Color.BLACK);
        nes.setTextColor(Color.BLACK);
        imePrezimeTv.setTextColor(Color.BLACK);
        ClassTv.setTextColor(Color.BLACK);
        shiftTv.setTextColor(Color.BLACK);
        domainTv.setTextColor(Color.BLACK);
        prof.setTextColor(Color.BLACK);
        todo.setTextColor(Color.BLACK);

        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.WHITE);
    }

    private void Write(String data, Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }




}
