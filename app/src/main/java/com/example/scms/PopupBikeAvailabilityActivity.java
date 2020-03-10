package com.example.scms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class PopupBikeAvailabilityActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String location = "";
    String [] availableBikes;

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_bike_availability);

        location = getIntent().getExtras().getString("loc");
        availableBikes = getIntent().getExtras().getStringArray("availableBikes");
        if(location == null) finish();
        setTitle("location");


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -100;

        getWindow().setAttributes(params);

        listView = (ListView) findViewById(R.id.ListView);
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for(int i=0; i < availableBikes.length; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("bikeID", availableBikes[i]);
            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.bike_layout;
        String [] from = {"bikeID"};


        int[] to = {R.id.bikeID};
        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);

        listView.setAdapter(adapter);//Set adapter
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
