package com.example.fa_sabinregmi_c0856358_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class EditPlace extends AppCompatActivity implements OnMapReadyCallback {
    // defining variables
    EditText etName;
    MaterialButton btnAdd;

    CheckBox cvFav;
    CheckBox cvComp;

    private GoogleMap mMap = null;

    // initializing Place Model
    Place place = null;
    private PlaceDao placeDao;

    LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // getting place model from database when id was placed
        placeDao = DatabaseClient.getInstance(getApplicationContext()).getApplicationDatabase().placeDao();

        place = placeDao.getProductById(getIntent().getIntExtra("id", 0));
        initialize();
    }

    // method to initialize toolbar
    void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(place.getName());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressLint("MissingPermission")
    public void checkPermissionAndEnableLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            //TODO
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
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

        btnAdd.setText("Update Location");

        etName.setText(place.getName());
        cvFav.setChecked(place.getFav());
        cvComp.setChecked(place.getVisited());

        // setting up map
        setUpMap();

        // setting up click listener to add button
        btnAdd.setOnClickListener(view -> {

            if (!etName.getText().toString().contentEquals("")) {

                // getting place dao
                PlaceDao placeDao = DatabaseClient.getInstance(getApplicationContext()).getApplicationDatabase().placeDao();

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
                    // updating place in database
                    placeDao.update(place);



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

        checkPermissionAndEnableLocation();

        LatLng placeLatLng = new LatLng(place.getLatitude(), place.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(placeLatLng)
                .title(place.getName())).setDraggable(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 11));

        // setting up click listener to place marker in mapview
        mMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            LatLng current1 = new LatLng(latLng.latitude, latLng.longitude);
            place.setLatitude((float) current1.latitude);
            place.setLongitude((float) current1.longitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(current1)
                    .title(place.getName())).setDraggable(true);
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                googleMap.clear();
                LatLng current1 = new LatLng(marker.getPosition().latitude, marker.getPosition().latitude);
                place.setLatitude((float) current1.latitude);
                place.setLongitude((float) current1.longitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(current1)
                        .title(place.getName())).setDraggable(true);
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }
        });

    }

    // method to handle permission and update user location
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
                                checkPermissionAndEnableLocation();
                                LatLng source = new LatLng(location.getLatitude(), location.getLongitude());
                                drawLine(source, new LatLng(place.getLatitude(), place.getLongitude()));

                                fusedLocationClient.removeLocationUpdates(mLocationCallback);
                            });

                        }
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());

    }

    // method to draw line between place location and user location
    private void drawLine(LatLng source, LatLng destination) {
        PolylineOptions options1 = new PolylineOptions()
                .color(Color.BLUE)
                .width(10)
                .add(source, destination);
        options1.clickable(true);
        options1.zIndex(2F);
        Polyline p = mMap.addPolyline(options1);
        List<LatLng> test = p.getPoints();
        float[] results = new float[1];
        Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                test.get(1).latitude, test.get(1).longitude,
                results);

        // calculate midpoint of polyline
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < p.getPoints().size() ; i++)
        {
            builder.include(p.getPoints().get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        setMarkerInCoordinate(centerLatLng, results[0]);


    }

    // method to set marker in the co ordinate
    private void setMarkerInCoordinate(LatLng latLng, Float distance){
        Log.i("MapsActivity", "setMarkerInCoordinate: "+latLng);
        DecimalFormat df = new DecimalFormat("#.##");
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title("")
                .icon(createPureTextIcon(String.valueOf(df.format(distance/1000))+" KM"));
        mMap.addMarker(options);
    }

    // method to create icon from string
    public BitmapDescriptor createPureTextIcon(String text) {

        Paint textPaint = new Paint(); // Adapt to your needs
        textPaint.setTextSize(50);
        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        int width = (int) (textWidth);
        int height = (int) (textHeight);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, height);

        // For development only:
        // Set a background in order to see the
        // full size and positioning of the bitmap.
        // Remove that for a fully transparent icon.
        canvas.drawColor(Color.LTGRAY);

        canvas.drawText(text, 0, 0, textPaint);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }
}
