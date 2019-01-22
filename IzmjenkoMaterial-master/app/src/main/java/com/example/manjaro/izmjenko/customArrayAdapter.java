package com.example.manjaro.izmjenko;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;

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

        tvHeader.setTextColor(Color.WHITE);
        tvStartTime.setTextColor(Color.WHITE);
        tvEndTime.setTextColor(Color.WHITE);
        tvSubject.setTextColor(Color.WHITE);

        tvHeader.setText(Class.header+". sat");
        tvStartTime.setText(Class.startTime);
        tvEndTime.setText(Class.endTime);
        tvSubject.setText(Class.subject);

        return view;


    }


}
