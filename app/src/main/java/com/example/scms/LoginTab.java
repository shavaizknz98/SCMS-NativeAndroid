package com.example.scms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginTab extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String SCMS_PREFS = "SCMS_PREFS";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button loginButton;
    private TextInputLayout loginLayout;

    private Button signInButton;
    private EditText emailAddrEditText;
    private EditText passwordEditText;
    private OnFragmentInteractionListener mListener;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public LoginTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginTab.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginTab newInstance(String param1, String param2) {
        LoginTab fragment = new LoginTab();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton = view.findViewById(R.id.logInButton);

        emailAddrEditText = view.findViewById(R.id.emailAddrEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);

        prefs = getContext().getSharedPreferences(SCMS_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();

        emailAddrEditText.setText("ahmedhamza199@gmail.com");
        passwordEditText.setText("Becooler-98");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAddrEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                Call<ResponseBody> call = RetrofitClient
                        .getRetrofitClient()
                        .getAPI()
                        .signInUser(email,password);



                //make post request here
                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String r = null;
                        try {
                            r = response.body().string();
                            //Toast.makeText(getActivity(), r, Toast.LENGTH_SHORT).show();
                            Log.d("AAAAA", "onResponse: login resp: " + r);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JSONObject resp = null;
                    /*
                    code = 0 user not registered
                    code = 1 login successfully
                    code = 2 server error
                    code = 3 wrong password
                    code = 4 user banned
                     */
                        int code = 4;
                        try {
                            resp = new JSONObject(r);
                            code = resp.getInt("status");
                        } catch (JSONException e) {
                            Log.d("BookFragment", "onResponse: " + e.getStackTrace());
                        }

                        if (code == 0) {
                            Toast.makeText(getContext(), "User is not registered", Toast.LENGTH_LONG).show();
                        } else if (code == 1) {
                            editor.putString("useremail", emailAddrEditText.getText().toString().trim()).commit();
                            Intent toNavigationActivity = new Intent(getContext(), NavigationActivity.class);
                            startActivity(toNavigationActivity);
                            getActivity().finish();
                        } else if (code == 2) {
                            Toast.makeText(getActivity(), "Server error, please try again later", Toast.LENGTH_LONG).show();
                        } else if (code == 3) {
                            Toast.makeText(getActivity(), "Incorrect password", Toast.LENGTH_LONG).show();
                        } else if (code == 4) {
                            Toast.makeText(getActivity(), "User is banned", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure: " + t.getStackTrace());
                    }
                });

            }
        });}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_tab, container, false);
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
