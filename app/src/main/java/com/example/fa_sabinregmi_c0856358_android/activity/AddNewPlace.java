package com.example.fa_sabinregmi_c0856358_android.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.fa_sabinregmi_c0856358_android.R;
import com.example.fa_sabinregmi_c0856358_android.database.DatabaseClient;
import com.example.fa_sabinregmi_c0856358_android.database.PlaceDao;
import com.example.fa_sabinregmi_c0856358_android.model.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class AddNewPlace extends AppCompatActivity implements OnMapReadyCallback {

    // defining variables
    EditText etName;
    MaterialButton btnAdd;

    CheckBox cvFav;
    CheckBox cvComp;

    private GoogleMap mMap = null;

    // initializing Place Model
    Place place = new Place();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //calling initialize method to initialize layout components
        initialize();
    }

    // method to initialize toolbar
    void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Place");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void initialize() {

        // initializing toolbar
        initializeToolbar();

        // binding components with variables
        etName = findViewById(R.id.edit_name);
        btnAdd = findViewById(R.id.button_add);
        cvFav = findViewById(R.id.cv_isFav);
        cvComp = findViewById(R.id.cv_isCompleted);

        // setting up map
        setUpMap();

        // setting up click listener to add button
        btnAdd.setOnClickListener(view -> {
            if (!etName.getText().toString().contentEquals("")) {

                // getting place dao
                PlaceDao placeDao = DatabaseClient.getInstance(getApplicationContext()).getApplicationDatabase().placeDao();

                // validating place name
                for (Place place1 : placeDao.getAllPlaces()) {
                    if (place1.getName().contentEquals(etName.getText().toString())) {
                        Toast.makeText(this, "Cannot use the same name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // checking lat long if its null or not
                if (place.getLatitude() != null && place.getLatitude() != null) {
                    place.setDate(String.valueOf(Calendar.getInstance().getTime()));
                    place.setName(etName.getText().toString());

                    if (cvComp.isChecked()) {
                        place.setVisited(true);
                    } else {
                        place.setVisited(false);
                    }

                    if (cvFav.isChecked()) {
                        place.setFav(true);
                    } else {
                        place.setFav(false);
                    }

                    // inserting place in database
                    placeDao.insert(place);

                    Toast.makeText(this, "Place has been added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "No location has been selected", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Add a Title to your location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // method to set up map fragment
    void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        enableLocation();

        // if intent have values (opened from long press on map)
        if (getIntent() != null) {
            LatLng selectedLatLng = new LatLng(getIntent().getDoubleExtra("lan", -34), getIntent().getDoubleExtra("long", -151));
            mMap.addMarker(new MarkerOptions()
                    .position(selectedLatLng)
                    .title("Selected Location")).setDraggable(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 12));
        } else {
            // if intent doesnt have value
            LatLng selectedLatLng = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions()
                    .position(selectedLatLng)
                    .title("Selected Location")).setDraggable(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 12));
        }


        // setting up click listener to place marker in mapview
        mMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            LatLng current1 = new LatLng(latLng.latitude, latLng.longitude);
            place.setLatitude((float) current1.latitude);
            place.setLongitude((float) current1.longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(current1)
                    .title("Selected Location"));
        });
    }

    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void enableLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            //TODO
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    LocationRequest mLocationRequest;
    private double latitude, longitude;


    // method to handle permission and update user location
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(2000);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        final LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (fusedLocationClient != null) {
                        if (location != null) {
                            mMap.setOnMapLoadedCallback(() -> {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                fusedLocationClient.removeLocationUpdates(mLocationCallback);
                            });

                        }
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());

    }

}

