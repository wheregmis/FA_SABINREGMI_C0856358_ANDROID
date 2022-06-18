package com.example.fa_sabinregmi_c0856358_android.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient mInstance;

    private ApplicationDatabase applicationDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        applicationDatabase = Room.databaseBuilder(context, ApplicationDatabase.class, "PlaceDB")
                // TODO: 18/06/2022 Allowing on main thread for now
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public ApplicationDatabase getApplicationDatabase() {
        return applicationDatabase;
    }
}
