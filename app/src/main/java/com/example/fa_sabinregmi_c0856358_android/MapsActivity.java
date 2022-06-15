package com.example.fa_sabinregmi_c0856358_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.fa_sabinregmi_c0856358_android.databinding.ActivityMapsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    FloatingActionButton fabAdd;

    MaterialButton btnSatelite;
    MaterialButton btnHybrid;
    MaterialButton btnTerrian;
    MaterialButton btnNone;

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void initialize() {
        fabAdd = findViewById(R.id.float_add);
        btnSatelite = findViewById(R.id.button_satelite);
        btnHybrid = findViewById(R.id.button_hybrid);
        btnTerrian = findViewById(R.id.button_terrain);
        btnNone = findViewById(R.id.button_none);

        btnSatelite.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btnHybrid.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID));
        btnTerrian.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN));
        btnNone.setOnClickListener(view -> mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL));
    }
}