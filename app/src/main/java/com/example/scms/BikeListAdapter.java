package com.example.scms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BikeListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list;
    private Context context;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public BikeListAdapter(String[] bikes, Context context) {
        this.context = context;
        this.list = new ArrayList<String>(Arrays.asList(bikes));

        prefs = context.getSharedPreferences(LoginTab.SCMS_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
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
            view = LayoutInflater.from(context).inflate(R.layout.bike_layout, parent, false);//inflater.inflate(R.layout.bike_layout, null);
        }

        TextView bikeIDTextView = (TextView) view.findViewById(R.id.bikeID);
        Button reserve = (Button) view.findViewById(R.id.reserveButton);

        final String bikeID = (String) getItem(position);
        bikeIDTextView.setText(bikeID);
        editor.putString("bikereserved", bikeID).commit();

        final String userID = prefs.getString("useremail", "");

        reserve.setText("Reserve");


        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = RetrofitClient
                        .getRetrofitClient()
                        .getAPI()
                        .reserveBike(userID, bikeID);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String r = null;
                        try {
                            r = response.body().string();
                        } catch (IOException e) {
                            Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                        }

                        JSONObject resp = null;
                        String status = "";
                        try {
                            resp = new JSONObject(r);
                            status = resp.getString("status");
                        } catch (JSONException e) {
                            Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                        }

                        /*
                        0 - not available
                        1 - success
                        2 - server error
                        3 - already reserved
                        4 - currently riding
                         */
                        switch(status) {
                            case "0":
                                Toast.makeText(context, "This bike is no longer available.", Toast.LENGTH_SHORT).show();
                                break;
                            case "1":
                                Toast.makeText(context, "Reservation successful.", Toast.LENGTH_SHORT).show();
                                break;
                            case "2":
                                Toast.makeText(context, "Server error. Please try again.", Toast.LENGTH_SHORT).show();
                                break;
                            case "3":
                                Toast.makeText(context, "You have already reserved a bike.", Toast.LENGTH_SHORT).show();
                                break;
                            case "4":
                                Toast.makeText(context, "You are already in a ride.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(context, "Error. Please try again.", Toast.LENGTH_SHORT).show();
                        }

                        if(status == "1") {
                            Toast.makeText(context, "Please scan the QR code within 5 minutes.", Toast.LENGTH_SHORT).show();
                            Call<ResponseBody> timercall = RetrofitClient
                                    .getRetrofitClient()
                                    .getAPI()
                                    .startReservationTimer("");

                            timercall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) { }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) { }
                            });
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });
                ((PopupBikeAvailabilityActivity) context).finish();

            }
        });

        return view;
    }
}
