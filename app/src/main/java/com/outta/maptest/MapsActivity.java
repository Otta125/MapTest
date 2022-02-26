package com.outta.maptest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.outta.maptest.APIs.RetrofitClient;
import com.outta.maptest.Adapters.CustomInfoWindowAdapter;
import com.outta.maptest.Model.MapModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int PERMISSION_REQUEST_CODE = 0;
    private GoogleMap mMap;
    Context context;
    int counter = 10;
    TextView counterTxt,addressTxt;
    LocationManager locationManager;
    String provider;
    AlertDialog alert;
    String LAT, LONG, City, MapLat, MapLong;
    FrameLayout locateMe, openTraffic, changeMapType, refreshMap;
    Location location;
    boolean TRAFFIC_STATUS = false;
    int count = 0;
    CountDownTimer cTimer = null;

    //constant PERMISSION_REQUEST_CODE =0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mMap.setMyLocationEnabled(true);

                    } else {
                        statusCheck();
                    }


                }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        context = this;


        counterTxt = findViewById(R.id.counter_id);
        locateMe = findViewById(R.id.mylocation);
        openTraffic = findViewById(R.id.mytraffic);
        changeMapType = findViewById(R.id.maptype);
        refreshMap = findViewById(R.id.refresh);
        addressTxt = findViewById(R.id.address_id);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CountDown();
        GetPermessionFun();
        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
              //  mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16F));*/

                GetMyLocation();
            }


        });

        openTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TRAFFIC_STATUS) {
                    TRAFFIC_STATUS = false;

                } else {
                    TRAFFIC_STATUS = true;
                }
                mMap.setTrafficEnabled(TRAFFIC_STATUS);

            }
        });

        changeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if (count == 1) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    //Toast.makeText(context, "MAP_TYPE_SATELLITE",Toast.LENGTH_SHORT).show();
                }
                if (count == 2) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    // Toast.makeText(context, "MAP_TYPE_TERRAIN",Toast.LENGTH_SHORT).show();

                }
                if (count == 3) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    // Toast.makeText(context, "MAP_TYPE_HYBRID",Toast.LENGTH_SHORT).show();


                }
                if (count == 4) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    // Toast.makeText(context, "MAP_TYPE_NORMAL",Toast.LENGTH_SHORT).show();

                    count = 0;
                }
            }
        });

        refreshMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cTimer.cancel();
                counter = 10;
                counterTxt.setText("10");
                getMapApi();
                CountDown();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // statusCheck();
        if (alert != null) {
            alert.cancel();
        }
    }

    public void getMapApi() {
        Call<MapModel> call = RetrofitClient.getInstance().getApi().getVehicleData();
        call.enqueue(new Callback<MapModel>() {
            @Override
            public void onResponse(Call<MapModel> call, Response<MapModel> response) {

                Log.e("DATAA", response.body().getOfflineFrom());
                Log.e("Long: ", String.valueOf(response.body().getLongitude()));
                Log.e("Lat: ", String.valueOf(response.body().getLatitude()));
                //Toast.makeText(context, String.valueOf(response.body().getLatitude()), Toast.LENGTH_SHORT).show();

                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(response.body().getLatitude(), response.body().getLongitude());

                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
                mMap.setPadding(0, 0, 0, 200);
                LatLng myCoordinates = new LatLng(response.body().getLatitude(), response.body().getLongitude());
                List<Address> Myaddresses = getCityName(myCoordinates);
                Log.e("add", String.valueOf(Myaddresses));

                if (!Myaddresses.isEmpty()) {

                    City = Myaddresses.get(0).getAddressLine(0);
                    addressTxt.setText(String.valueOf(City));


                }else{
                    addressTxt.setVisibility(View.GONE);
                }

                if (response.body().getStatus() != null) {
                    if (response.body().getStatus().toLowerCase().equals("static")) {
                        mMap.addMarker(new MarkerOptions().position(sydney)
                                .title("KIA")
                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("car_blue", 150, 150)))
                                .snippet(
                                        "Status:" + response.body().getStatus() + "\n" +
                                                "Time:" + response.body().getUpdateTime() + "\n" +
                                                "Engine:" + response.body().getEngine() + "\n" +
                                                "Battery:" + response.body().getBattery()
                                ));
                    }
                    if (response.body().getStatus().toLowerCase().equals("moving") || response.body().getStatus().toLowerCase().equals("online")) {
                        mMap.addMarker(new MarkerOptions().position(sydney)
                                .title("KIA")
                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("car_green", 150, 150)))
                                .snippet(
                                        "Status:" + response.body().getStatus() + "\n" +
                                                "Time:" + response.body().getUpdateTime() + "\n" +
                                                "Engine:" + response.body().getEngine() + "\n" +
                                                "Battery:" + response.body().getBattery()
                                ));
                    }
                    if (response.body().getStatus().toLowerCase().equals("offline")) {
                        mMap.addMarker(new MarkerOptions().position(sydney)
                                .title("KIA")
                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("car_gry", 150, 150)))
                                .snippet(
                                        "Status:" + response.body().getStatus() + "\n" +
                                                "Time:" + response.body().getUpdateTime() + "\n" +
                                                "Engine:" + response.body().getEngine() + "\n" +
                                                "Battery:" + response.body().getBattery()
                                ));
                    }

                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                CameraPosition cameraPosition = new CameraPosition.Builder().
                        target(new LatLng(response.body().getLatitude(),
                                response.body().getLongitude())).zoom(10).build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                //   41.40338, 2.17403
            }

            @Override
            public void onFailure(Call<MapModel> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "There is problem , please check your connection", Toast.LENGTH_SHORT).show();
                Log.e("fail", t.getMessage());
