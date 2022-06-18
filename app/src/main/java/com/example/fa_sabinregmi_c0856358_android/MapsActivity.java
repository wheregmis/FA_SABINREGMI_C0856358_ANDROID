package com.example.fa_sabinregmi_c0856358_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.fa_sabinregmi_c0856358_android.activity.AddNewPlace;
import com.example.fa_sabinregmi_c0856358_android.activity.PlaceList;
import com.example.fa_sabinregmi_c0856358_android.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    FloatingActionButton fabAdd, fabList;

    MaterialButton btnSatelite;
    MaterialButton btnHybrid;
    MaterialButton btnTerrian;
    MaterialButton btnNone;

    LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initialize();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Map View");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        checkPermissionAndEnableLocation();

        mMap.setOnMapLongClickListener(latLng -> {
            Toast.makeText(MapsActivity.this, "Add Location to list", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), AddNewPlace.class);
            intent.putExtra("long", latLng.longitude);
            intent.putExtra("lan", latLng.latitude);
            startActivity(intent);
        });
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkPermissionAndEnableLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            //TODO
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
    }

    private void initialize() {
        fabAdd = findViewById(R.id.float_add);
        fabList = findViewById(R.id.fabList);
        btnSatelite = findViewById(R.id.button_satelite);
        btnHybrid = findViewById(R.id.button_hybrid);
        btnTerrian = findViewById(R.id.button_terrain);
        btnNone = findViewById(R.id.button_none);

        fabAdd.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddNewPlace.class)));
        fabList.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), PlaceList.class)));

        btnSatelite.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btnHybrid.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
        btnTerrian.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        btnNone.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissionAndEnableLocation();
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request
        mLocationRequest = LocationRequest.create()
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
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11));

//
//                                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
//                                // and once again when the user makes a selection (for example when calling fetchPlace()).
//                                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
//
//                                // Create a RectangularBounds object.
//                                RectangularBounds bounds = RectangularBounds.newInstance(
//                                        new LatLng(-33.880490, 151.184363),
//                                        new LatLng(-33.858754, 151.229596));
//                                // Use the builder to create a FindAutocompletePredictionsRequest.
//                                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                                        // Call either setLocationBias() OR setLocationRestriction().
//                                        .setLocationBias(bounds)
//                                        //.setLocationRestriction(bounds)
//                                        .setOrigin(new LatLng(-33.8749937,151.2041382))
//                                        .setCountries("AU", "NZ")
//                                        .setTypeFilter(TypeFilter.ADDRESS)
//                                        .setSessionToken(token)
//                                        .build();
//
//                                PlacesClient placesClient = new PlacesClient() {
//                                    @NonNull
//                                    @Override
//                                    public Task<FindAutocompletePredictionsResponse> findAutocompletePredictions(@NonNull FindAutocompletePredictionsRequest findAutocompletePredictionsRequest) {
//                                        return null;
//                                    }
//
//                                    @NonNull
//                                    @Override
//                                    public Task<FetchPhotoResponse> fetchPhoto(@NonNull FetchPhotoRequest fetchPhotoRequest) {
//                                        return null;
//                                    }
//
//                                    @NonNull
//                                    @Override
//                                    public Task<FetchPlaceResponse> fetchPlace(@NonNull FetchPlaceRequest fetchPlaceRequest) {
//                                        return null;
//                                    }
//
//                                    @NonNull
//                                    @Override
//                                    public Task<FindCurrentPlaceResponse> findCurrentPlace(@NonNull FindCurrentPlaceRequest findCurrentPlaceRequest) {
//                                        return null;
//                                    }
//                                };
//                                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
//                                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                                        Log.i("PlacesAPI", prediction.getPlaceId());
//                                        Log.i("PlacesAPI", prediction.getPrimaryText(null).toString());
//                                    }
//                                }).addOnFailureListener((exception) -> {
//                                    if (exception instanceof ApiException) {
//                                        ApiException apiException = (ApiException) exception;
//                                        Log.e("PlacesAPI", "Place not found: " + apiException.getStatusCode());
//                                    }
//                                });


                                fusedLocationClient.removeLocationUpdates(mLocationCallback);
                            });

                        }
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());

    }
}