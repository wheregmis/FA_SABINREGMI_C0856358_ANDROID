package com.example.fa_sabinregmi_c0856358_android.activity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fa_sabinregmi_c0856358_android.R;
import com.example.fa_sabinregmi_c0856358_android.database.DatabaseClient;
import com.example.fa_sabinregmi_c0856358_android.database.PlaceDao;
import com.example.fa_sabinregmi_c0856358_android.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class EditPlace extends AppCompatActivity implements OnMapReadyCallback {
    EditText etName;
    MaterialButton btnAdd;

    Marker placeMarker;
    CheckBox cvFav;
    CheckBox cvComp;

    private GoogleMap mMap = null;

    Place place = null;
    private PlaceDao placeDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        placeDao = DatabaseClient.getInstance(getApplicationContext()).getApplicationDatabase().placeDao();

        place = placeDao.getProductById(getIntent().getIntExtra("id", 0));
        initialize();
    }

    void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(place.getName());

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
        initializeToolbar();

        etName = findViewById(R.id.edit_name);
        btnAdd = findViewById(R.id.button_add);
        cvFav = findViewById(R.id.cv_isFav);
        cvComp = findViewById(R.id.cv_isCompleted);

        btnAdd.setText("Update Location");

        etName.setText(place.getName());
        cvFav.setChecked(place.getFav());
        cvComp.setChecked(place.getVisited());

        setUpMap();

        btnAdd.setOnClickListener(view -> {
            if (!etName.getText().toString().contentEquals("")) {

                PlaceDao placeDao = DatabaseClient.getInstance(getApplicationContext()).getApplicationDatabase().placeDao();

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

    void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng placeLatLng = new LatLng(place.getLatitude(), place.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(placeLatLng)
                .title(place.getName())).setDraggable(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 12));

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
}
