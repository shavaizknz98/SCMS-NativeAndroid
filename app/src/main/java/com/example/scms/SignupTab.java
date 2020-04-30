package com.example.scms;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;
import com.google.android.material.switchmaterial.SwitchMaterial;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupTab.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupTab extends Fragment implements BiometricCallback, View.OnClickListener/*, View.OnFocusChangeListener*/ {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button signUpButton;
    private EditText emailAddrEditText;
    private EditText phoneNumberEditText;
    private EditText nameEditText;
    //private EditText IDEditText;
    private EditText passwordEditText;
    private SwitchMaterial switchMaterial;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    BiometricManager mBiometricManager;


    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressBar progressBar;

    private OnFragmentInteractionListener mListener;

    private Pattern emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");


    public SignupTab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupTab.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupTab newInstance(String param1, String param2) {
        SignupTab fragment = new SignupTab();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_tab, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpButton = view.findViewById(R.id.signUpButton);
        emailAddrEditText = view.findViewById(R.id.emailAddrEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        //IDEditText = view.findViewById(R.id.idEditText);
        switchMaterial = view.findViewById(R.id.switchMaterial);

        //TODO email regex
        /*
        emailAddrEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && !emailAddrEditText.getText().toString().isEmpty()) {
                    String emailText = emailAddrEditText.getText().toString();
                    Matcher m = emailPattern.matcher(emailText);
                    if(m.matches()) {
                        Toast.makeText(getActivity(), "Email matches", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Email does not match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        */

        emailAddrEditText.setText("ahmedhamza199@gmail.com");
        phoneNumberEditText.setText("123456790");
        nameEditText.setText("Ahmed Hamza");
        passwordEditText.setText("Becooler-98");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO save biometric login choice to sharedprefs so that in the sign in screen it can be used instead of email/pw

                /*if(switchMaterial.isChecked()) {
                    mBiometricManager = new BiometricManager.BiometricBuilder(getContext()).setTitle("Use Biometric Sign In")
                            .setSubtitle("")
                            .setDescription("Please Sign In Using Biometrics")
                            .setNegativeButtonText("Cancel")
                            .build();

                    //start authentication
                    mBiometricManager.authenticate(SignupTab.this);
                }
                else{
                    Intent toNavigationActivity = new Intent(getActivity(), NavigationActivity.class);
                    startActivity(toNavigationActivity);
                    getActivity().finish();
                }*/

                String email, phoneNumber, fullName, password;

                if(emailAddrEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "You must enter your email!", Toast.LENGTH_LONG).show();
                    emailAddrEditText.requestFocus();
                    return;
                }

                if(phoneNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "You must enter your phone number!", Toast.LENGTH_LONG).show();
                    phoneNumberEditText.requestFocus();
                    return;
                }
                    //TODO add phone regex?


                if(nameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "You must enter your name!", Toast.LENGTH_LONG).show();
                    nameEditText.requestFocus();
                    return;
                }

                if(passwordEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "You must enter a password!", Toast.LENGTH_LONG).show();
                    passwordEditText.requestFocus();
                    return;
                }
                    //TODO add hashing
                    //TODO add minimum length check



                email = emailAddrEditText.getText().toString().toLowerCase().trim();
                phoneNumber = phoneNumberEditText.getText().toString().trim();
                fullName = nameEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();



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
                                Log.d(TAG, "onResponse: status = " + code);
                                Toast.makeText(getActivity(), "status = " + code, Toast.LENGTH_SHORT).show();
                            } catch(JSONException e) {
                                Log.e(TAG, "onResponse: " + e.toString());
                            }

                            if(code == 1) {
                                Intent toNavigationActivity = new Intent(getContext(), NavigationActivity.class);

                                startActivity(toNavigationActivity);
                                getActivity().finish();
                            } else if(code == 0){
                                Log.d(TAG, "onResponse: user exists");
                            } else if(code == 2) {
                                Log.d(TAG, "onResponse: server error");
                            } else {
                                Log.d(TAG, "onResponse: user error");
                            }



                        } catch (IOException e) {
                            Log.e(TAG, "onResponse: " + e.getStackTrace());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure: " + t.getStackTrace());
                    }
                });
            }
        });
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

    @Override
    public void onSdkVersionNotSupported() {
        Toast.makeText(getContext(), getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBiometricAuthenticationNotSupported() {
        Toast.makeText(getContext(), getString(R.string.biometric_error_hardware_not_supported), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {
        Toast.makeText(getContext(), getString(R.string.biometric_error_fingerprint_not_available), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(getContext(), getString(R.string.biometric_error_permission_not_granted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
//        Toast.makeText(getApplicationContext(), getString(R.string.biometric_failure), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationCancelled() {
        Toast.makeText(getContext(), getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show();
        //mBiometricManager.cancelAuthentication();

    }

    @Override
    public void onAuthenticationSuccessful() {
        Toast.makeText(getContext(), getString(R.string.biometric_success), Toast.LENGTH_LONG).show();
        Intent toNavigationActivity = new Intent(getActivity(), NavigationActivity.class);
        startActivity(toNavigationActivity);
        getActivity().finish();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//        Toast.makeText(getApplicationContext(), helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
//        Toast.makeText(getApplicationContext(), errString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

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
