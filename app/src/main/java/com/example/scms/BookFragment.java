package com.example.scms;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int REQ_CODE = 999;
    private MapView mapView;
    private GoogleMap gmap;

    private FloatingActionButton scanQRFAB;

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyBiMSFk-do4ySBxulASfk2wm2pik1Z-CSs";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BookFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookFragment newInstance(String param1, String param2) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
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
        View v = inflater.inflate(R.layout.fragment_book, container, false);



        mapView = (MapView) v.findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        scanQRFAB = (FloatingActionButton) v.findViewById(R.id.scanQRFAB);
        scanQRFAB.setOnClickListener(this);

        Call<ResponseBody> call = RetrofitClient
                .getRetrofitClient()
                .getAPI()
                .getAvailableBikes("HI");


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String r = response.body().string();
                    Toast.makeText(getActivity(), "response: " + r, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Log.d("BookFragment", "onResponse: " + e.getStackTrace());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanQRFAB:
                AlertDialog.Builder builder = new AlertDialog.Builder((getContext()));//add 2nd parameter here R.style.theme
                builder.setTitle("Attention!");
                builder.setIcon(R.drawable.ic_warning_black_24dp);
                builder.setMessage("Please make sure you have reserved a bike before scanning.")
                        .setCancelable(true)
                        .setPositiveButton("I reserved a bike", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //ok
                                //request permissions here
                                Dexter.withActivity(getActivity())
                                        .withPermission(Manifest.permission.CAMERA)
                                        .withListener(new PermissionListener() {
                                            @Override
                                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                                //success
                                                Snackbar snackbar = Snackbar.make(getView(), "Have camera permissions", Snackbar.LENGTH_SHORT);
                                                snackbar.show();
                                                Intent intent = new Intent(getActivity(), QRCodeScannerActivity.class);
                                               // startActivityForResult(intent, REQ_CODE);//will be start activity for result to get scanned value
                                                //new IntentIntegrator(getActivity()).setOrientationLocked(false).setCaptureActivity(QRCodeScannerActivity.class).initiateScan();
                                               // new IntentIntegrator(getActivity()).initiateScan(); // `this` is the current Activity
                                                IntentIntegrator.forSupportFragment(BookFragment.this).setOrientationLocked(false).setCaptureActivity(QRCodeScannerActivity.class).initiateScan(); // `this` is the current Fragment


                                            }

                                            @Override
                                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                                Snackbar snackbar = Snackbar.make(getView(), "Permission for camera is needed to be able to scan the QR code!", Snackbar.LENGTH_LONG)
                                                        .setAction("Settings", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                                                intent.setData(uri);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                snackbar.show();
                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                                token.continuePermissionRequest();  //ask it again
                                                //if it is denied then it wont be asked again, so user can go enable it from the snackbar
                                            }
                                        })
                                        .check();
                            }
                        })
                        .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));  //make a colors entry
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPageBackground));
                alertDialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPageBackground));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(scanResult != null) {
            Log.d("AAAAA", "onActivityResult:" + scanResult);
            Toast.makeText(getContext(), scanResult.getContents(), Toast.LENGTH_LONG).show();
        } else {
            Log.d("AAAAA", "onActivityResult: FAILED");
            Toast.makeText(getContext(), "FAILED", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);

        }
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

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        //make reqquest again here
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setOnMarkerClickListener(this);
        gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //25.3144447,55.3625
        //25.312769, 55.492643

        LatLng AUSLatLng = new LatLng(25.312769, 55.492643);
        LatLng AUSNE = new LatLng(25.313475, 55.500086);
        LatLng AUSSW = new LatLng(25.308831, 55.483615);

        com.google.android.gms.maps.model.LatLng AUS_EB2 = new com.google.android.gms.maps.model.LatLng(25.311888, 55.491579);

        gmap.getUiSettings().setAllGesturesEnabled(false);
        gmap.getUiSettings().setZoomGesturesEnabled(true);
        gmap.getUiSettings().setScrollGesturesEnabled(true);
        LatLngBounds AUSBounds = new LatLngBounds(AUSSW, AUSNE);    //SE comes before NW for some godforsaken reason

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(AUS_EB2)      // Sets the center of the map to eb2
                .zoom(15.7f)                   // Sets the zoom
                .bearing(34)                //AUS is oriented straight
                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        gmap.setLatLngBoundsForCameraTarget(AUSBounds);
        gmap.setMinZoomPreference(15.7f);
        LatLng EB2_stand = new LatLng(25.311577, 55.491686);
        MarkerOptions EB2 = new MarkerOptions()
                            .position(EB2_stand)
                            .title("EB2 charging station")
                            .icon(generateBitmapDescriptorFromRes(getContext(), R.drawable.ic_directions_bike_blue_24dp))
                            .draggable(false);


        Marker EB2_marker = gmap.addMarker(EB2);
        String EB2_tag = "EB2";
        EB2_marker.setTag(EB2_tag);

        //gmap.moveCamera(CameraUpdateFactory.newLatLng(AUS));
        //gmap.getUiSettings().setScrollGesturesEnabled(false);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(getContext(), PopupBikeAvailabilityActivity.class);
        String loc = (String) marker.getTag();
        Log.d("BookFragment", "onMarkerClick: " + loc + " clicked");
        intent.putExtra("loc", loc);
        startActivity(intent);
        return false;
    }


    public static BitmapDescriptor generateBitmapDescriptorFromRes(
            Context context, int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        drawable.setBounds(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /*
    public static BitmapDescriptor scaleBitmap() {
        int width = 100;
        int height = 100;
        Bitmap b = BitmapFactory.decodeResource(, )
    }*/
    
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
