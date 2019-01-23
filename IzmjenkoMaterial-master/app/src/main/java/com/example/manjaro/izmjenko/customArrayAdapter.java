package com.example.manjaro.izmjenko;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class customArrayAdapter extends ArrayAdapter {
    private Context context;
    private List<ClassInfo> classes;
    public customArrayAdapter(Context context, int resource, ArrayList<ClassInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.classes = objects;
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        ClassInfo Class= classes.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.customlist, null);

        TextView tvHeader= (TextView) view.findViewById(R.id.head);
        TextView tvStartTime= (TextView) view.findViewById(R.id.start);
        TextView tvEndTime= (TextView) view.findViewById(R.id.end);
        TextView tvSubject= (TextView) view.findViewById(R.id.sub);

        String meta=Read(getContext(),"dark.txt");

        if(!(meta.isEmpty())){
            tvHeader.setTextColor(Color.WHITE);
            tvStartTime.setTextColor(Color.WHITE);
            tvEndTime.setTextColor(Color.WHITE);
            tvSubject.setTextColor(Color.WHITE);
        }else{
            tvHeader.setTextColor(Color.BLACK);
            tvStartTime.setTextColor(Color.BLACK);
            tvEndTime.setTextColor(Color.BLACK);
            tvSubject.setTextColor(Color.BLACK);
        }



        tvHeader.setText(Class.header+". sat");
        tvStartTime.setText(Class.startTime);
        tvEndTime.setText(Class.endTime);
        tvSubject.setText(Class.subject);

        return view;
    }

    private String Read(Context context,String filename){

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

}
