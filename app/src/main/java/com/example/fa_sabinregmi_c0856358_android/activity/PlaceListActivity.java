package com.example.fa_sabinregmi_c0856358_android.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.fa_sabinregmi_c0856358_android.R;
import com.example.fa_sabinregmi_c0856358_android.database.DatabaseClient;
import com.example.fa_sabinregmi_c0856358_android.database.PlaceDao;
import com.example.fa_sabinregmi_c0856358_android.adapter.ListAdapter;
import com.example.fa_sabinregmi_c0856358_android.model.Place;
import com.example.fa_sabinregmi_c0856358_android.util.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;

public class PlaceListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ListAdapter listAdapter;

    List<Place> placeList = new ArrayList<>();
    private PlaceDao placeDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initializeToolbar();
        initialize();
    }

    void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bookmarked Places");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initialize() {
        recyclerView = findViewById(R.id.recycler_view_list);
        listAdapter = new ListAdapter(this, placeList);

        placeDao = DatabaseClient.getInstance(getApplicationContext()).getApplicationDatabaseDatabase().placeDao();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(listAdapter);


    }

    private void makeList() {
        placeList.clear();
        placeList.addAll(placeDao.getAllPlaces());

        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        makeList();
        enableSwipeToDeleteAndUndo();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                listAdapter.removeItem(position);

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}
