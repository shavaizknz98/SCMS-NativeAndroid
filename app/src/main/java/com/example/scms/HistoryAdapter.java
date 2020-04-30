package com.example.scms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class HistoryAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> startLocs, endLocs, startTimes, endTimes, costs, dates;
    private Context context;

    public HistoryAdapter(String[] dates, String[] startLocs, String[] endLocs, String[] startTimes, String[] endTimes, String[] costs, Context context) {
        this.context = context;
        this.dates = new ArrayList<String>(Arrays.asList(dates));
        this.startLocs = new ArrayList<String>(Arrays.asList(startLocs));
        this.endLocs = new ArrayList<String>(Arrays.asList(endLocs));
        this.startTimes = new ArrayList<String>(Arrays.asList(startTimes));
        this.endTimes = new ArrayList<String>(Arrays.asList(endTimes));
        this.costs = new ArrayList<String>(Arrays.asList(costs));
        this.context = context;
    }

    @Override
    public int getCount() {
        return startLocs.size();
    }

    @Override
    public Object getItem(int pos) {
        return startLocs.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //items dont have id so return 0
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = LayoutInflater.from(context).inflate(R.layout.history_layout, parent, false);//inflater.inflate(R.layout.bike_layout, null);
        }

        TextView startLocTextView = (TextView) view.findViewById(R.id.textViewStartLoc);
        TextView endLocTextView = (TextView) view.findViewById(R.id.textViewEndLoc);
        TextView startTimeTextView = (TextView) view.findViewById(R.id.textViewStartTime);
        TextView endTimeTextView = (TextView) view.findViewById(R.id.textViewEndTime);
        TextView costTextView = (TextView) view.findViewById(R.id.textViewRideCost);
        TextView dateTextView = (TextView) view.findViewById(R.id.textViewDate);

        startLocTextView.setText(startLocs.get(position));
        endLocTextView.setText(endLocs.get(position));
        startTimeTextView.setText(startTimes.get(position));
        endTimeTextView.setText(endTimes.get(position));
        costTextView.setText(costs.get(position));
        dateTextView.setText(dates.get(position));



        return view;
    }
}
