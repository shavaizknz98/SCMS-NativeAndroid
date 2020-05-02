package com.example.scms;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ResponseCache;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LottieAnimationView emptyHistoryAnimation;
    private TextView emptyHistoryTextView;
    private ListView listView;
    private String[] starttimes, endtimes, startlocs, endlocs, costs, dates;

    private OnFragmentInteractionListener mListener;

    private int lenHistory = 0;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

        View v = inflater.inflate(R.layout.fragment_history, container, false);
        emptyHistoryAnimation = v.findViewById(R.id.noHistoryAnim);
        emptyHistoryTextView = v.findViewById(R.id.textViewNoHistory);
        listView = (ListView) v.findViewById(R.id.historyListView);
        ConstraintLayout constraintLayout = (ConstraintLayout) v.findViewById(R.id.historyConstraintLayout);

        prefs = getContext().getSharedPreferences(LoginTab.SCMS_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();

        getHistory();

        return v;
    }

    void getHistory() {
        String email = prefs.getString("useremail", "");
        Call<ResponseBody> call = RetrofitClient
                .getRetrofitClient()
                .getAPI()
                .getUserInfo(email);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String r = null;
                try {
                    r = response.body().string();
                    Log.d("AAAAA", "onResponse: respbodystring" + r);
                } catch (IOException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }

                JSONObject resp = null;
                try {
                    int totalRides;
                    resp = new JSONObject(r);
                    JSONObject user = resp.getJSONObject("result");
                    JSONArray history = user.getJSONArray("history");

                    totalRides = history.length();
                    lenHistory = totalRides;

                    Log.d("AAAAA", "onResponse: lenhistory" + lenHistory);

                    if(lenHistory > 0) {
                        startlocs = new String[totalRides];
                        endlocs = new String[totalRides];
                        dates = new String[totalRides];
                        starttimes = new String[totalRides];
                        endtimes = new String[totalRides];
                        costs = new String[totalRides];

                        for(int i = 0; i < totalRides; i++) {
                            JSONObject tmp = history.getJSONObject(i);
                            Log.d("AAAAA", "onResponse: jsonobject" + tmp.toString());
                            startlocs[i] = tmp.getString("fromLocation");
                            endlocs[i] = tmp.getString("toLocation");
                            starttimes[i] = tmp.getString("rideStartTime");
                            endtimes[i] = tmp.getString("rideEndTime");
                            costs[i] = tmp.getString("rideCost");
                            dates[i] = tmp.getString("reservationDate");
                        }

                        listView.setAdapter(new HistoryAdapter(dates, startlocs, endlocs, starttimes, endtimes, costs, getContext()));
                    }
                    if(lenHistory == 0) {
                        listView.setVisibility(View.GONE);
                    } else {
                        emptyHistoryAnimation.setVisibility(View.GONE);
                        emptyHistoryTextView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
