package com.nabarup.avator.login;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.nabarup.avator.BuildConfig;

public class FirebaseManager {
    public static void init(Context context) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.APPLICATION_ID)
                .setApiKey("AIzaSyDcfg6H13d8ecgaxWexx3RRsJFm8Ir0qH4")
                .setDatabaseUrl("https://avator-dd16f-default-rtdb.firebaseio.com/")
                .setProjectId("avator-dd16f")
                .build();

        FirebaseApp.initializeApp(context, options, "com.nabarup.avator");
    }
}


