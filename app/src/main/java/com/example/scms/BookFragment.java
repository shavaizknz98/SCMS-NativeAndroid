package com.example.scms;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.widget.Button;
import android.widget.Toast;

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
import com.google.android.material.behavior.SwipeDismissBehavior;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
    private String [] bikesInESBSlot;
    private String [] bikesInPHYSlot;
    private String [] bikesInEBSlot;
    private String [] bikesInSBASlot;
    private String [] bikesInMBSlot;
    private String [] bikesInSCSlot;

    private String currentstatus;

    public static final int REQ_CODE = 999;
    private MapView mapView;
    private GoogleMap gmap;

    private FloatingActionButton scanQRFAB;

    private static final String MAP_VIEW_BUNDLE_KEY = "AIzaSyBiMSFk-do4ySBxulASfk2wm2pik1Z-CSs";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

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

        prefs = getContext().getSharedPreferences(LoginTab.SCMS_PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();

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

    private String[] getSlotsAsArray(String bikeSlot){
        String [] result = bikeSlot.replace("]","").replace("[","").replace("\"", "").split(",");
        if(result[0] == ""){
            return null;
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            Log.d("AAAAA", "onActivityResult: scanresult" + scanResult + " scanresult.getcontents:" + scanResult.getContents());
            Toast.makeText(getContext(), scanResult.getContents(), Toast.LENGTH_LONG).show();
            startStopRide(scanResult.getContents());
        } else {
            Log.d("AAAAA", "onActivityResult: FAILED");
            Toast.makeText(getContext(), "FAILED", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    void startStopRide(final String scanresult) {
        if(scanresult == null || scanresult.isEmpty() || scanresult == "") { return; }

        final String userID = prefs.getString("useremail", "");
        Call<ResponseBody> call0 = RetrofitClient
                .getRetrofitClient()
                .getAPI()
                .getUserInfo(userID);
        call0.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String r = null;
                try {
                    r = response.body().string();
                } catch (IOException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }
                JSONObject resp = null;
                try {
                    resp = new JSONObject(r).getJSONObject("result");
                    if(resp.getString("current_ride").contains("NA")) {
                        currentstatus = "nobooking";
                    } else {
                        JSONArray hist = resp.getJSONArray("history");
                        int len = hist.length();
                        if(len != 0) {
                            JSONObject currentRide = hist.getJSONObject(len-1);
                            String currentridestatus = currentRide.getString("status");
                            if(currentridestatus.contains("reserved")) {
                                currentstatus = "reserved";
                            } else {
                                currentstatus = "inride";
                            }
                        }

                    }

                } catch (JSONException e) {
                    Log.d("AAAAA", "onResponse: " + e.getStackTrace());
                }

                Call<ResponseBody> call2 = null;

                if(currentstatus == "nobooking") {
                    Toast.makeText(getContext(), "You have not reserved a bike, please reserve one first!", Toast.LENGTH_SHORT).show();
                    return;
                } else if(currentstatus == "reserved") {
                    call2 = RetrofitClient
                            .getRetrofitClient()
                            .getAPI()
                            .startRide(userID, scanresult);

                } else if(currentstatus == "inride") {
                    call2 = RetrofitClient
                            .getRetrofitClient()
                            .getAPI()
                            .endRide(userID, scanresult);
                }

                call2.enqueue(new Callback<ResponseBody>() {
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

                        switch (status) {
                            case "0":
                                String resText = "";
                                if(currentstatus == "reserved") resText = "You have not reserved this bike. Please scan the correct QR code.";
                                else resText = "You have not started a ride.";
                                Toast.makeText(getContext(), resText, Toast.LENGTH_SHORT).show();
                                break;
                            case "1":
                                String resText2 = "";
                                if(currentstatus == "reserved") resText2 = "started";
                                else resText2 = "ended";
                                Toast.makeText(getContext(), "Successfully " + resText2 + " ride.", Toast.LENGTH_SHORT).show();
                                break;
                            case "2":
                                Toast.makeText(getContext(), "Server error, please try again.", Toast.LENGTH_SHORT).show();
                                break;
                            case "3":
                                Toast.makeText(getContext(), "The slot you are trying to scan is occupied. Please try another slot.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
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

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        updateStationArrays();
        //make request again here
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

        updateStationArrays();


    }
    public void markStationsWithAvailableBikes() {

        BitmapDescriptor marker = createScaledMarker();

        if(bikesInEBSlot !=  null){
            LatLng EB_Station= new LatLng(25.311577, 55.491686);
            MarkerOptions EB = new MarkerOptions()
                    .position(EB_Station)
                    .title("EB charging station")
                    .icon(marker)
                    .draggable(false);

            Marker EB_marker= gmap.addMarker(EB);
            String EB_tag = "EB";
            EB_marker.setTag(EB_tag);
        }
        if(bikesInESBSlot != null){
            LatLng ESB_Station= new LatLng(25.311997, 55.490324);
            MarkerOptions ESB = new MarkerOptions()
                    .position(ESB_Station)
                    .title("ESB charging station")
                    .icon(marker)
                    .draggable(false);

            Marker ESB_marker= gmap.addMarker(ESB);
            String ESB_tag = "ESB";
            ESB_marker.setTag(ESB_tag);
        }
        if(bikesInMBSlot != null){
            LatLng MB_Station= new LatLng(25.310396, 55.491005);
            MarkerOptions MB = new MarkerOptions()
                    .position(MB_Station)
                    .title("Main Building charging station")
                    .icon(marker)
                    .draggable(false);

            Marker MB_marker= gmap.addMarker(MB);
            String MB_tag = "MB";
            MB_marker.setTag(MB_tag);
        }
        if(bikesInPHYSlot != null){
            LatLng PHY_Station= new LatLng(25.308775, 55.490820);
            MarkerOptions PHY = new MarkerOptions()
                    .position(PHY_Station)
                    .title("Physics charging station")
                    .icon(marker)
                    .draggable(false);

            Marker PHY_marker= gmap.addMarker(PHY);
            String PHY_tag = "PHY";
            PHY_marker.setTag(PHY_tag);
        }
        if(bikesInSBASlot != null){
            LatLng SBA_Station= new LatLng(25.310990, 55.492696);
            MarkerOptions SBA = new MarkerOptions()
                    .position(SBA_Station)
                    .title("SBA charging station")
                    .icon(marker)
                    .draggable(false);

            Marker SBA_marker= gmap.addMarker(SBA);
            String SBA_tag = "SBA";
            SBA_marker.setTag(SBA_tag);
        }
        if(bikesInSCSlot != null){
            LatLng SC_Station= new LatLng(25.309409, 55.490132);
            MarkerOptions SC = new MarkerOptions()
                    .position(SC_Station)
                    .title("SC charging station")
                    .icon(marker)
                    .draggable(false);

            Marker SC_marker= gmap.addMarker(SC);
            String SC_tag = "SC";
            SC_marker.setTag(SC_tag);
        }

    }

    BitmapDescriptor createScaledMarker() {
        int height = 100;
        int width = 100;
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_directions_bike_blue_24dp);


        Bitmap bitmap1 = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        Bitmap newmarker = Bitmap.createScaledBitmap(bitmap1, width, height, false);
        BitmapDescriptor  newMarkerIcon = BitmapDescriptorFactory.fromBitmap(newmarker);
        return newMarkerIcon;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(getContext(), PopupBikeAvailabilityActivity.class);
        String loc = (String) marker.getTag();
        String [] availableBikes = null;
        Log.d("BookFragment", "onMarkerClick: " + loc + " clicked");
        intent.putExtra("loc", loc);
        if(loc == "ESB"){
            availableBikes = bikesInESBSlot;
        }else if(loc == "EB"){
            availableBikes = bikesInEBSlot;
        }else if(loc == "PHY"){
            availableBikes = bikesInPHYSlot;
        }else if (loc == "SBA"){
            availableBikes = bikesInSBASlot;
        }else if (loc == "SC"){
            availableBikes = bikesInSCSlot;
        }else if (loc == "MB"){
            availableBikes = bikesInMBSlot;
        }
        intent.putExtra("availableBikes", availableBikes);
        startActivity(intent);
        return false;
    }


    public void updateStationArrays() {
        Call<ResponseBody> call = RetrofitClient
                .getRetrofitClient()
                .getAPI()
                .getAvailableBikes("HI");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String r = null;
                try {
                    r = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject resp = null;
                try {
                    resp = new JSONObject(r);

                    String ESB  = resp.getString("ESB");
                    bikesInESBSlot = getSlotsAsArray(ESB);

                    String PHY = resp.getString("PHY");
                    bikesInPHYSlot = getSlotsAsArray(PHY);

                    String SBA = resp.getString("SBA");
                    bikesInSBASlot = getSlotsAsArray(SBA);

                    String EB = resp.getString("EB");
                    bikesInEBSlot = getSlotsAsArray(EB);

                    String MB = resp.getString("MB");
                    bikesInMBSlot = getSlotsAsArray(MB);

                    String SC = resp.getString("SC");
                    bikesInSCSlot = getSlotsAsArray(SC);

                    Toast.makeText(getContext(), r, Toast.LENGTH_SHORT).show();
                    markStationsWithAvailableBikes();
                    Log.d("AAAAA", "onResponse: calling getuserdata from bookfragment");
                } catch (JSONException e) {
                    Log.d("BookFragment", "onResponse: " + e.getStackTrace());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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