/*                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(41.40338, 2.17403);

                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));
                mMap.addMarker(new MarkerOptions().position(sydney)
                        .title("")
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("cars", 150, 150)))
                        .snippet("No Data Found Please Try Again"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


                CameraPosition cameraPosition = new CameraPosition.Builder().
                        target(new LatLng(41.40338,
                                2.17403)).zoom(15).build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);*/
            }
        });
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void CountDown() {
        counter = 10;
        cTimer = new CountDownTimer(12000, 1000) {
            public void onTick(long millisUntilFinished) {
                counterTxt.setText(String.valueOf(counter));
                counter--;
            }

            public void onFinish() {
                counter = 10;
                counterTxt.setText("10");
                getMapApi();
                CountDown();

            }
        }.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getMapApi();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            GetPermessionFun();
            return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }


    //// Location Funtions

    public void GetMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        // Getting LocationManager object
        statusCheck();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {
            if (!provider.contains("gps")) { // if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
            // Get the location from the given provider
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, (LocationListener) this);

            if (location != null) onLocationChanged(location);
            else location = locationManager.getLastKnownLocation(provider);
            /////////////////////////////////////////////////
            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16F));
//                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
                statusCheck();
            }

        } else {
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
            statusCheck();
        }
    }


    public void GetPermessionFun() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        // Getting LocationManager object
        statusCheck();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {
            if (!provider.contains("gps")) { // if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
            // Get the location from the given provider
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, (LocationListener) this);

            if (location != null) onLocationChanged(location);
            else location = locationManager.getLastKnownLocation(provider);
            /////////////////////////////////////////////////
            if (location != null) {
                onLocationChanged(location);
                LAT = String.valueOf(location.getLatitude());
                LONG = String.valueOf(location.getLongitude());
                LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
                List<Address> Myaddresses = getCityName(myCoordinates);
                Log.e("add", String.valueOf(Myaddresses));

                if (!Myaddresses.isEmpty()) {

                    City = Myaddresses.get(0).getAddressLine(0);
                   // addressTxt.setText(String.valueOf(City));


                }else{
                   // addressTxt.setVisibility(View.GONE);
                }
//                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
                statusCheck();
            }

        } else {
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
            statusCheck();
        }

    }

    private List<Address> getCityName(LatLng myCoordinates) {
        String myCity = "";
        List<Address> Myaddresses = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            Myaddresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            if (!Myaddresses.isEmpty()) {
                String address = Myaddresses.get(0).getAddressLine(0);
            }
            Myaddresses.addAll(Myaddresses);
            if (!Myaddresses.isEmpty()) {
                myCity = Myaddresses.get(0).getSubAdminArea();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Myaddresses;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();


        } else {

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}