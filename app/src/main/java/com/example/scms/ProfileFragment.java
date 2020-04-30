package com.example.scms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private JSONObject userinfoobj;

    private TextView titleTextView, currentStatusTextView, numRidesTextView, totalCostTextView, violationScoretextView;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        prefs = getContext().getSharedPreferences(LoginTab.SCMS_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();

        titleTextView = v.findViewById(R.id.textViewTitle);
        currentStatusTextView = v.findViewById(R.id.textViewCurrentStatusVal);
        numRidesTextView = v.findViewById(R.id.textViewCostVal);
        totalCostTextView = v.findViewById(R.id.textViewCompletedRidesVal);
        violationScoretextView = v.findViewById(R.id.textViewViolationScoreVal);

        currentStatusTextView.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.textViewCurrentStatusVal:
                String t = totalCostTextView.getText().toString().trim();
                if(t == "No booking") {
                    Toast.makeText(getContext(), "No bike booked!", Toast.LENGTH_SHORT).show();
                } else if(t == "Ride in progress") {
                    Toast.makeText(getContext(), "Ride is in progress!", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder((getContext()));//add 2nd parameter here R.style.theme
                    builder.setTitle("Attention!");
                    builder.setIcon(R.drawable.ic_warning_black_24dp);
                    builder.setMessage("Do you want to cancel your current reservation?")
                            .setCancelable(true)
                            .setPositiveButton("Yes, cancel the reservation", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String email = prefs.getString("useremail", "");
                                    final String bikeid = prefs.getString("bikereserved", "");
                                    Call<ResponseBody> call = RetrofitClient
                                            .getRetrofitClient()
                                            .getAPI()
                                            .cancelReservation(email, bikeid);
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
                                            String status = null;
                                            try {
                                                resp = new JSONObject(r);
                                                status = resp.getString("status");
                                            } catch (JSONException e) {
                                                Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                                            }

                                            switch(status) {
                                                case "1":
                                                    Toast.makeText(getContext(), "You did not reserve this bike!", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case "2":
                                                    Toast.makeText(getContext(), "Reservation cancelled successfully", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case "3":
                                                    Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show();
                                                    break;
                                                default:
                                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) { }
                                    });
                                }
                            });
                }
        }
    }

   /* public void tr() {
        Call<ResponseBody> call = RetrofitClient
                .getRetrofitClient()
                .getAPI()
                .signUpUser(email, phoneNumber, fullName, password);

        //make post request here
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s = response.body().string();
                    //Log.d(TAG, "onResponse: " + s);
                    Toast.makeText(getActivity(), "onResponse: " + s, Toast.LENGTH_LONG).show();
                    JSONObject resp = null;
                    int code = 3;   //if resp is null then user error
                    try {
                        resp = new JSONObject(s);
                        code = resp.getInt("status");
                        Log.d("AAAAA", "onResponse: status = " + code);
                    } catch(JSONException e) {
                        Log.e("AAAAA", "onResponse: " + e.toString());
                    }


                } catch (IOException e) {
                    Log.e("AAAAA", "onResponse: " + e.getStackTrace());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
    }

*/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserData();
        //updateData();
    }

    private void getUserData() {
        final String email = prefs.getString("useremail", "");
        Log.d("AAAAA", "getUserData: useremail " + email);
        Call<ResponseBody> call1 = RetrofitClient
                .getRetrofitClient()
                .getAPI()
                .getUserProfile(email);

        Log.d("AAAAA", "getUserData: got here0");
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("AAAAA", "getUserData: got here0");
                String r = null;
                try {
                    r = response.body().string();
                    Log.d("AAAAA", "onResponse: respbodystring" + r);
                } catch (IOException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }

                JSONObject resp = null;
                try {
                    resp = new JSONObject(r);
                    Log.d("AAAAA", "onResponse: resp " + resp);
                    userinfoobj = resp;
                } catch (JSONException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("AAAAA", "getUserData: fail");

            }
        });
    }

    private void updateData() {
        String name = "Sample";
        int numRides = 0;
        float total_cost = 0;
        String violationscore = "";
        String current_status = "";
        int flag = 0;
        try {
            Log.d("AAAAA", "updateData: userinfoobj " + userinfoobj);
            JSONObject user = userinfoobj.getJSONObject("result");
            name = user.getString("fullname");
            JSONArray hist = user.getJSONArray("history");
            numRides = hist.length();
            violationscore = user.getString("violationScore");
            if(user.getString("status") != "NA") {
                flag = 1;
            }

            for(int i = 0; i < hist.length(); i++) {
                JSONObject item = hist.getJSONObject(i);
                String tmpcost = item.getString("rideCost");
                if(tmpcost != "NA") {
                    total_cost += Float.valueOf(tmpcost);
                }
                if(flag == 1 && i == 0) {
                    current_status = item.getString("status");
                }
            }
        } catch (JSONException e) {
            Log.d("AAAAA", "updateData: " + e.getStackTrace());
        }

        titleTextView.setText(name);

        if(flag == 0) {
            currentStatusTextView.setText("No booking");
        } else {
            if(current_status == "reserved") {
                currentStatusTextView.setText("Bike reserved");
            } else {
                currentStatusTextView.setText("Ride in progress");
            }
        }

        numRidesTextView.setText(numRides);

        totalCostTextView.setText(String.valueOf(total_cost));

        violationScoretextView.setText(violationscore);

        //todo faq, report user, email devs
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
