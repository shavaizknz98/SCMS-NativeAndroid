package com.example.scms;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportUserActivity extends AppCompatActivity implements View.OnClickListener {

    private Button reportButton;
    private EditText bikeIDEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
        setTitle("Report a user");

        bikeIDEditText = (EditText) findViewById(R.id.reportBikeIDEditText);
        reportButton = (Button) findViewById(R.id.reportButton);

        reportButton.setOnClickListener(this);

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.reportButton:
                if(bikeIDEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter a bike ID.", Toast.LENGTH_SHORT).show();
                } else {
                    String bikeID = bikeIDEditText.getText().toString().trim();
                    if(!bikeID.contains("BIKE")) {
                        Toast.makeText(this, "Please enter a valid bike ID.", Toast.LENGTH_SHORT).show();
                    } else {
                        reportUser(bikeID);
                    }
                }
                break;
        }
    }

    public void reportUser(String bikeID) {
        Call<ResponseBody> call = RetrofitClient
                .getRetrofitClient()
                .getAPI()
                .reportUser(bikeID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String r = null;
                try {
                    r = response.body().string();
                } catch (IOException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }

                String status = "2";

                JSONObject resp = null;
                try {
                    resp = new JSONObject(r);
                    status = resp.getString("status");
                } catch (JSONException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }

                if(status.contains("0")) {
                    Toast.makeText(getApplicationContext(), "No user is currently riding this bike.", Toast.LENGTH_SHORT).show();
                } else if(status.contains("1")) {
                    Toast.makeText(getApplicationContext(), "User reported", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error, please try again later.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
