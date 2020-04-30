package com.example.scms;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.MapView;

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
    private LottieAnimationView emptyHistory;
    private TextView emptyHistoryTextView;
    private ListView listView;
    private String[] starttimes, endtimes, startlocs, endlocs, costs, dates;

    private OnFragmentInteractionListener mListener;

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
        emptyHistory = v.findViewById(R.id.noHistoryAnim);
        emptyHistoryTextView = v.findViewById(R.id.textViewNoHistory);

        emptyHistory.setVisibility(View.GONE);
        emptyHistoryTextView.setVisibility(View.GONE);

        getHistory();

        listView = (ListView) v.findViewById(R.id.historyListView);
        listView.setAdapter(new HistoryAdapter(dates, startlocs, endlocs, starttimes, endtimes, costs, getContext()));


        return v;

    }

    void getHistory() {
        //todo server call for history, very small bit of code
        startlocs = new String[6];
        startlocs[0] = "PHY"; startlocs[1] = "PHY"; startlocs[2] = "PHY"; startlocs[3] = "PHY"; startlocs[4] = "PHY"; startlocs[5] = "PHY";
        endlocs = new String[6];
        endlocs[0] = "ESB"; endlocs[1] = "SBA"; endlocs[2] = "ESB"; endlocs[3] = "SBA"; endlocs[4] = "ESB"; endlocs[5] = "SBA";
        starttimes = new String[6];
        starttimes[0] = "03:04:24"; starttimes[1] = "01:33:03"; starttimes[2] = "01:01:05"; starttimes[3] = "04:02:55"; starttimes[4] = "10:45:43"; starttimes[5] = "04:22:51";
        endtimes = new String[6];
        endtimes[0] = "03:05:13"; endtimes[1] = "01:40:10"; endtimes[2] = "01:11:13"; endtimes[3] = "04:07:10"; endtimes[4] = "10:46:44"; endtimes[5] = "04:24:39";
        costs = new String[6];
        costs[0] = "2.40"; costs[1] = "11.75"; costs[2] = "13.25"; costs[3] = "6.00"; costs[4] = "3.50"; costs[5] = "3.30";
        dates = new String[6];
        dates[0] = "27/04/2020"; dates[1] = "26/04/2020"; dates[2] = "25/04/2020"; dates[3] = "24/04/2020"; dates[4] = "23/04/2020"; dates[5] = "22/04/2020";

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
